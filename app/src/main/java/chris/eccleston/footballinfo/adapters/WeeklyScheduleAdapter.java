package chris.eccleston.footballinfo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

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
        mWeekSchedule = weekSchedule;
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
    public void onBindViewHolder(WeeklyScheduleViewHolder weeklyScheduleViewHolder, int i) {
        Game mGame = mWeekSchedule.get(i);
//        weeklyScheduleViewHolder.awayScore.setText(ci.getPosition());
//        weeklyScheduleViewHolder.homeScore.setText(ci.getNumber());
        weeklyScheduleViewHolder.awayTeam.setText(mGame.getAwayTeam());
        Team awayTeam = Team.find(Team.class, "team_name = ?", mGame.getAwayTeam()).get(0);
        weeklyScheduleViewHolder.homeTeam.setText(mGame.getHomeTeam());
        Team homeTeam = Team.find(Team.class, "team_name = ?", mGame.getHomeTeam()).get(0);

        weeklyScheduleViewHolder.gameTime.setText(mGame.getGameTime());
        weeklyScheduleViewHolder.awayLogo.setImageResource(awayTeam.getTeamLogo());
        weeklyScheduleViewHolder.homeLogo.setImageResource(homeTeam.getTeamLogo());
        weeklyScheduleViewHolder.pm.setText(mGame.getPm());
    }

    @Override
    public WeeklyScheduleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_weekly_schedule, viewGroup, false);
        return new WeeklyScheduleViewHolder(itemView);
    }

    public class WeeklyScheduleViewHolder extends RecyclerView.ViewHolder {
        protected TextView awayTeam;
        protected TextView homeTeam;
        protected TextView awayScore;
        protected TextView homeScore;
        protected TextView gameTime;
        protected ImageView awayLogo;
        protected ImageView homeLogo;
        protected TextView pm;

        public WeeklyScheduleViewHolder(View v) {
            super(v);
            awayTeam = (TextView) v.findViewById(R.id.weekly_away_team);
            homeTeam = (TextView) v.findViewById(R.id.weekly_home_team);
            awayScore = (TextView) v.findViewById(R.id.weekly_away_score);
            homeScore = (TextView) v.findViewById(R.id.weekly_home_score);
            awayLogo = (ImageView) v.findViewById(R.id.weekly_away_logo);
            homeLogo = (ImageView) v.findViewById(R.id.weekly_home_logo);
            gameTime = (TextView) v.findViewById(R.id.weekly_game_time);
            pm = (TextView) v.findViewById(R.id.weekly_game_time_pm);
        }
    }
}