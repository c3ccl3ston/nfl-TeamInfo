package chris.eccleston.footballinfo.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.activities.BaseActivity;
import chris.eccleston.footballinfo.activities.TeamActivity;
import chris.eccleston.footballinfo.activities.TeamsActivity;
import chris.eccleston.footballinfo.types.Team;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamsViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    protected Context mContext;
    private List<Team> teamsList;
    private boolean isSticky;

    public TeamAdapter(List<Team> teamsList, Context context) {
        this.teamsList = teamsList;
        mContext = context;
    }

    public void updateList(List<Team> data) {
        teamsList = data;
        notifyDataSetChanged();
    }

    @Override
    public long getHeaderId(int position) {
        String division = teamsList.get(position).getConference() + " " + teamsList.get(position).getDivision();
        switch (division) {
            case "AFC East":
                return 1L;
            case "AFC North":
                return 2L;
            case "AFC South":
                return 3L;
            case "AFC West":
                return 4L;
            case "NFC East":
                return 5L;
            case "NFC North":
                return 6L;
            case "NFC South":
                return 7L;
            case "NFC West":
                return 8L;
            default:
                return 0;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.teams_header, viewGroup, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        viewHolder.itemView.setVisibility(TeamsActivity.TEAMS_SORT_ORDER != 3 ? View.GONE : View.VISIBLE);
        TextView textView = (TextView) viewHolder.itemView.findViewById(R.id.conference_header_text);
        textView.setText(teamsList.get(position).getConference() + " " + teamsList.get(position).getDivision());
    }

    @Override
    public int getItemCount() {
        return teamsList.size();
    }

    @Override
    public void onBindViewHolder(TeamsViewHolder holder, int i) {
        Team ci = teamsList.get(i);
        holder.textViewName.setText(ci.location);
        holder.textViewTeamName.setText(ci.teamName);
        holder.textViewTeamRecord.setText("(" + ci.getWins() + " - " + ci.getLosses() + (ci.getTies() != 0 ? " - " + ci.getTies() : "") + ")");
        holder.imageViewLogo.setImageResource(ci.getTeamLogo());
    }

    @Override
    public TeamsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_teams, viewGroup, false);
        return new TeamsViewHolder(itemView, mContext);
    }

    static class TeamsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_view)
        CardView card;
        @BindView(R.id.teamImage)
        ImageView imageViewLogo;
        @BindView(R.id.locationTextView)
        TextView textViewName;
        @BindView(R.id.teamNameText)
        TextView textViewTeamName;
        @BindView(R.id.teamRecord)
        TextView textViewTeamRecord;

        public TeamsViewHolder(View v, Context context) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @OnClick({R.id.card_view})
        public void onClick(View view) {
            int teamID = Team.find(Team.class, "team_name = ?", textViewTeamName.getText().toString()).get(0).getTeamId();
            Intent intent = new Intent(BaseActivity.mContext, TeamActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("teamID", teamID);
            view.getContext().startActivity(intent);
        }
    }
}