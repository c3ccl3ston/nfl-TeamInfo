package chris.eccleston.footballinfo.tasks;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.activities.TeamActivity;
import chris.eccleston.footballinfo.types.Player;
import chris.eccleston.footballinfo.types.Team;

/**
 * Created by Chris on 8/13/2015.
 */
public class GetPlayerInfo extends AsyncTask<String, Void, String[]> {
    static Bitmap mIcon_val = null;
    static boolean hasStats = false;

    public static ProgressDialog progress;

    static Context mContext;
    static Team mTeam;
    static Player mPlayer;

    static String[] mResult;

    public static Dialog dialog;

    public GetPlayerInfo(Context context, Team team, Player player) {
        mContext = context;
        mTeam = team;
        mPlayer = player;
    }

    public GetPlayerInfo getPlayerInfo() {
        return this;
    }

    public void showProgress(String text) {
        progress = new ProgressDialog(mContext);
        progress.setCancelable(true);
        progress.setCanceledOnTouchOutside(true);

        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface pd) {
                getPlayerInfo().cancel(true);
            }
        });

        progress.show();
        progress.setContentView(R.layout.progress);

        TextView status = (TextView) progress.findViewById(R.id.progressText);
        status.setText(text);
    }

    @Override
    protected void onPreExecute() {
        showProgress("Loading player info...");
    }

    @Override
    protected String[] doInBackground(String... params) {
        String[] player_information = new String[21];
        try {
            /**
             * Player Info
             */
            Document doc = Jsoup.connect(params[0]).userAgent("Mozilla")
                    .timeout(10 * 1000).get();
            Elements player_profile = doc.select("div[class=mod-content]");

            String player_height = player_profile
                    .select("ul[class=general-info]").get(0).child(1)
                    .text().split(",")[0];
            String player_weight = player_profile
                    .select("ul[class=general-info]").get(0).child(1)
                    .text().split(",")[1].trim();
            String player_born = player_profile
                    .select("ul[class~=player-metadata]").select("li")
                    .get(0).text().replaceAll("Born", "");
            int birth_date_indx = player_born.indexOf("in");
            if (birth_date_indx == -1) {
                birth_date_indx = player_born.indexOf("(");
            }
            int age_indx = player_born.indexOf("Age:");
            String player_age = player_born.substring(age_indx + 5,
                    age_indx + 7);
            player_born = player_born.substring(0, birth_date_indx - 1);

            String player_picture = doc.select("div[class=main-headshot]")
                    .select("img").attr("src").toString();

            if (player_picture.equals("")) {
                player_picture = "No picture";
                mIcon_val = null;
            } else {
                mIcon_val = getBitmapFromURL(player_picture);
            }

            player_information[0] = "";
            player_information[1] = "";
            player_information[2] = player_height;
            player_information[3] = player_weight + ".";
            player_information[4] = player_born;
            player_information[5] = player_age;
            player_information[6] = "";
            player_information[7] = "";
            player_information[8] = "";
            player_information[9] = "";
            player_information[10] = "";
            player_information[11] = "--";
            player_information[12] = "--";
            player_information[13] = "--";

            Elements player_stats = doc.select("div[class=player-stats]");
            hasStats = false;

            if (!player_stats.text().equals("")) {
                hasStats = true;
                String stat_headers = player_stats.select("tr").get(0)
                        .text();
                player_information[14] = stat_headers.split(" ")[0];
                player_information[15] = stat_headers.split(" ")[1];
                player_information[16] = stat_headers.split(" ")[2];

                String stats = player_stats.select("tr").get(1).text();

                player_information[17] = stats.split(" ")[0];
                player_information[18] = stats.split(" ")[1];
                player_information[19] = stats.split(" ")[2];
                player_information[20] = player_stats.select("p").first()
                        .text();
            }

        } catch (Exception e) {
        }

        return player_information;
    }

    /**
     * The system calls this to perform work in the UI thread and delivers
     * the result from doInBackground()
     *
     * @return
     */
    protected void onPostExecute(String[] result) {
        progress.dismiss();
        mResult = result;
        showCard();
    }

    public static void showCard() {
        dialog = new Dialog(mContext, R.style.PlayerCardDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.player_card);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        RelativeLayout header_bg = (RelativeLayout) dialog.findViewById(R.id.header_bg);
        header_bg.setBackgroundColor(mTeam.getColorPrimary());

        TextView player_first_name = (TextView) dialog.findViewById(R.id.player_first_name);
        TextView player_last_name = (TextView) dialog.findViewById(R.id.player_last_name);
        TextView player_number = (TextView) dialog.findViewById(R.id.player_num);
        TextView player_pos = (TextView) dialog.findViewById(R.id.player_pos);
        TextView player_height = (TextView) dialog.findViewById(R.id.player_height);
        TextView player_weight = (TextView) dialog.findViewById(R.id.player_weight);
        TextView player_birth_place = (TextView) dialog.findViewById(R.id.player_born);
        TextView player_age = (TextView) dialog.findViewById(R.id.player_age);
        TextView player_college = (TextView) dialog.findViewById(R.id.player_college);
        TextView player_experience = (TextView) dialog.findViewById(R.id.player_exp);

        TextView stats_header = (TextView) dialog.findViewById(R.id.stats_header);
        TextView player_stat_one_header = (TextView) dialog.findViewById(R.id.stat_one_header);
        TextView player_stat_two_header = (TextView) dialog.findViewById(R.id.stat_two_header);
        TextView player_stat_three_header = (TextView) dialog.findViewById(R.id.stat_three_header);

        Space space = (Space) dialog.findViewById(R.id.first_space);

        player_stat_one_header.setBackgroundColor(mTeam.getColorPrimary());
        player_stat_two_header.setBackgroundColor(mTeam.getColorPrimary());
        player_stat_three_header.setBackgroundColor(mTeam.getColorPrimary());

        player_stat_one_header.setTextColor(mTeam.getColorAccent());
        player_stat_two_header.setTextColor(mTeam.getColorAccent());
        player_stat_three_header.setTextColor(mTeam.getColorAccent());

        TextView player_stat_one = (TextView) dialog.findViewById(R.id.stat_one);
        TextView player_stat_two = (TextView) dialog.findViewById(R.id.stat_two);
        TextView player_stat_three = (TextView) dialog.findViewById(R.id.stat_three);

        player_first_name.setTextColor(mTeam.getColorAccent());
        player_last_name.setTextColor(mTeam.getColorAccent());
        player_number.setTextColor(mTeam.getColorAccent());

        player_first_name.setText(mPlayer.getFirstName());
        player_last_name.setText(mPlayer.getLastName());
        player_number.setText(mPlayer.getNumber());
        player_pos.setText(createText("Position", mPlayer.getPosition()));
        player_height.setText(createText("Height", mResult[2]));
        player_weight.setText(createText("Weight", mResult[3]));
        player_birth_place.setText(createText("Born", mResult[4]));
        player_age.setText(createText("Age", mResult[5]));
        player_college.setText(createText("College", mPlayer.getCollege()));
        player_experience.setText(createText("Experience", mPlayer.getExp()));

        if (!hasStats) {
            player_stat_one_header.setText(mResult[8]);
            player_stat_two_header.setText(mResult[9]);
            player_stat_three_header.setText(mResult[10]);
            player_stat_one.setText(mResult[11]);
            player_stat_two.setText(mResult[12]);
            player_stat_three.setText(mResult[13]);
            stats_header.setText("");
            stats_header.setVisibility(View.GONE);
            space.setVisibility(View.GONE);
        } else {
            if (mResult[14].equals("COMB")) {
                player_stat_one_header.setText("TCKL");
            } else {
                player_stat_one_header.setText(mResult[14]);
            }
            player_stat_two_header.setText(mResult[15]);
            player_stat_three_header.setText(mResult[16]);
            player_stat_one.setText(mResult[17]);
            player_stat_two.setText(mResult[18]);
            player_stat_three.setText(mResult[19]);
            stats_header.setText(mResult[20]);
        }

        ImageView image = (ImageView) dialog.findViewById(R.id.player_headshot);
        image.setBackgroundColor(mTeam.getColorPrimary());

        if (mIcon_val != null) {
            image.setImageBitmap(mIcon_val);
        }
        dialog.show();
    }

    protected static Spannable createText(String title, String value) {
        Spannable retVal = new SpannableString(title + ": " + value);
        retVal.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length() + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        retVal.setSpan(new TypefaceSpan("sans-serif-light"), title.length() + 1, retVal.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return retVal;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
        }

        return null;
    }
}