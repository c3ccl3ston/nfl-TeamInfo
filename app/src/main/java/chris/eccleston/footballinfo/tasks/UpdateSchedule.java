package chris.eccleston.footballinfo.tasks;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import chris.eccleston.footballinfo.activities.TeamActivity;
import chris.eccleston.footballinfo.activities.TeamsActivity;
import chris.eccleston.footballinfo.adapters.TeamAdapter;
import chris.eccleston.footballinfo.fragments.FragmentTeamSchedule;
import chris.eccleston.footballinfo.types.Schedule;
import chris.eccleston.footballinfo.types.Team;

public class UpdateSchedule extends AsyncTask<Team, Void, Void> {
    private final String baseURL = "http://espn.go.com/nfl/team/schedule/_/name/";
    private final String[] team_schedule_urls = {"ari", "atl", "bal", "buf",
            "car", "chi", "cin", "cle", "dal", "den",
            "det", "gb", "hou", "ind", "jax", "kc",
            "mia", "min", "ne", "no", "nyg", "nyj",
            "oak", "phi", "pit", "sd", "sea", "sf",
            "stl", "tb", "ten", "wsh"},
            against_team_ids = {"Arizona", "Atlanta", "Baltimore", "Buffalo", "Carolina", "Chicago", "Cincinnati", "Cleveland",
                    "Dallas", "Denver", "Detroit", "Green Bay", "Houston", "Indianapolis", "Jacksonville", "Kansas City", "Miami",
                    "Minnesota", "New England", "New Orleans", "New York", "New York", "Oakland", "Philadelphia", "Pittsburgh", "San Diego",
                    "Seattle", "San Francisco", "St. Louis", "Tampa Bay", "Tennessee", "Washington"};
    private boolean mSingleTeam;
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

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Void doInBackground(Team... params) {
        for (int i = 0; i < params.length; i++) {
            mTeam = Team.find(Team.class, "team_id = ?", String.valueOf(params[i].getTeamId())).get(0);
            int num_wins = 0, num_losses = 0, num_ties = 0;

            try {
                if (isOnline()) {
                    Connection con = Jsoup.connect(baseURL + team_schedule_urls[mTeam.getTeamId()]).userAgent("Mozilla").timeout(10 * 1000);

                    Document doc = con.get();
                    Elements modcontent = doc.select("div[class=mod-content]");
                    Elements scheduleWeeks = modcontent.select("tr[class~=(oddrow|evenrow)]");

                    System.out.println("Schedule Size: " + scheduleWeeks.size());

                    int num_bad_weeks = 0;
                    if (scheduleWeeks.size() > 17) {
                        for (int j = 0; j < scheduleWeeks.size(); j++) {
                            String t = scheduleWeeks.get(j).select("td").get(1).text();
                            String out = scheduleWeeks.get(j).text();
                            if (t.contains("Jan") || t.contains("Feb")) {
                                num_bad_weeks++;
                            }
                            if (out.contains("POSTPONED")) {
                                scheduleWeeks.remove(j);
                            }
                        }
                    }

                    for (int j = 0; j < num_bad_weeks; j++) {
                        scheduleWeeks.remove(0);
                    }

                    // Remove pre-season weeks
                    if(modcontent.select("tr[class=stathead]").get(0).text().contains("Preseason")) {
                        while (scheduleWeeks.size() > 17) {
                            scheduleWeeks.remove(0);
                        }
                    }

                    String date = "";
                    boolean isHome = false;
                    int againstTeam = 0;
                    String time = "";
                    String outcome = "";
                    String scores = "";

                    int week_num = 1;
                    for (Element scheduleWeek : scheduleWeeks) {
                        date = scheduleWeek.select("td").get(1).text();

                        Elements outcome_element = scheduleWeek.select("li[class~=game-status (win|loss)?]");

                        if (outcome_element.size() > 0) {
                            outcome = outcome_element.select("li[class~=game-status (win|loss)?]").text();
                            if(outcome.equals("W")) {
                                num_wins++;
                            } else if(outcome.equals("L")) {
                                num_losses++;
                            } else {
                                num_ties++;
                            }
                        } else {
                            outcome = "";
                        }

                        if (outcome.equals("")) {
                            if (!date.equals("BYE WEEK")) {
                                time = scheduleWeek.select("td").get(3).text();
                                if (time.contains(":")) {
                                    time = time.substring(0, time.indexOf(":") + 3);
                                }
                            }
                        }

                        String home_game = scheduleWeek.select("li[class=game-status]").text();

                        if (home_game.equals("@")) {
                            isHome = false;
                        } else {
                            isHome = true;
                        }

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

                        if (!date.equals("BYE WEEK")) {
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
                            if (date.equals("BYE WEEK")) {
                                existing_week.updateSchedule(schedule_id, mTeam.getTeamId(), true);
                            } else if (!outcome.equals("") && !outcome.equals("POSTPONED")) {
                                existing_week.updateSchedule(schedule_id, mTeam.getTeamId(), date, isHome, Team.find(Team.class, "team_id = ?", String.valueOf(againstTeam)).get(0).getTeamId(), outcome, scores);
                            } else {
                                existing_week.updateSchedule(schedule_id, mTeam.getTeamId(), date, isHome, Team.find(Team.class, "team_id = ?", String.valueOf(againstTeam)).get(0).getTeamId(), time);
                            }
                            System.out.println(existing_week.toString());
                        } else {
                            Schedule schedule_week = null;
                            if (date.equals("BYE WEEK")) {
                                schedule_week = new Schedule(schedule_id, mTeam.getTeamId(), true);
                            } else if (!outcome.equals("") && !outcome.equals("POSTPONED")) {
                                schedule_week = new Schedule(schedule_id, mTeam.getTeamId(), date, isHome, Team.find(Team.class, "team_id = ?", String.valueOf(againstTeam)).get(0).getTeamId(), outcome, scores);
                            } else {
                                schedule_week = new Schedule(schedule_id, mTeam.getTeamId(), date, isHome, Team.find(Team.class, "team_id = ?", String.valueOf(againstTeam)).get(0).getTeamId(), time);
                            }
                            schedule_week.save();
                            System.out.println(schedule_week.toString());
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
            TeamActivity.updateSubtitle(mTeam);
        } else {
            TeamsActivity.refreshTeamsList.setRefreshing(false);
            List<Team> mTeams = Team.listAll(Team.class);
            switch (TeamsActivity.TEAMS_SORT_ORDER) {
                case 1:
                    Collections.sort(mTeams, new Comparator<Team>() {
                        public int compare(Team s1, Team s2) {
                            return s1.getTeamName().compareToIgnoreCase(s2.getTeamName());
                        }
                    });
                    break;
                case 2:
                    Collections.sort(mTeams, new Comparator<Team>() {
                        public int compare(Team s1, Team s2) {
                            if (s1.getWinPercentage() == s2.getWinPercentage()) {
                                if (s1.getNumGames() == s2.getNumGames()) {
                                    return s1.getLocation().compareToIgnoreCase(s2.getLocation());
                                } else if (s1.getNumGames() > s2.getNumGames()) {
                                    return -1;
                                } else {
                                    return 1;
                                }
                            } else if (s1.getWinPercentage() > s2.getWinPercentage()) {
                                return -1;
                            } else {
                                return 1;
                            }
                        }
                    });
                    break;
                case 3:
                    Collections.sort(mTeams, new Comparator<Team>() {
                        public int compare(Team s1, Team s2) {
                            String team_one = s1.getConference() + " " + s1.getDivision();
                            String team_two = s2.getConference() + " " + s2.getDivision();
                            if (team_one.equals(team_two)) {
                                if (s1.getWinPercentage() == s2.getWinPercentage()) {
                                    return s1.getLocation().compareToIgnoreCase(s2.getLocation());
                                } else if (s1.getWinPercentage() < s2.getWinPercentage()) {
                                    return 1;
                                } else {
                                    return -1;
                                }
                            } else {
                                return team_one.compareToIgnoreCase(team_two);
                            }
                        }
                    });
                    break;
                case 0:
                default:
                    Collections.sort(mTeams, new Comparator<Team>() {
                        public int compare(Team s1, Team s2) {
                            if (s1.getLocation().equals(s2.getLocation())) {
                                return s1.getTeamName().compareToIgnoreCase(s2.getTeamName());
                            }
                            return s1.getLocation().compareToIgnoreCase(s2.getLocation());
                        }
                    });
            }
            mTeamAdapter.updateList(mTeams);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
