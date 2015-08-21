package chris.eccleston.footballinfo.tasks;

import android.content.Context;
import android.os.AsyncTask;

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

    Context mContext;
    FragmentWeeklySchedule mFragmentWeeklySchedule;

    public UpdateWeeklySchedules(Context context) {
        mContext = context;
    }

    public void setFragmentWeeklySchedule(FragmentWeeklySchedule fragment) {
        mFragmentWeeklySchedule = fragment;
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
        int gameID = 1;
        try {
            for (int weekNum = 1; weekNum <= 17; weekNum++) {
                String url = "http://www.nfl.com/schedules/2015/REG" + weekNum;
                Document doc = Jsoup.connect(url).get();
                Elements scheduleTable = doc.select("ul[class~=schedules-table]").select("li");

                String date = "";
                for (Element item : scheduleTable) {
                    if (item.hasClass("schedules-list-date")) {
                        date = dateId++ + " " + item.select("span > span").text();
                    } else {
                        awayTeam = item.select("span[class~=team-name away]").text();
                        awayScore = item.select("span[class~=team-score away]").text();
                        homeTeam = item.select("span[class~=team-name home]").text();
                        homeScore = item.select("span[class~=team-score home]").text();
                        gameTime = item.select("span[class~=time]").text();
                        pm = item.select("span[class~=pm]").text();

                        Game existing_game = null;
                        try {
                            existing_game = Game.find(Game.class, "game_id = ?", String.valueOf(gameID)).get(0);
                        } catch (Exception e) {
                        }

                        if (existing_game != null) {
                            existing_game.updateGame(awayScore, homeScore, wasOvertime);
                        } else {
                            Game new_game = new Game(gameID, date, weekNum, awayTeam, homeTeam, gameTime, pm, awayScore, homeScore, false);
                            new_game.save();
                            gameID++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (mFragmentWeeklySchedule != null) {
            mFragmentWeeklySchedule.stopRefreshing();
        }
    }
}
