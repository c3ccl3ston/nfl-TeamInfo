package chris.eccleston.footballinfo.tasks;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.activities.TeamsActivity;
import chris.eccleston.footballinfo.adapters.TeamAdapter;
import chris.eccleston.footballinfo.fragments.FragmentTeamSchedule;
import chris.eccleston.footballinfo.types.Schedule;
import chris.eccleston.footballinfo.types.Team;

public class UpdateSchedule extends AsyncTask<Team, Integer, Void> {
    private final String baseURL = "http://espn.go.com/nfl/team/schedule/_/name/";
    private final String[] team_schedule_urls = {"ari", "atl", "bal", "buf",
            "car", "chi", "cin", "cle", "dal", "den",
            "det", "gb", "hou", "ind", "jax", "kc",
            "mia", "min", "ne", "no", "nyg", "nyj",
            "oak", "phi", "pit", "sd", "sea", "sf",
            "la", "tb", "ten", "wsh"},
            against_team_ids = {"Arizona", "Atlanta", "Baltimore", "Buffalo", "Carolina", "Chicago", "Cincinnati", "Cleveland",
                    "Dallas", "Denver", "Detroit", "Green Bay", "Houston", "Indianapolis", "Jacksonville", "Kansas City", "Miami",
                    "Minnesota", "New England", "New Orleans", "New York", "New York", "Oakland", "Philadelphia", "Pittsburgh", "San Diego",
                    "Seattle", "San Francisco", "Los Angeles", "Tampa Bay", "Tennessee", "Washington"};
    public MaterialDialog mProgressDialog;
    private boolean mSingleTeam;
    private boolean mInitialLoad;
    private Team mTeam;
    private Context mContext;
    private TeamAdapter mTeamAdapter;

    public UpdateSchedule(Context context) {
        mContext = context;
    }

    public void setTeamAdapter(TeamAdapter t) {
        mTeamAdapter = t;
    }

    public void setSingleTeam(boolean isSingleTeam) {
        mSingleTeam = isSingleTeam;
    }

    public void setInitialLoad(boolean initialLoad) {
        mInitialLoad = initialLoad;
    }

    @Override
    protected void onPreExecute() {
        if (mInitialLoad) {
            mProgressDialog = new MaterialDialog.Builder(mContext)
                    .title("Initializing files")
                    .titleColorRes(R.color.nfl_primary_color)
                    .progress(false, 32, false)
                    .backgroundColor(Color.WHITE)
                    .cancelable(false)
                    .contentColorRes(R.color.nfl_primary_color)
                    .widgetColorRes(R.color.nfl_primary_color)
                    .show();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        mProgressDialog.incrementProgress(1);
    }

    @Override
    protected Void doInBackground(Team... params) {
        for (int i = 0; i < params.length; i++) {
            if (mInitialLoad) {
                publishProgress(i);
            }

            mTeam = Team.find(Team.class, "team_id = ?", String.valueOf(params[i].getTeamId())).get(0);
            int num_wins = 0, num_losses = 0, num_ties = 0;

            try {
                if (isOnline()) {
                    Connection con = Jsoup.connect(baseURL + team_schedule_urls[mTeam.getTeamId()]).userAgent("Mozilla").timeout(10 * 1000);

                    Document doc = con.get();
                    Elements modcontent = doc.select("div[class=mod-content]");
                    Elements scheduleWeeks = modcontent.select("tr[class~=(oddrow|evenrow)]");

                    Calendar cal = Calendar.getInstance();
                    cal.set(2016, Calendar.SEPTEMBER, 7, 13, 0);
                    Date startDate = cal.getTime();
                    cal.set(2017, Calendar.JANUARY, 2, 13, 0);
                    Date endDate = cal.getTime();

                    Date date = new Date();
                    boolean isHome = false;
                    int againstTeam = 0;
                    String outcome = "";
                    String scores = "";

                    int week_num = 1;
                    for (Element scheduleWeek : scheduleWeeks) {
                        String dateTxt = scheduleWeek.select("td").get(1).text();
                        DateFormat formatter = new SimpleDateFormat("EEE, MMM d");
                        boolean isByeWeek = false;
                        try {
                            date = formatter.parse(dateTxt);
                            Calendar inst = Calendar.getInstance();
                            inst.setTime(date);
                            if (inst.get(Calendar.MONTH) == Calendar.JANUARY) {
                                inst.set(Calendar.YEAR, 2017);
                            } else {
                                inst.set(Calendar.YEAR, 2016);
                            }
                            date = inst.getTime();
                        } catch (ParseException pe) {
                            Log.e("PARSING_ERROR", pe.getMessage());
                            isByeWeek = true;
                        }

                        if ((date.after(startDate) && date.before(endDate)) || isByeWeek) {
                            Elements outcome_element = scheduleWeek.select("li[class~=game-status (win|loss)?]");

                            if (outcome_element.size() > 0) {
                                outcome = outcome_element.select("li[class~=game-status (win|loss)?]").text();
                                if (outcome.equals("W")) {
                                    num_wins++;
                                } else if (outcome.equals("L")) {
                                    num_losses++;
                                } else if (outcome.equals("T")) {
                                    num_ties++;
                                }
                            } else {
                                outcome = "";
                            }

                            if (outcome.equals("")) {
                                if (!isByeWeek) {
                                    String time = scheduleWeek.select("td").get(3).text();
                                    if (time.contains(":")) {
                                        time = time.substring(0, time.indexOf(":") + 3);
                                        Calendar inst = Calendar.getInstance();
                                        inst.setTime(date);
                                        inst.set(Calendar.HOUR, Integer.parseInt(time.substring(0, time.indexOf(":"))));
                                        inst.set(Calendar.MINUTE, Integer.parseInt(time.substring(time.indexOf(":") + 1)));
                                        date = inst.getTime();
                                    }
                                }
                            }

                            String home_game = scheduleWeek.select("li[class=game-status]").text();

                            isHome = !home_game.equals("@");

                            scores = scheduleWeek.select("li[class=score]").text();

                            String vs_team = scheduleWeek.select("li[class=team-name]").text();
                            boolean isGiants = false;
                            boolean isJets = false;
                            if (vs_team.equals("New York")) {
                                if (scheduleWeek.select("li[class=team-name]").select("a").attr("href").contains("giants")) {
                                    isGiants = true;
                                }
                                if (scheduleWeek.select("li[class=team-name]").select("a").attr("href").contains("jets")) {
                                    isJets = true;
                                }
                            }

                            if (!isByeWeek) {
                                if (isGiants) {
                                    againstTeam = 20;
                                } else if (isJets) {
                                    againstTeam = 21;
                                } else {
                                    int indx = Arrays.asList(against_team_ids).indexOf(vs_team);
                                    againstTeam = Team.find(Team.class, "team_id = ?", String.valueOf(indx)).get(0).getTeamId();
                                }
                            }

                            int schedule_id = (mTeam.getTeamId() * 17) + week_num++;

                            Schedule existing_week = null;
                            try {
                                existing_week = Schedule.find(Schedule.class, "schedule_id = ?", String.valueOf(schedule_id)).get(0);
                            } catch (Exception e) {
                            }

                            if (existing_week != null) {
                                if (isByeWeek) {
                                    existing_week.updateSchedule(schedule_id, mTeam.getTeamId(), true);
                                } else if (!outcome.equals("") && !outcome.equals("POSTPONED")) {
                                    existing_week.updateSchedule(schedule_id, mTeam.getTeamId(), date, isHome, Team.find(Team.class, "team_id = ?", String.valueOf(againstTeam)).get(0).getTeamId(), outcome, scores);
                                } else {
                                    existing_week.updateSchedule(schedule_id, mTeam.getTeamId(), date, isHome, Team.find(Team.class, "team_id = ?", String.valueOf(againstTeam)).get(0).getTeamId(), "", scores);
                                }
                            } else {
                                Schedule schedule_week = null;
                                if (isByeWeek) {
                                    schedule_week = new Schedule(schedule_id, mTeam.getTeamId(), true);
                                } else if (!outcome.equals("") && !outcome.equals("POSTPONED")) {
                                    schedule_week = new Schedule(schedule_id, mTeam.getTeamId(), date, isHome, Team.find(Team.class, "team_id = ?", String.valueOf(againstTeam)).get(0).getTeamId(), outcome, scores);
                                } else {
                                    schedule_week = new Schedule(schedule_id, mTeam.getTeamId(), date, isHome, Team.find(Team.class, "team_id = ?", String.valueOf(againstTeam)).get(0).getTeamId(), "", scores);
                                }
                                schedule_week.save();
                            }
                        }
                    }

                    mTeam.setWins(num_wins);
                    mTeam.setLosses(num_losses);
                    mTeam.setTies(num_ties);
                    mTeam.save();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (mSingleTeam) {
            List<Schedule> new_roster = Schedule.find(Schedule.class, "team_id = ?", String.valueOf(mTeam.getTeamId()));
            FragmentTeamSchedule.ca.updateList(new_roster);
            FragmentTeamSchedule.ca.notifyDataSetChanged();
            FragmentTeamSchedule.refreshScheduleList.setRefreshing(false);
            ((AppCompatActivity) mContext).getSupportActionBar().setSubtitle("(" + mTeam.getWins() + " - " + mTeam.getLosses() + ")");
        } else {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            TeamsActivity.refreshTeamsList.setRefreshing(false);
            List<Team> mTeams = Team.listAll(Team.class);
            Collections.sort(mTeams);
            mTeamAdapter.updateList(mTeams);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}