package chris.eccleston.footballinfo.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.adapters.TeamScheduleAdapter;
import chris.eccleston.footballinfo.types.Schedule;

public class FragmentTeamNews extends BaseFragment {
    public List<Schedule> teams = new ArrayList<Schedule>();

    public FragmentTeamNews() {
    }

    /**
     * Returns a new instance of this fragment.
     */
    public static FragmentTeamNews newInstance() {
        FragmentTeamNews fragment = new FragmentTeamNews();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_team_schedule, container, false);
        RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);

        recList.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        TeamScheduleAdapter ca = new TeamScheduleAdapter(teams, rootView.getContext());
        recList.setAdapter(ca);

//        recList.setOnTouchListener(this);

        return rootView;
    }
}