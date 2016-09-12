package chris.eccleston.footballinfo.types;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import chris.eccleston.footballinfo.activities.BaseActivity;
import chris.eccleston.footballinfo.activities.TeamsActivity;

public class Team extends SugarRecord<Team> implements Comparable<Team> {
    @Ignore
    protected static final long serialVersionUID = 1L;

    public int teamId;
    public String location;
    public String teamName;
    public int teamImage;
    public int color_primary;
    public int color_primary_dark;
    public int color_accent;
    public String conference;
    public String division;
    public int themeId;
    public int wins;
    public int losses;
    public int ties;
    private int teamPopupId;

    public Team() {
    }

    public Team(int teamId, String location, String teamName, int teamImage, int color_primary, int color_primary_dark, int color_accent, String teamConference, String teamDivision, int themeId, int teamPopupId, int wins, int losses, int ties) {
        this.teamId = teamId;
        this.location = location;
        this.teamName = teamName;
        this.teamImage = teamImage;
        this.color_primary = color_primary;
        this.color_primary_dark = color_primary_dark;
        this.color_accent = color_accent;
        this.conference = teamConference;
        this.division = teamDivision;
        this.themeId = themeId;
        this.teamPopupId = teamPopupId;
        this.wins = wins;
        this.losses = losses;
        this.ties = ties;
    }

    public String getConference() {
        return this.conference;
    }

    public String getDivision() {
        return this.division;
    }

    public int getColorPrimary() {
        return this.color_primary;
    }

    public int getColorAccent() {
        return this.color_accent;
    }

    public int getColorPrimaryDark() {
        return this.color_primary_dark;
    }

    public String getLocation() {
        return this.location;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public int getTeamLogo() {
        return this.teamImage;
    }

    public double getWinPercentage() {
        double wins = this.wins + (this.ties / 2.0);
        return (wins / (double) (this.wins + this.losses + this.ties));
    }

    public int getNumGames() {
        return (this.wins + this.losses + this.ties);
    }

    public int getTeamId() {
        return this.teamId;
    }

    public int getThemeId() {
        return this.themeId;
    }

    public int getTeamPopupId() {
        return this.teamPopupId;
    }

    public int getWins() {
        return this.wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return this.losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getTies() {
        return this.ties;
    }

    public void setTies(int ties) {
        this.ties = ties;
    }

    @Override
    public int compareTo(Team s2) {
        switch (TeamsActivity.TEAMS_SORT_ORDER) {
            case BaseActivity.SORT_BY_TEAMNAME:
                return getTeamName().compareToIgnoreCase(s2.getTeamName());
            case BaseActivity.SORT_BY_RECORD:
                if (getWinPercentage() == s2.getWinPercentage()) {
                    return getLocation().compareToIgnoreCase(s2.getLocation());
                } else {
                    return Double.compare(s2.getWinPercentage(), getWinPercentage());
                }
            case BaseActivity.SORT_BY_DIVISION:
                String team_one = getConference() + " " + getDivision();
                String team_two = s2.getConference() + " " + s2.getDivision();
                if (team_one.equals(team_two)) {
                    if (getWinPercentage() == s2.getWinPercentage()) {
                        return getLocation().compareToIgnoreCase(s2.getLocation());
                    } else {
                        return Double.compare(s2.getWinPercentage(), getWinPercentage());
                    }
                } else {
                    return team_one.compareToIgnoreCase(team_two);
                }
            case BaseActivity.SORT_BY_LOCATION:
            default:
                if (getLocation().equals(s2.getLocation())) {
                    return getTeamName().compareToIgnoreCase(s2.getTeamName());
                }
                return getLocation().compareToIgnoreCase(s2.getLocation());
        }
    }
}