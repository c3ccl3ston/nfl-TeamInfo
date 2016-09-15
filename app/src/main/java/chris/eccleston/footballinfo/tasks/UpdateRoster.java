package chris.eccleston.footballinfo.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import chris.eccleston.footballinfo.fragments.FragmentTeamRoster;
import chris.eccleston.footballinfo.types.Player;
import chris.eccleston.footballinfo.types.Team;

/**
 * Created by Chris on 2/9/2015.
 */
public class UpdateRoster extends AsyncTask<Team, Integer, Void> {
    private final String baseURL = "http://espn.go.com/nfl/team/roster/_/name/";
    private final String[] team_roster_urls = {"ari/arizona-cardinals", "atl/atlanta-falcons", "bal/baltimore-ravens", "buf/buffalo-bills",
            "car/carolina-panthers", "chi/chicago-bears", "cin/cincinnati-bengals", "cle/cleveland-browns", "dal/dallas-cowboys", "den/denver-broncos",
            "det/detroit-lions", "gb/green-bay-packers", "hou/houston-texans", "ind/indianapolis-colts", "jax/jacksonville-jaguars", "kc/kansas-city-chiefs",
            "mia/miami-dolphins", "min/minnesota-vikings", "ne/new-england-patriots", "no/new-orleans-saints", "nyg/new-york-giants", "nyj/new-york-jets",
            "oak/oakland-raiders", "phi/philadelphia-eagles", "pit/pittsburgh-steelers", "sd/san-diego-chargers", "sea/seattle-seahawks", "sf/san-francisco-49ers",
            "la/los-angeles-rams", "tb/tampa-bay-buccaneers", "ten/tennessee-titans", "wsh/washington-redskins"};
    public ProgressDialog progress_dialog;
    public int mSortOrder;
    private boolean singleTeam;
    private Team s_team;
    private Context mContext;

    public UpdateRoster(Context context) {
        mContext = context;
    }

    public void setSortOrder(int sortOrder) {
        mSortOrder = sortOrder;
    }

    public void setSingleTeam(boolean isSingleTeam) {
        singleTeam = isSingleTeam;
    }

    @Override
    protected void onPreExecute() {

    }

    protected Void doInBackground(Team... params) {
        for (int i = 0; i < params.length; i++) {
            if (!singleTeam) {
                double progress = 100.0 * (i / 32.0);
                publishProgress((int) progress);
            }

            s_team = params[i];

            List<Player> current_players = Player.find(Player.class, "team_id = ?", String.valueOf(s_team.getTeamId()));
            List<Integer> current_roster = new ArrayList<Integer>();
            List<Integer> new_roster = new ArrayList<Integer>();

            for (Player p : current_players) {
                current_roster.add(p.getPlayerId());
            }

            try {
                if (isOnline()) {
                    Connection con = Jsoup.connect(baseURL + team_roster_urls[s_team.getTeamId()]).userAgent("Mozilla").timeout(10 * 1000);
                    Document doc = con.get();

                    Elements players = doc.select("tr[class~=player]");

                    for (Element player : players) {
                        String number;
                        String full_name;
                        String first_name;
                        String last_name;
                        String position;
                        String exp;
                        String college;
                        String link;
                        int player_id;

                        number = player.child(0).text();
                        full_name = player.child(1).text();

                        if (full_name.indexOf(' ') == full_name.lastIndexOf(' ')) {
                            first_name = full_name.substring(0, full_name.indexOf(' '));
                            last_name = full_name.substring(full_name.indexOf(' ') + 1);
                        } else {
                            if (full_name.substring(full_name.lastIndexOf(' ') + 1).contains("Jr.") || full_name.substring(full_name.lastIndexOf(' ') + 1).contains("Sr.") ||
                                    full_name.substring(full_name.lastIndexOf(' ') + 1).contains("III") || full_name.substring(full_name.lastIndexOf(' ') + 1).contains("II")) {
                                first_name = full_name.substring(0, full_name.indexOf(' '));
                                last_name = full_name.substring(full_name.indexOf(' ') + 1);
                            } else {
                                first_name = full_name.substring(0, full_name.indexOf(' ', full_name.indexOf(' ') + 1));
                                last_name = full_name.substring(full_name.indexOf(' ', full_name.indexOf(' ') + 1) + 1);
                            }
                        }

                        position = player.child(2).text();
                        exp = player.child(6).text();
                        college = player.child(7).text();
                        link = player.child(1).child(0).attr("href");
                        player_id = Integer.parseInt(link.substring(link.indexOf("id/") + 3, link.lastIndexOf('/')));

                        if (number.equals("")) {
                            number = "00";
                        }

                        full_name = full_name.replaceAll("\\s[A-Z]{1}.\\s", " ");

                        Player existing_player = null;
                        try {
                            existing_player = Player.find(Player.class, "player_id = ?", String.valueOf(player_id)).get(0);
                        } catch (Exception e) {

                        }

                        if (existing_player != null) {
                            existing_player.updatePlayer(number, last_name, first_name, position, exp, college, link, s_team.getTeamId());
                        } else {
                            Player p = new Player(player_id, number, last_name, first_name, position, exp, college, link, s_team.getTeamId());
                            p.save();
                        }
                        new_roster.add(player_id);
                    }

                    for (int id : current_roster) {
                        if (current_roster.contains(id) && !new_roster.contains(id)) {
                            Player.deleteAll(Player.class, "player_id = ?", String.valueOf(id));
                        }
                    }
                }
            } catch (IOException e1) {
            }

        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progress_dialog.setProgress(values[0]);
    }

    /**
     * The system calls this to perform work in the UI thread and delivers
     * the result from doInBackground()
     */
    protected void onPostExecute(Void result) {
        if (singleTeam) {
            List<Player> new_roster = Player.find(Player.class, "team_id = ?", String.valueOf(s_team.getTeamId()));

            switch (mSortOrder) {
                case 1:
                    Collections.sort(new_roster, new Comparator<Player>() {
                        public int compare(Player s1, Player s2) {
                            String s1_number = s1.getNumber();
                            String s2_number = s2.getNumber();
                            int num_one, num_two;
                            if (s1_number.equals("--")) {
                                num_one = 0;
                            } else {
                                num_one = Integer.parseInt(s1_number);
                            }
                            if (s2_number.equals("--")) {
                                num_two = 0;
                            } else {
                                num_two = Integer.parseInt(s2_number);
                            }
                            if (num_one == num_two) {
                                return s1.getLastName().compareToIgnoreCase(s2.getLastName());
                            } else if (num_one < num_two) {
                                return -1;
                            } else {
                                return 1;
                            }
                        }
                    });
                    break;
                case 2:
                    Collections.sort(new_roster, new Comparator<Player>() {
                        public int compare(Player s1, Player s2) {
                            return s1.getPosition().compareToIgnoreCase(s2.getPosition());
                        }
                    });
                    break;
                case 0:
                default:
                    Collections.sort(new_roster, new Comparator<Player>() {
                        public int compare(Player s1, Player s2) {
                            return s1.getLastName().compareToIgnoreCase(s2.getLastName());
                        }
                    });

            }
            FragmentTeamRoster.ca.updateList(new_roster);
            FragmentTeamRoster.ca.notifyDataSetChanged();
            FragmentTeamRoster.refreshRosterList.setRefreshing(false);
        } else {
            progress_dialog.dismiss();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}