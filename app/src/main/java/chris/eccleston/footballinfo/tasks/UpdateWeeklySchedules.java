package chris.eccleston.footballinfo.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import chris.eccleston.footballinfo.fragments.FragmentWeeklySchedule;
import chris.eccleston.footballinfo.types.Game;

/**
 * Created by Chris on 8/20/2015.
 */
public class UpdateWeeklySchedules extends AsyncTask<Void, Void, Void> {

    protected int gameID = 1;
    protected int weekNumber;
    FragmentWeeklySchedule mFragment;

    public UpdateWeeklySchedules() {
    }

    public UpdateWeeklySchedules(FragmentWeeklySchedule fragment) {
        mFragment = fragment;
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
    @Override
    protected Void doInBackground(Void... params) {
        String awayTeam;
        String homeTeam;
        String gameTime;
        String awayScore;
        String homeScore;
        String pm;
        boolean wasOvertime = false;

        int dateId = 1;
        try {
            for (int weekNum = 1; weekNum <= 17; weekNum++) {
                String url = "http://www.nfl.com/schedules/2016/REG" + weekNum;
                Connection con = Jsoup.connect(url).userAgent("Mozilla").timeout(10000);
                Document doc = con.get();
                Elements scheduleTable = doc.select("ul.schedules-table");
                if (scheduleTable.size() > 1) {
                    scheduleTable = scheduleTable.get(1).select("li");
                } else {
                    scheduleTable = scheduleTable.get(0).select("li");
                }

                String date = "";
                for (Element item : scheduleTable) {
                    if (item.hasClass("schedules-list-date")) {
                        date = dateId++ + " " + item.select("span > span").text();
                        date = date.substring(0, date.lastIndexOf(' ')) + " " + Integer.parseInt(date.substring(date.lastIndexOf(' ') + 1));
                    } else {
                        awayTeam = item.select("span.team-name.away").text();
                        awayScore = item.select("span.team-score.away").text();
                        homeTeam = item.select("span.team-name.home").text();
                        homeScore = item.select("span.team-score.home").text();
                        gameTime = item.select("span.time").text();
                        pm = item.select("span.pm").text();

                        Game existing_game = null;
                        try {
                            existing_game = Game.find(Game.class, "game_id = ?", String.valueOf(gameID)).get(0);
                            gameID++;
                        } catch (Exception e) {
                            Log.d("DEBUG", "Matching game was not found");
                        }

                        if (existing_game != null) {
                            existing_game.updateGame(awayScore, homeScore, wasOvertime);
                        } else {
                            new Game(gameID++, date, weekNum, awayTeam, homeTeam, gameTime, pm, awayScore, homeScore, false).save();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        mFragment.stopRefresh();
        mFragment.updateList();
    }
}