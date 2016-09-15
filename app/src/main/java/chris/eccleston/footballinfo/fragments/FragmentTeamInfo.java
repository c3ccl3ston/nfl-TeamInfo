package chris.eccleston.footballinfo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.util.Locale;

import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.tasks.UpdateTeamInfo;
import chris.eccleston.footballinfo.types.Team;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentTeamInfo extends BaseFragment {
    public static SwipeRefreshLayout refreshTeamInfo;
    public static WebView webView;

    protected static Context mContext;

    public FragmentTeamInfo() {}

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentTeamInfo newInstance(Context c, Team team) {
        FragmentTeamInfo fragment = new FragmentTeamInfo();
        mContext = c;

        File f = new File(mContext.getFilesDir().getAbsolutePath() + "/" + team.getTeamName().toLowerCase(Locale.ENGLISH) + "_team_info.html");
        if (!f.exists()) {
            UpdateTeamInfo updateTask = new UpdateTeamInfo(mContext);
            updateTask.setSingleTeam(true);
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
        View rootView = inflater.inflate(R.layout.fragment_team_info, container, false);
        webView = (WebView) rootView.findViewById(R.id.teamInfoWebView);
        refreshTeamInfo = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshTeamInfo);

        final Team mTeam = Team.find(Team.class, "team_id = ?", String.valueOf(getArguments().get("teamID"))).get(0);

        refreshTeamInfo.setColorSchemeColors(mTeam.getColorPrimary(), mTeam.getColorAccent());
        refreshTeamInfo.canChildScrollUp();

        File f = new File(mContext.getFilesDir().getAbsolutePath() + "/" + mTeam.getTeamName().toLowerCase(Locale.ENGLISH) + "_team_info.html");
        if (!f.exists()) {
            refreshTeamInfo.setRefreshing(true);
        }

        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setAllowContentAccess(true);
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webView.loadData("<HTML><BODY></BODY></HTML>", "text/html", "utf-8");
            }
        });

        webView.loadUrl("file://" + f.getAbsolutePath());

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
