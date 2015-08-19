package chris.eccleston.footballinfo.types;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.Serializable;

public class Team extends SugarRecord<Team> {
    @Ignore
    protected static final long serialVersionUID = 1L;

    public int teamId;
    public String location;
    public String teamName;
    public int teamImage;
    //    public boolean isChecked;
//    public double winPercentage;
//    public boolean isFavorite;
//    public int games;
    public int color_primary;
    public int color_primary_dark;
    public int color_accent;
    //    public String NFLAbbreviation;
//    public String ESPNAbbreviation;
    public String conference;
    public String division;
    public int themeId;
    private int teamPopupId;
    public int wins;
    public int losses;
    public int ties;

    public Team() {}

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
//
//    public String getNFLAbbreviation() {
//        return this.NFLAbbreviation;
//    }
//
//    public String getESPNAbbreviation() {
//        return this.ESPNAbbreviation;
//    }

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

//    public boolean getChecked() {
//        return this.isChecked;
//    }
//
//    public void setChecked(boolean checked) {
//        this.isChecked = checked;
//    }
//
//    public void setWinPercentage(double wins) {
//        this.winPercentage = wins;
//    }
//
    public double getWinPercentage() {
        return ((double) this.wins / (double) (this.wins + this.losses + this.ties));
    }

    public int getNumGames() {
        return (this.wins + this.losses + this.ties);
    }

//
//    public boolean isFavorite() {
//        return this.isFavorite;
//    }
//
//    public void setAsFavorite(boolean fav) {
//        this.isFavorite = fav;
//    }

    public int getTeamId() { return this.teamId; }

    public int getThemeId() {
        return this.themeId;
    }

    public int getTeamPopupId() { return this.teamPopupId; }

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
}