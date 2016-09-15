package chris.eccleston.footballinfo.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.types.Schedule;
import chris.eccleston.footballinfo.types.Team;

public class TeamScheduleAdapter extends RecyclerView.Adapter<TeamScheduleAdapter.TeamScheduleViewHolder> {

    DateFormat formatter = new SimpleDateFormat("EEE, MMM d");
    DateFormat timeFormatter = new SimpleDateFormat("h:mm");
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
    public void onBindViewHolder(TeamScheduleViewHolder holder, int i) {
        Schedule ci = scheduleWeeks.get(i);

        holder.opponentLogo.setVisibility(View.GONE);
        holder.weekNumber.setVisibility(View.GONE);
        holder.weekDate.setVisibility(View.GONE);
        holder.gameLocation.setVisibility(View.GONE);
        holder.gameTime.setVisibility(View.GONE);
        holder.opponentName.setVisibility(View.GONE);
        holder.byeWeek.setVisibility(View.GONE);
        holder.pm.setVisibility(View.GONE);
        holder.et.setVisibility(View.GONE);
        holder.score.setVisibility(View.GONE);
        holder.outcome.setVisibility(View.GONE);

        holder.weekNumber.setText("Week " + (i + 1));

        if (!ci.getByeWeek()) {
            holder.opponentLogo.setVisibility(View.VISIBLE);
            holder.weekNumber.setVisibility(View.VISIBLE);
            holder.weekDate.setVisibility(View.VISIBLE);
            holder.gameLocation.setVisibility(View.VISIBLE);
            holder.opponentName.setVisibility(View.VISIBLE);

            Team opponentTeam = Team.find(Team.class, "team_id = ?", String.valueOf(ci.getAgainstTeam())).get(0);

            holder.weekDate.setText(formatter.format(ci.getDate()));
            holder.opponentLogo.setImageResource(opponentTeam.getTeamLogo());
            holder.opponentName.setText(opponentTeam.getTeamName());
            holder.gameLocation.setText(ci.getIsHome() ? "vs." : "at ");

            if (ci.getOutcome().equals("")) {
                if (!ci.getScores().matches("\\d+\\-\\d+")) {
                    holder.score.setVisibility(View.GONE);
                    holder.outcome.setVisibility(View.GONE);
                    holder.gameTime.setVisibility(View.VISIBLE);
                    holder.pm.setVisibility(View.VISIBLE);
                    holder.et.setVisibility(View.VISIBLE);
                    holder.gameTime.setText(timeFormatter.format(ci.getDate()));
                } else {
                    holder.gameTime.setVisibility(View.GONE);
                    holder.pm.setVisibility(View.GONE);
                    holder.et.setVisibility(View.GONE);
                    holder.outcome.setVisibility(View.GONE);
                    holder.score.setVisibility(View.VISIBLE);
                    holder.score.setText(ci.getScores());
                }
            } else {
                holder.gameTime.setVisibility(View.GONE);
                holder.pm.setVisibility(View.GONE);
                holder.et.setVisibility(View.GONE);
                holder.score.setVisibility(View.VISIBLE);
                holder.outcome.setVisibility(View.VISIBLE);
                holder.outcome.setTextColor(ci.getOutcome().equals("W") ? ContextCompat.getColor(mContext, R.color.green) :
                        (ci.getOutcome().equals("L") ? ContextCompat.getColor(mContext, R.color.red) : ContextCompat.getColor(mContext, R.color.gray)));
                holder.outcome.setText(ci.getOutcome());
                holder.score.setText(ci.getScores());
            }
        } else {
            holder.opponentLogo.setVisibility(View.GONE);
            holder.weekNumber.setVisibility(View.VISIBLE);
            holder.weekDate.setVisibility(View.GONE);
            holder.gameLocation.setVisibility(View.GONE);
            holder.gameTime.setVisibility(View.GONE);
            holder.opponentName.setVisibility(View.GONE);
            holder.byeWeek.setVisibility(View.VISIBLE);
            holder.pm.setVisibility(View.GONE);
            holder.et.setVisibility(View.GONE);
            holder.score.setVisibility(View.GONE);
            holder.outcome.setVisibility(View.GONE);
        }
    }

    @Override
    public TeamScheduleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_schedule, viewGroup, false);
        return new TeamScheduleViewHolder(itemView);
    }

    static class TeamScheduleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.opponentLogo)
        ImageView opponentLogo;
        @BindView(R.id.weekNumber)
        TextView weekNumber;
        @BindView(R.id.weekDate)
        TextView weekDate;
        @BindView(R.id.gameLocation)
        TextView gameLocation;
        @BindView(R.id.gameTime)
        TextView gameTime;
        @BindView(R.id.opponentName)
        TextView opponentName;
        @BindView(R.id.bye_week)
        TextView byeWeek;
        @BindView(R.id.pm)
        TextView pm;
        @BindView(R.id.et)
        TextView et;
        @BindView(R.id.scores)
        TextView score;
        @BindView(R.id.outcome)
        TextView outcome;

        public TeamScheduleViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}