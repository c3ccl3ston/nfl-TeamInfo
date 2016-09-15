package chris.eccleston.footballinfo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.tasks.GetPlayerInfo;
import chris.eccleston.footballinfo.types.Player;
import chris.eccleston.footballinfo.types.Team;

public class TeamRosterAdapter extends RecyclerView.Adapter<TeamRosterAdapter.TeamRosterViewHolder> {

    protected List<Player> mRosterList;
    protected Context mContext;
    protected Team mTeam;

    public TeamRosterAdapter(List<Player> rosterList, Context context, Team team) {
        mRosterList = rosterList;
        mContext = context;
        mTeam = team;
    }

    public void updateList(List<Player> data) {
        mRosterList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mRosterList.size();
    }

    @Override
    public void onBindViewHolder(TeamRosterViewHolder holder, int i) {
        Player ci = mRosterList.get(i);
        holder.playerNumber.setText(ci.getNumber());
        holder.playerName.setText(ci.getLastName() + ", " + ci.getFirstName());
        holder.playerPosition.setText(ci.getPosition());
    }

    @Override
    public TeamRosterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_roster, viewGroup, false);
        return new TeamRosterViewHolder(itemView);
    }

    public class TeamRosterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rosterNumber)
        TextView playerNumber;
        @BindView(R.id.rosterName)
        TextView playerName;
        @BindView(R.id.rosterPos)
        TextView playerPosition;

        public TeamRosterViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @OnClick({R.id.rosterCard})
        public void onClick(View v) {
            String playerUrl = mRosterList.get(this.getAdapterPosition()).getLink();
            GetPlayerInfo playerInfoTask = new GetPlayerInfo(mContext, mTeam, mRosterList.get(this.getAdapterPosition()));
            playerInfoTask.execute(playerUrl);
        }
    }
}