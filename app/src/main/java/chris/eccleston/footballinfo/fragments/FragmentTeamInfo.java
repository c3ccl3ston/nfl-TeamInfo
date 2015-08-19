package chris.eccleston.footballinfo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.activities.TeamActivity;
import chris.eccleston.footballinfo.tasks.UpdateSchedule;
import chris.eccleston.footballinfo.tasks.UpdateTeamInfo;
import chris.eccleston.footballinfo.types.Schedule;
import chris.eccleston.footballinfo.types.Team;
import chris.eccleston.footballinfo.adapters.TeamScheduleAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentTeamInfo extends BaseFragment {
    public static SwipeRefreshLayout refreshTeamInfo;
    public static WebView webView;
    protected static Team mTeam;

    public FragmentTeamInfo() {}

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public FragmentTeamInfo newInstance(Team team) {
        FragmentTeamInfo fragment = new FragmentTeamInfo();
        mTeam = team;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_team_info, container, false);
        webView = (WebView) rootView.findViewById(R.id.teamInfoWebView);
        refreshTeamInfo = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshTeamInfo);
        webView.loadUrl("file:///" + rootView.getContext().getFilesDir().getAbsolutePath() + "/" + mTeam.getTeamName().toLowerCase(Locale.ENGLISH) + "_team_info.html");
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setAllowContentAccess(true);
        refreshTeamInfo.setColorSchemeColors(mTeam.getColorPrimary(), mTeam.getColorAccent());
        refreshTeamInfo.canChildScrollUp();
        refreshTeamInfo.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTeamInfo.setRefreshing(true);
                UpdateTeamInfo updateTask = new UpdateTeamInfo(getActivity());
                updateTask.setSingleTeam(true);
                Team[] team = {mTeam};
                updateTask.execute(team);
            }
        });

        webView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                int scrollY = webView.getScrollY();
                if(scrollY == 0) refreshTeamInfo.setEnabled(true);
                else refreshTeamInfo.setEnabled(false);

            }
        });
        return rootView;
    }
}
