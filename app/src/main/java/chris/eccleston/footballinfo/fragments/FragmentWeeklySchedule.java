package chris.eccleston.footballinfo.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.List;

import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.adapters.WeeklyScheduleAdapter;
import chris.eccleston.footballinfo.tasks.UpdateWeeklySchedules;
import chris.eccleston.footballinfo.types.Game;

/**
 * Created by Chris on 8/20/2015.
 */
public class FragmentWeeklySchedule extends BaseFragment {

    public List<Game> mWeekSchedule;
    public LinearLayoutManager llm;
    public SwipeRefreshLayout refreshWeeklySchedule;
    public Context mContext;
    public FragmentWeeklySchedule mFragmentWeeklySchedule;
    public WeeklyScheduleAdapter ca;

    protected StickyRecyclerHeadersDecoration mStickyHeaders;

    public FragmentWeeklySchedule() {
        mFragmentWeeklySchedule = this;
    }

    /**
     * Returns a new instance of this fragment.
     */
    public static FragmentWeeklySchedule newInstance(Context context, int weekNumber) {
        FragmentWeeklySchedule fragment = new FragmentWeeklySchedule();

        Bundle args = new Bundle();
        args.putInt("weekNumber", weekNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public void stopRefresh() {
        refreshWeeklySchedule.setRefreshing(false);
    }

    public void updateList() {
        mWeekSchedule = Game.find(Game.class, "week_number = ?", String.valueOf(getArguments().get("weekNumber")));
        ca.updateList(mWeekSchedule);
        ca.notifyDataSetChanged();
    }

    public void stopRefreshing() {
        refreshWeeklySchedule.setRefreshing(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weekly_schedule, container, false);
        RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.cardList);
        refreshWeeklySchedule = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshWeeklySchedule);

        mWeekSchedule = Game.find(Game.class, "week_number = ?", String.valueOf(getArguments().get("weekNumber")));

        final int wN = getArguments().getInt("weekNumber");
        recList.setHasFixedSize(true);
        llm = new LinearLayoutManager(rootView.getContext());
        recList.setLayoutManager(llm);
        ca = new WeeklyScheduleAdapter(this.getActivity(), mWeekSchedule);
        mStickyHeaders = new StickyRecyclerHeadersDecoration(ca);
        recList.addItemDecoration(mStickyHeaders);
        recList.setAdapter(ca);

        refreshWeeklySchedule.setColorSchemeColors(getResources().getColor(R.color.nfl_primary_color), getResources().getColor(R.color.nfl_color_accent));
        refreshWeeklySchedule.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWeeklySchedule.setRefreshing(true);
                UpdateWeeklySchedules task = new UpdateWeeklySchedules(mFragmentWeeklySchedule);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    task.execute();
                }
            }
        });

        return rootView;
    }
}
