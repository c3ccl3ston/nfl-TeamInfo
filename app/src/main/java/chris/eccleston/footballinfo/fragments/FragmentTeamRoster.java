package chris.eccleston.footballinfo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.activities.BaseActivity;
import chris.eccleston.footballinfo.adapters.TeamRosterAdapter;
import chris.eccleston.footballinfo.tasks.UpdateRoster;
import chris.eccleston.footballinfo.types.Player;
import chris.eccleston.footballinfo.types.Team;

public class FragmentTeamRoster extends Fragment {
    public static SwipeRefreshLayout refreshRosterList;
    public static TeamRosterAdapter ca;
    public static LinearLayoutManager llm;

    public FragmentTeamRoster() {
    }

    /**
     * Returns a new instance of this fragment.
     */
    public static FragmentTeamRoster newInstance(Context mContext, Team team, List<Player> roster) {
        FragmentTeamRoster fragment = new FragmentTeamRoster();

        if (Player.count(Player.class, "team_id = " + (team.getId() - 1), null) == 0) {
            UpdateRoster updateTask = new UpdateRoster(mContext);
            updateTask.setSingleTeam(true);
            updateTask.setSortOrder(BaseActivity.SORT_ORDER);
            Team[] teams = {team};
            updateTask.execute(teams);
        }

        Bundle args = new Bundle();
        args.putLong("teamID", team.getId() - 1);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_team_roster, container, false);
        RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.cardList);
        refreshRosterList = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshRosterList);
        recList.setHasFixedSize(true);

        final Team mTeam = Team.find(Team.class, "team_id = ?", String.valueOf(getArguments().get("teamID"))).get(0);
        refreshRosterList.setColorSchemeColors(mTeam.getColorPrimary(), mTeam.getColorAccent());
        List<Player> mRoster = Player.find(Player.class, "team_id = ?", String.valueOf(getArguments().getLong("teamID")));

        if (mRoster.isEmpty()) {
            refreshRosterList.setRefreshing(true);
        }

        Collections.sort(mRoster);

        llm = new LinearLayoutManager(rootView.getContext());
        recList.setLayoutManager(llm);
        ca = new TeamRosterAdapter(mRoster, this.getActivity(), mTeam);
        recList.setAdapter(ca);

        refreshRosterList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRosterList.setRefreshing(true);
                UpdateRoster updateTask = new UpdateRoster(getActivity());
                updateTask.setSingleTeam(true);
                updateTask.setSortOrder(BaseActivity.SORT_ORDER);
                Team[] team = {mTeam};
                updateTask.execute(team);
            }
        });

        return rootView;
    }
}