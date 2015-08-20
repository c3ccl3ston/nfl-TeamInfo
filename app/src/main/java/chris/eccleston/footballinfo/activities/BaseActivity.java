package chris.eccleston.footballinfo.activities;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import chris.eccleston.footballinfo.types.Player;
import chris.eccleston.footballinfo.types.Team;

/**
 * Created by Chris on 8/18/2015.
 */
public class BaseActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "PREFERENCES";
    public static int TEAMS_SORT_ORDER = 0;
    public static int SORT_ORDER = -1;
    public static Toolbar mToolbar;
    public final int SORT_BY_NAME = 0;
    public final int SORT_BY_NUMBER = 1;
    public final int SORT_BY_POSITION = 2;
    public final int SORT_BY_LOCATION = 0;
    public final int SORT_BY_TEAMNAME = 1;
    public final int SORT_BY_RECORD = 2;
    public final int SORT_BY_DIVISION = 3;
    public final String TEAM_ID = "teamID";

    public static void updateSubtitle(Team team) {
        if (team.getTies() != 0) {
            mToolbar.setSubtitle("(" + team.getWins() + " - " + team.getLosses() + " - " + team.getTies() + ")");
        } else {
            mToolbar.setSubtitle("(" + team.getWins() + " - " + team.getLosses() + ")");
        }
    }

    public Drawable colorizeIcon(int icon, int color) {
        Drawable arrowDrawable = getResources().getDrawable(icon);
        Drawable wrapped = DrawableCompat.wrap(arrowDrawable);

        if (arrowDrawable != null && wrapped != null) {
            arrowDrawable.mutate();
            DrawableCompat.setTint(wrapped, color);
        }

        return wrapped;
    }

    public int darken(int color, double fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        red = darkenColor(red, fraction);
        green = darkenColor(green, fraction);
        blue = darkenColor(blue, fraction);
        int alpha = Color.alpha(color);

        return Color.argb(alpha, red, green, blue);
    }

    public int darkenColor(int color, double fraction) {
        return (int) Math.max(color - (color * fraction), 0);
    }

    @TargetApi(19)
    public void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public List<Player> sortRosterByName(List<Player> roster) {
        Collections.sort(roster, new Comparator<Player>() {
            public int compare(Player s1, Player s2) {
                return s1.getLastName().compareToIgnoreCase(s2.getLastName());
            }
        });
        SORT_ORDER = SORT_BY_NAME;
        return roster;
    }

    public List<Player> sortRosterByNumber(List<Player> roster) {
        Collections.sort(roster, new Comparator<Player>() {
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
        SORT_ORDER = SORT_BY_NUMBER;
        return roster;
    }

    public List<Player> sortRosterByPosition(List<Player> roster) {
        Collections.sort(roster, new Comparator<Player>() {
            public int compare(Player s1, Player s2) {
                return s1.getPosition().compareToIgnoreCase(s2.getPosition());
            }
        });
        SORT_ORDER = SORT_BY_POSITION;
        return roster;
    }

    public List<Team> sortByLocation(List<Team> teams) {
        Collections.sort(teams, new Comparator<Team>() {
            public int compare(Team s1, Team s2) {
                if (s1.getLocation().equals(s2.getLocation())) {
                    return s1.getTeamName().compareToIgnoreCase(s2.getTeamName());
                }
                return s1.getLocation().compareToIgnoreCase(s2.getLocation());
            }
        });
        TEAMS_SORT_ORDER = SORT_BY_LOCATION;
        return teams;
    }

    public List<Team> sortByTeamName(List<Team> teams) {
        Collections.sort(teams, new Comparator<Team>() {
            public int compare(Team s1, Team s2) {
                return s1.getTeamName().compareToIgnoreCase(s2.getTeamName());
            }
        });
        TEAMS_SORT_ORDER = SORT_BY_TEAMNAME;
        return teams;
    }

    public List<Team> sortByRecord(List<Team> teams) {
        Collections.sort(teams, new Comparator<Team>() {
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
        TEAMS_SORT_ORDER = SORT_BY_RECORD;
        return teams;
    }

    public List<Team> sortByDivision(List<Team> teams) {
        Collections.sort(teams, new Comparator<Team>() {
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
        TEAMS_SORT_ORDER = SORT_BY_DIVISION;
        return teams;
    }
}
