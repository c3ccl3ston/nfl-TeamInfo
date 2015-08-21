package chris.eccleston.footballinfo.tasks;

import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import chris.eccleston.footballinfo.types.Game;

/**
 * Created by Chris on 8/20/2015.
 */
public class UpdateWeeklySchedules extends AsyncTask<Void, Void, Void> {

    Context mContext;

    public UpdateWeeklySchedules(Context context) {
        mContext = context;
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
        String awayTeam = "";
        String homeTeam = "";
        String gameTime = "";
        String awayScore = "";
        String homeScore = "";
        String pm = "";
        boolean wasOvertime;

        int dateId = 1;
        try {
            for (int weekNum = 1; weekNum <= 17; weekNum++) {
                int gameID = 1;
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
                        Game new_game = new Game(gameID++ * weekNum, date, weekNum, awayTeam, homeTeam, gameTime, pm, "", "", false);
                        new_game.save();
                    }
                }

//                for (Element nfl_schedule_week : week_schedule) {
//                    awayTeam = nfl_schedule_week.select("span[class~=team-name away]").text();
//                    awayScore = nfl_schedule_week.select("span[class~=team-score away]").text();
//                    homeTeam = nfl_schedule_week.select("span[class~=team-name home]").text();
//                    homeScore = nfl_schedule_week.select("span[class~=team-score home]").text();
//                    gameTime = nfl_schedule_week.select("span[class~=time]").text();
//                    pm = nfl_schedule_week.select("span[class~=pm]").text();

//                    Game existing_game = null;
//                    try {
//                        existing_game = Game.find(Game.class, "game_id = ?", String.valueOf(gameID)).get(0);
//                    } catch (Exception e) {
//                    }
//
//                    if (existing_game != null) {
//
//                    } else {
//                        Game new_game = new Game(gameID++*weekNum, weekNum, awayTeam, homeTeam, gameTime, pm, "", "", false);
//                        new_game.save();
//                    }
//                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
//        TeamsActivity.refreshTeamsList.setRefreshing(false);
    }
}
