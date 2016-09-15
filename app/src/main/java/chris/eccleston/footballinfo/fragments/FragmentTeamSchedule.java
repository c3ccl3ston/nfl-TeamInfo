package chris.eccleston.footballinfo.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.adapters.TeamScheduleAdapter;
import chris.eccleston.footballinfo.tasks.UpdateSchedule;
import chris.eccleston.footballinfo.types.Schedule;
import chris.eccleston.footballinfo.types.Team;

public class FragmentTeamSchedule extends BaseFragment {
    public static SwipeRefreshLayout refreshScheduleList;
    public static TeamScheduleAdapter ca;
    public static LinearLayoutManager llm;

    public FragmentTeamSchedule() {}

    /**
     * Returns a new instance of this fragment.
     */
    public static FragmentTeamSchedule newInstance(Team team) {
        FragmentTeamSchedule fragment = new FragmentTeamSchedule();

        Bundle args = new Bundle();
        args.putLong("teamID", team.getId() - 1);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_team_schedule, container, false);
        RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.cardList);
        refreshScheduleList = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshScheduleList);
        recList.setHasFixedSize(true);
        llm = new LinearLayoutManager(rootView.getContext());
        recList.setLayoutManager(llm);

        final Team mTeam = Team.find(Team.class, "team_id = ?", String.valueOf(getArguments().get("teamID"))).get(0);
        List<Schedule> schedule = Schedule.find(Schedule.class, "team_id = ?", String.valueOf(getArguments().getLong("teamID")));

        ca = new TeamScheduleAdapter(schedule, rootView.getContext());
        recList.setAdapter(ca);

        refreshScheduleList.setColorSchemeColors(mTeam.getColorPrimary(), mTeam.getColorAccent());
        refreshScheduleList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshScheduleList.setRefreshing(true);
                UpdateSchedule updateTask = new UpdateSchedule(getActivity());
                updateTask.setSingleTeam(true);
                Team[] team = {mTeam};
                updateTask.execute(team);
            }
        });

        return rootView;
    }
}