package chris.eccleston.footballinfo.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import chris.eccleston.footballinfo.R;
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

    public void setSticky(boolean isSticky) {
        this.isSticky = isSticky;
    }

    public void updateList(List<Team> data) {
        teamsList = data;
        notifyDataSetChanged();
    }

    @Override
    public long getHeaderId(int position) {
        String division = teamsList.get(position).getConference() + " " + teamsList.get(position).getDivision();
        if (division.equals("AFC East")) {
            return 1L;
        } else if (division.equals("AFC North")) {
            return 2L;
        } else if (division.equals("AFC South")) {
            return 3L;
        } else if (division.equals("AFC West")) {
            return 4L;
        } else if (division.equals("NFC East")) {
            return 5L;
        } else if (division.equals("NFC North")) {
            return 6L;
        } else if (division.equals("NFC South")) {
            return 7L;
        } else {
            return 8L;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        if (TeamsActivity.TEAMS_SORT_ORDER == 3) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.teams_header, viewGroup, false);
            return new RecyclerView.ViewHolder(view) {
            };
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.teams_header, viewGroup, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (TeamsActivity.TEAMS_SORT_ORDER != 3) {
            TextView textView = (TextView) viewHolder.itemView;
            textView.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) viewHolder.itemView;
            textView.setText(teamsList.get(position).getConference() + " " + teamsList.get(position).getDivision());
            textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return teamsList.size();
    }

    @Override
    public void onBindViewHolder(TeamsViewHolder teamViewHolder, int i) {
        Team ci = teamsList.get(i);
        teamViewHolder.textViewName.setText(ci.location);
        teamViewHolder.textViewTeamName.setText(ci.teamName);
        if (ci.getTies() != 0) {
            teamViewHolder.textViewTeamRecord.setText("(" + ci.getWins() + " - " + ci.getLosses() + " - " + ci.getTies() + ")");
        } else {
            teamViewHolder.textViewTeamRecord.setText("(" + ci.getWins() + " - " + ci.getLosses() + ")");
        }
        teamViewHolder.imageViewLogo.setImageResource(ci.getTeamLogo());
    }

    @Override
    public TeamsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_teams, viewGroup, false);
        return new TeamsViewHolder(itemView, mContext);
    }

    public static class TeamsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        protected Context mContext;
        protected CardView card;
        protected ImageView imageViewLogo;
        protected TextView textViewName;
        protected TextView textViewTeamName;
        protected TextView textViewTeamRecord;
        protected CheckBox checkBoxFavTeam;
        protected ImageView imageViewFavTeam;

        public TeamsViewHolder(View v, Context context) {
            super(v);
            mContext = context;
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
            imageViewLogo = (ImageView) v.findViewById(R.id.teamImage);
            textViewName = (TextView) v.findViewById(R.id.locationTextView);
            textViewTeamName = (TextView) v.findViewById(R.id.teamNameText);
            textViewTeamRecord = (TextView) v.findViewById(R.id.teamRecord);
            checkBoxFavTeam = (CheckBox) v.findViewById(R.id.fav_selected);
            imageViewFavTeam = (ImageView) v.findViewById(R.id.fav_image);
            card = (CardView) v.findViewById(R.id.card_view);
        }

        @Override
        public void onClick(View view) {
            int teamID = Team.find(Team.class, "team_name = ?", textViewTeamName.getText().toString()).get(0).getTeamId();
            Intent intent = new Intent(TeamsActivity.APPLICATION_CONTEXT, TeamActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("teamID", teamID);
            mContext.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            String text = textViewName.getText().toString() + " " + textViewTeamName.getText().toString();
            Toast.makeText(TeamsActivity.APPLICATION_CONTEXT, text, Toast.LENGTH_LONG).show();
            return true;
        }
    }
}
