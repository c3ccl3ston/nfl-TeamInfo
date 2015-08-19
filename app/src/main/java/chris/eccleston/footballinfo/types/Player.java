package chris.eccleston.footballinfo.types;

import com.orm.SugarRecord;

import java.util.Comparator;

/**
 * Created by Chris on 2/9/2015.
 */
public class Player extends SugarRecord<Player> implements Comparator<Player> {
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

//    public String getFullName() {
//        if (lastNameFirst) {
//            return lastName + ", " + firstName;
//        }
//        return firstName + " " + lastName;
//    }

//    public void setLastNameFirst(boolean type) {
//        this.lastNameFirst = type;
//    }

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

    public int sort(Player lhs, Player rhs, String type) {
        if (type.equals("First Name")) {
            return lhs.getFirstName().compareToIgnoreCase(rhs.getFirstName());
        } else if (type.equals("Last Name")) {
            return lhs.getLastName().compareToIgnoreCase(rhs.getLastName());
        } else if (type.equals("Number")) {
            return lhs.getNumber().compareToIgnoreCase(rhs.getNumber());
        } else {
            return lhs.getPosition().compareToIgnoreCase(rhs.getPosition());
        }
    }

    public int compare(Player lhs, Player rhs) {
        return 0;
    }
}