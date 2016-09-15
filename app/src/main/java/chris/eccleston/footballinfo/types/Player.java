package chris.eccleston.footballinfo.types;

import com.orm.SugarRecord;

import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.activities.TeamActivity;

/**
 * Created by Chris on 2/9/2015.
 */
public class Player extends SugarRecord implements Comparable<Player> {
    public int playerId;
    public String number;
    public String firstName;
    public String lastName;
    public String position;
    public String link;
    public String exp;
    public String college;

    public int teamId;

    public Player() {}

    public Player(int playerId, String number, String lastName, String firstName, String position, String exp, String college, String link, int teamId) {
        this.playerId = playerId;
        this.number = number;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.exp = exp;
        this.college = college;
        this.link = link;
        this.teamId = teamId;
    }

    public void updatePlayer(String number, String lastName, String firstName, String position, String exp, String college, String link, int teamId) {
        this.number = number;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.exp = exp;
        this.college = college;
        this.link = link;
        this.teamId = teamId;
        this.save();
    }

    public String getNumber() {
        return number;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getExp() {
        if (exp.equals("R")) {
            return "Rookie";
        }
        if (exp.equals("1")) {
            return "1 year";
        }
        return this.exp + " years";
    }

    public String getCollege() {
        return this.college;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public void setNewTeamId(int teamId) {
        this.teamId = teamId;
        this.save();
    }

    public String getPosition() {
        return position;
    }

    public String getLink() {
        return link;
    }

    @Override
    public int compareTo(Player s2) {
        switch (TeamActivity.sortOrder) {
            case R.id.roster_sort_by_number:
                int p1Number = (getNumber().equals("--") ? 0 : Integer.parseInt(getNumber()));
                int p2Number = (s2.getNumber().equals("--") ? 0 : Integer.parseInt(s2.getNumber()));

                if (p1Number == p2Number) {
                    if (getLastName().equals(s2.getLastName())) {
                        return getFirstName().compareToIgnoreCase(s2.getFirstName());
                    }
                    return getLastName().compareToIgnoreCase(s2.getLastName());
                } else {
                    return Integer.compare(p1Number, p2Number);
                }
            case R.id.roster_sort_by_position:
                if (getPosition().equals(s2.getPosition())) {
                    if (getLastName().equals(s2.getLastName())) {
                        return getFirstName().compareToIgnoreCase(s2.getFirstName());
                    }
                    return getLastName().compareToIgnoreCase(s2.getLastName());
                }
                return getPosition().compareToIgnoreCase(s2.getPosition());
            case R.id.roster_sort_by_name:
            default:
                if (getLastName().equals(s2.getLastName())) {
                    return getFirstName().compareToIgnoreCase(s2.getFirstName());
                }
                return getLastName().compareToIgnoreCase(s2.getLastName());
        }
    }
}