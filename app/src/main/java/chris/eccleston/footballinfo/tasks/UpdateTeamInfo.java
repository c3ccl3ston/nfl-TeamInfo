package chris.eccleston.footballinfo.tasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

import chris.eccleston.footballinfo.fragments.FragmentTeamInfo;
import chris.eccleston.footballinfo.types.Team;

public class UpdateTeamInfo extends AsyncTask<Team, Void, Void> {

    private final String baseURL = "http://en.wikipedia.org/wiki/";
    private final String[] team_info_urls = {"Arizona_Cardinals", "Atlanta_Falcons", "Baltimore_Ravens", "Buffalo_Bills",
            "Carolina_Panthers", "Chicago_Bears", "Cincinnati_Bengals", "Cleveland_Browns", "Dallas_Cowboys", "Denver_Broncos",
            "Detroit_Lions", "Green_Bay_Packers", "Houston_Texans", "Indianapolis_Colts", "Jacksonville_Jaguars", "Kansas_City_Chiefs",
            "Miami_Dolphins", "Minnesota_Vikings", "New_England_Patriots", "New_Orleans_Saints", "New_York_Giants", "New_York_Jets",
            "Oakland_Raiders", "Philadelphia_Eagles", "Pittsburgh_Steelers", "San_Diego_Chargers", "Seattle_Seahawks", "San_Francisco_49ers",
            "Los_Angeles_Rams", "Tampa_Bay_Buccaneers", "Tennessee_Titans", "Washington_Redskins"};

    private Team s_team;
    private Context mContext;
    private boolean singleTeam;

    public UpdateTeamInfo(Context context) {
        mContext = context;
    }

    public void setSingleTeam(boolean isSingleTeam) {
        singleTeam = isSingleTeam;
    }

    @Override
    protected void onPreExecute() {
    }

    /**
     * The system calls this to perform work in a worker thread and delivers
     * it the parameters given to AsyncTask.execute()
     *
     * @return
     */
    protected Void doInBackground(Team... params) {
        String info_box_text = "";

        for (int i = 0; i < params.length; i++) {

            s_team = Team.find(Team.class, "team_id = ?", String.valueOf(params[i].getTeamId())).get(0);

            File file = new File(mContext.getFilesDir(), s_team.getTeamName().toLowerCase(Locale.ENGLISH) + "_team_info.html");

            try {
                if (isOnline()) {
                    FileOutputStream out = new FileOutputStream(file);
                    Connection con = Jsoup.connect(baseURL + team_info_urls[s_team.getTeamId()]).userAgent("Mozilla").timeout(10000);
                    Document doc = con.get();

                    Element table = doc.select("table[class=infobox]").first();

                    table.select("tr").first().remove();
                    table.select("tr").first().remove();

                    info_box_text = table.outerHtml().toString();
                    info_box_text = info_box_text.replaceAll("width:.*em;", "");
                    info_box_text = info_box_text.replaceAll("width:300px", "width:100%");
                    info_box_text = info_box_text.replaceAll("<a.*?href=\".*?>", "");
                    info_box_text = info_box_text.replaceAll("</a>", "");
                    info_box_text = info_box_text.replaceAll("<sup .*?>.*?</sup>", "");
                    info_box_text = info_box_text.replaceAll("src=\"", "src=\"https:");
                    info_box_text = info_box_text.replaceAll("srcset.*?\"", "");
                    info_box_text += "\n" + "<style> body { width:100%;margin:0; } </style>";

                    out.write(info_box_text.getBytes());
                    out.close();
                }
            } catch(Exception e) {

            }
        }
        return null;
    }

    /**
     * The system calls this to perform work in the UI thread and delivers
     * the result from doInBackground()
     *
     * @return
     */
    @Override
    protected void onPostExecute(Void result) {
        if (singleTeam) {
            FragmentTeamInfo.webView.loadUrl("file:///" + mContext.getFilesDir().getAbsolutePath() + "/" + s_team.getTeamName().toLowerCase(Locale.ENGLISH) + "_team_info.html");
            FragmentTeamInfo.refreshTeamInfo.setRefreshing(false);
        } else {
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}