package chris.eccleston.footballinfo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.types.Game;
import chris.eccleston.footballinfo.types.Team;

/**
 * Created by Chris on 8/20/2015.
 */
public class WeeklyScheduleAdapter extends RecyclerView.Adapter<WeeklyScheduleAdapter.WeeklyScheduleViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    protected List<Game> mWeekSchedule;
    protected Context mContext;

    public WeeklyScheduleAdapter(Context context, List<Game> weekSchedule) {
        mContext = context;
        this.mWeekSchedule = weekSchedule;
    }

    public void updateList(List<Game> data) {
        mWeekSchedule = data;
        notifyDataSetChanged();
    }

    @Override
    public long getHeaderId(int position) {
        String header = mWeekSchedule.get(position).getGameHeader();
        return Long.valueOf(header.substring(0, header.indexOf(' ')));
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.weekly_schedule_header, viewGroup, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        TextView textView = (TextView) viewHolder.itemView.findViewById(R.id.date_header);
        String header = mWeekSchedule.get(position).getGameHeader();
        textView.setText(header.substring(header.indexOf(' ') + 1));
    }

    @Override
    public int getItemCount() {
        if (mWeekSchedule == null) {
            return 0;
        }
        return mWeekSchedule.size();
    }


    @Override
    public void onBindViewHolder(WeeklyScheduleViewHolder holder, int i) {
        Game mGame = mWeekSchedule.get(i);

        holder.awayScore.setVisibility(View.GONE);
        holder.homeScore.setVisibility(View.GONE);

        holder.awayLogo.setVisibility(View.VISIBLE);
        holder.homeLogo.setVisibility(View.VISIBLE);
        holder.gameTime.setVisibility(View.VISIBLE);

        holder.awayTeam.setText(mGame.getAwayTeam());
        Team awayTeam = Team.find(Team.class, "team_name = ?", mGame.getAwayTeam()).get(0);
        holder.homeTeam.setText(mGame.getHomeTeam());
        Team homeTeam = Team.find(Team.class, "team_name = ?", mGame.getHomeTeam()).get(0);

        if (mGame.getGameTime().equals("FINAL")) {
            if (mGame.wasOvertime) {
                holder.overTime.setVisibility(View.VISIBLE);
            } else {
                holder.overTime.setVisibility(View.GONE);
            }
            holder.homeScore.setVisibility(View.VISIBLE);
            holder.awayScore.setVisibility(View.VISIBLE);
            holder.pm.setVisibility(View.GONE);
            holder.et.setVisibility(View.GONE);

            holder.gameTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            holder.gameTime.setText(mGame.getGameTime());
            holder.homeScore.setText(mGame.getHomeScore());
            holder.awayScore.setText(mGame.getAwayScore());
        } else {
            holder.pm.setVisibility(View.VISIBLE);
            holder.et.setVisibility(View.VISIBLE);
            holder.homeScore.setVisibility(View.GONE);
            holder.awayScore.setVisibility(View.GONE);

            holder.gameTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);
            holder.gameTime.setText(mGame.getGameTime());
            holder.pm.setText(mGame.getPm());
        }
        holder.awayLogo.setImageResource(awayTeam.getTeamLogo());
        holder.homeLogo.setImageResource(homeTeam.getTeamLogo());
    }

    @Override
    public WeeklyScheduleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_weekly_schedule, viewGroup, false);
        return new WeeklyScheduleViewHolder(itemView);
    }

    public class WeeklyScheduleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.weekly_away_team)
        TextView awayTeam;
        @BindView(R.id.weekly_home_team)
        TextView homeTeam;
        @BindView(R.id.weekly_away_score)
        TextView awayScore;
        @BindView(R.id.weekly_home_score)
        TextView homeScore;
        @BindView(R.id.weekly_game_time)
        TextView gameTime;
        @BindView(R.id.weekly_away_logo)
        ImageView awayLogo;
        @BindView(R.id.weekly_home_logo)
        ImageView homeLogo;
        @BindView(R.id.weekly_game_time_pm)
        TextView pm;
        @BindView(R.id.weekly_game_time_et)
        TextView et;
        @BindView(R.id.weekly_game_was_ot)
        TextView overTime;

        public WeeklyScheduleViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}