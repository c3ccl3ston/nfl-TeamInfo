package chris.eccleston.footballinfo.types;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by Chris on 2/12/2015.
 */
public class Schedule extends SugarRecord<Schedule> {
    private int scheduleId;
    private Date date = new Date();
    private int againstTeam;
    private boolean isHome;
    private String outcome = "";
    private String gameScores = "";
    private boolean byeWeek;

    private int teamId;

    public Schedule() {
    }

    public Schedule(int scheduleId, int teamId, Date date, boolean isHome, int againstTeam) {
        this.scheduleId = scheduleId;
        this.teamId = teamId;
        this.date = date;
        this.isHome = isHome;
        this.againstTeam = againstTeam;
        this.outcome = "";
        this.byeWeek = false;
        this.gameScores = "";
    }

    public Schedule(int scheduleId, int teamId, Date date, boolean isHome, int againstTeam, String outcome, String gameScores) {
        this.scheduleId = scheduleId;
        this.teamId = teamId;
        this.date = date;
        this.isHome = isHome;
        this.againstTeam = againstTeam;
        this.outcome = outcome;
        this.gameScores = gameScores;
        this.byeWeek = false;
    }

    public Schedule(int scheduleId, int teamId, boolean byeWeek) {
        this.scheduleId = scheduleId;
        this.teamId = teamId;
        this.outcome = "";
        this.byeWeek = true;
    }

    public void updateSchedule(int scheduleId, int teamId, Date date, boolean isHome, int againstTeam) {
        this.scheduleId = scheduleId;
        this.teamId = teamId;
        this.date = date;
        this.isHome = isHome;
        this.againstTeam = againstTeam;
        this.outcome = "";
        this.byeWeek = false;
        this.save();
    }

    public void updateSchedule(int scheduleId, int teamId, Date date, boolean isHome, int againstTeam, String outcome, String gameScores) {
        this.scheduleId = scheduleId;
        this.teamId = teamId;
        this.date = date;
        this.isHome = isHome;
        this.againstTeam = againstTeam;
        this.outcome = outcome;
        this.gameScores = gameScores;
        this.byeWeek = false;
        this.save();
    }

    public void updateSchedule(int scheduleId, int teamId, boolean byeWeek) {
        this.scheduleId = scheduleId;
        this.teamId = teamId;
        this.outcome = "";
        this.byeWeek = true;
        this.gameScores = "";
        this.save();
    }

    public String getScores() {
        return gameScores;
    }

    public Date getDate() {
        return date;
    }

    public boolean getIsHome() {
        return isHome;
    }

    public int getAgainstTeam() {
        return againstTeam;
    }

    public boolean getByeWeek() {
        return byeWeek;
    }

    public String getOutcome() {
        return outcome;
    }
}