package chris.eccleston.footballinfo.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.List;

import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.adapters.WeeklyScheduleAdapter;
import chris.eccleston.footballinfo.types.Game;

/**
 * Created by Chris on 8/20/2015.
 */
public class FragmentWeeklySchedule extends BaseFragment {

    public List<Game> mWeekSchedule;
    public LinearLayoutManager llm;


    protected StickyRecyclerHeadersDecoration mStickyHeaders;

    public FragmentWeeklySchedule() {
    }

    /**
     * Returns a new instance of this fragment.
     */
    public FragmentWeeklySchedule newInstance(List<Game> weekSchedule) {
//        FragmentWeeklySchedule fragment = new FragmentWeeklySchedule();
        mWeekSchedule = weekSchedule;
        this.setRetainInstance(true);
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weekly_schedule, container, false);
        RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.cardList);
//        refreshRosterList = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshRosterList);
        recList.setHasFixedSize(true);
        llm = new LinearLayoutManager(rootView.getContext());
        recList.setLayoutManager(llm);
        WeeklyScheduleAdapter ca = new WeeklyScheduleAdapter(this.getActivity(), mWeekSchedule);
        mStickyHeaders = new StickyRecyclerHeadersDecoration(ca);
        recList.addItemDecoration(mStickyHeaders);
        recList.setAdapter(ca);

//        refreshRosterList.setColorSchemeColors(mTeam.getColorPrimary(), mTeam.getColorAccent());
//        refreshRosterList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refreshRosterList.setRefreshing(true);
//                UpdateRoster updateTask = new UpdateRoster(getActivity());
//                updateTask.setSingleTeam(true);
//                updateTask.setSortOrder(BaseActivity.SORT_ORDER);
//                Team[] team = {mTeam};
//                updateTask.execute(team);
//            }
//        });

        return rootView;
    }
}
