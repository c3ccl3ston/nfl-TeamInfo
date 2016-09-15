package chris.eccleston.footballinfo.types;

import com.orm.SugarRecord;

/**
 * Created by Chris on 8/20/2015.
 */
public class Game extends SugarRecord {
    public int weekNumber;
    public String awayTeam;
    public String homeTeam;
    public String gameTime;
    public String awayScore;
    public String homeScore;
    public String pm;
    public String gameHeader;
    public boolean wasOvertime;
    int gameId;

    public Game() {
    }

    public Game(int gameID, String gameHeader, int weekNumber, String awayTeam, String homeTeam, String gameTime, String pm, String awayScore, String homeScore, boolean wasOvertime) {
        this.gameId = gameID;
        this.gameHeader = gameHeader;
        this.weekNumber = weekNumber;
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
        this.gameTime = gameTime;
        this.awayScore = awayScore;
        this.homeScore = homeScore;
        this.pm = pm;
        this.wasOvertime = wasOvertime;
    }

    public void updateGame(String awayScore, String homeScore, boolean wasOvertime) {
        this.awayScore = awayScore;
        this.homeScore = homeScore;
        this.wasOvertime = wasOvertime;
        this.save();
    }

    public String getGameHeader() {
        return gameHeader;
    }

    public String getPm() {
        return pm;
    }

    public void setPm(String pm) {
        this.pm = pm;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getGameTime() {
        return gameTime;
    }

    public void setGameTime(String gameTime) {
        this.gameTime = gameTime;
    }

    public String getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(String awayScore) {
        this.awayScore = awayScore;
    }

    public String getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(String homeScore) {
        this.homeScore = homeScore;
    }

    public boolean isWasOvertime() {
        return wasOvertime;
    }

    public void setWasOvertime(boolean wasOvertime) {
        this.wasOvertime = wasOvertime;
    }
}