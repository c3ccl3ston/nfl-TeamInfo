package chris.eccleston.footballinfo.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import chris.eccleston.footballinfo.activities.BaseActivity;
import chris.eccleston.footballinfo.types.Player;
import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.types.Team;
import chris.eccleston.footballinfo.activities.TeamActivity;
import chris.eccleston.footballinfo.adapters.TeamRosterAdapter;
import chris.eccleston.footballinfo.tasks.UpdateRoster;

public class FragmentTeamRoster extends BaseFragment {
    public static SwipeRefreshLayout refreshRosterList;
    public static TeamRosterAdapter ca;
    public static LinearLayoutManager llm;
    protected static Team mTeam;
    public static List<Player> roster = new ArrayList<Player>();

    public FragmentTeamRoster() {}

    /**
     * Returns a new instance of this fragment.
     */
    public FragmentTeamRoster newInstance(Team team) {
        FragmentTeamRoster fragment = new FragmentTeamRoster();
        mTeam = team;
        roster = Player.find(Player.class, "team_id = ?", String.valueOf(mTeam.getTeamId()));
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_team_roster, container, false);
        RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.cardList);
        refreshRosterList = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshRosterList);
        recList.setHasFixedSize(true);
        llm = new LinearLayoutManager(rootView.getContext());
        recList.setLayoutManager(llm);
        ca = new TeamRosterAdapter(roster, this.getActivity(), mTeam);
        recList.setAdapter(ca);

        refreshRosterList.setColorSchemeColors(mTeam.getColorPrimary(), mTeam.getColorAccent());
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