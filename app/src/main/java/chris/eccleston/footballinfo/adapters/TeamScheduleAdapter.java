package chris.eccleston.footballinfo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.types.Schedule;
import chris.eccleston.footballinfo.types.Team;

public class TeamScheduleAdapter extends RecyclerView.Adapter<TeamScheduleAdapter.TeamScheduleViewHolder> {

    private List<Schedule> scheduleWeeks;
    private Context mContext;

    public TeamScheduleAdapter(List<Schedule> teamsList, Context context) {
        this.scheduleWeeks = teamsList;
        this.mContext = context;
    }

    public void updateList(List<Schedule> data) {
        scheduleWeeks = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return scheduleWeeks.size();
    }

    @Override
    public void onBindViewHolder(TeamScheduleViewHolder teamScheduleViewHolder, int i) {
        Schedule ci = scheduleWeeks.get(i);

        teamScheduleViewHolder.weekNumber.setText("Week " + (i + 1));

        DateFormat formatter = new SimpleDateFormat("EEE, MMM d");
        DateFormat timeFormatter = new SimpleDateFormat("h:mm");

        if (!ci.getByeWeek()) {
            teamScheduleViewHolder.weekDate.setText(formatter.format(ci.getDate()));
            teamScheduleViewHolder.opponentLogo.setVisibility(View.VISIBLE);
            teamScheduleViewHolder.gameLocation.setVisibility(View.VISIBLE);
            teamScheduleViewHolder.opponentName.setVisibility(View.VISIBLE);
            teamScheduleViewHolder.byeWeek.setVisibility(View.GONE);

            Team opponentTeam = Team.find(Team.class, "team_id = ?", String.valueOf(ci.getAgainstTeam())).get(0);
            teamScheduleViewHolder.opponentLogo.setImageResource(opponentTeam.getTeamLogo());
            teamScheduleViewHolder.opponentName.setText(opponentTeam.getTeamName());
            if (ci.getIsHome()) {
                teamScheduleViewHolder.gameLocation.setText("vs.");
            } else {
                teamScheduleViewHolder.gameLocation.setText("at ");
            }

            if (ci.getOutcome().equals("")) {
                String time = timeFormatter.format(ci.getDate());
                teamScheduleViewHolder.score.setVisibility(View.GONE);
                teamScheduleViewHolder.outcome.setVisibility(View.GONE);
                teamScheduleViewHolder.gameTime.setVisibility(View.VISIBLE);
                teamScheduleViewHolder.pm.setVisibility(View.VISIBLE);
                teamScheduleViewHolder.et.setVisibility(View.VISIBLE);
                teamScheduleViewHolder.gameTime.setText(time);
            } else {
                teamScheduleViewHolder.gameTime.setVisibility(View.GONE);
                teamScheduleViewHolder.pm.setVisibility(View.GONE);
                teamScheduleViewHolder.et.setVisibility(View.GONE);
                teamScheduleViewHolder.score.setVisibility(View.VISIBLE);
                teamScheduleViewHolder.outcome.setVisibility(View.VISIBLE);
                if (ci.getOutcome().equals("W")) {
                    teamScheduleViewHolder.outcome.setTextColor(mContext.getResources().getColor(R.color.green));
                } else if (ci.getOutcome().equals("L")) {
                    teamScheduleViewHolder.outcome.setTextColor(mContext.getResources().getColor(R.color.red));
                } else {
                    teamScheduleViewHolder.outcome.setTextColor(mContext.getResources().getColor(R.color.gray));
                }
                teamScheduleViewHolder.outcome.setText(ci.getOutcome());
                teamScheduleViewHolder.score.setText(ci.getScores());
            }
        } else {
            teamScheduleViewHolder.opponentLogo.setVisibility(View.GONE);
            teamScheduleViewHolder.pm.setVisibility(View.GONE);
            teamScheduleViewHolder.et.setVisibility(View.GONE);
            teamScheduleViewHolder.score.setVisibility(View.GONE);
            teamScheduleViewHolder.outcome.setVisibility(View.GONE);
            teamScheduleViewHolder.gameLocation.setVisibility(View.GONE);
            teamScheduleViewHolder.gameTime.setVisibility(View.GONE);
            teamScheduleViewHolder.opponentName.setVisibility(View.GONE);
            teamScheduleViewHolder.weekDate.setVisibility(View.GONE);
            teamScheduleViewHolder.byeWeek.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public TeamScheduleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_schedule, viewGroup, false);
        return new TeamScheduleViewHolder(itemView);
    }

    public static class TeamScheduleViewHolder extends RecyclerView.ViewHolder {
        protected ImageView opponentLogo;
        protected TextView weekNumber;
        protected TextView weekDate;
        protected TextView gameLocation;
        protected TextView gameTime;
        protected TextView opponentName;
        protected TextView byeWeek;
        protected TextView pm;
        protected TextView et;
        protected TextView score;
        protected TextView outcome;

        public TeamScheduleViewHolder(View v) {
            super(v);
            opponentLogo = (ImageView) v.findViewById(R.id.opponentLogo);
            weekNumber = (TextView) v.findViewById(R.id.weekNumber);
            weekDate = (TextView) v.findViewById(R.id.weekDate);
            gameLocation = (TextView) v.findViewById(R.id.gameLocation);
            gameTime = (TextView) v.findViewById(R.id.gameTime);
            opponentName = (TextView) v.findViewById(R.id.opponentName);
            byeWeek = (TextView) v.findViewById(R.id.bye_week);
            pm = (TextView) v.findViewById(R.id.pm);
            et = (TextView) v.findViewById(R.id.et);
            score = (TextView) v.findViewById(R.id.scores);
            outcome = (TextView) v.findViewById(R.id.outcome);
        }

    }
}