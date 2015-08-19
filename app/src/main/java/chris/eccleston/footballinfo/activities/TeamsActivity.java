package chris.eccleston.footballinfo.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.adapters.TeamAdapter;
import chris.eccleston.footballinfo.tasks.UpdateRoster;
import chris.eccleston.footballinfo.tasks.UpdateSchedule;
import chris.eccleston.footballinfo.tasks.UpdateTeamInfo;
import chris.eccleston.footballinfo.types.Team;

public class TeamsActivity extends BaseActivity {

    public static HashMap<String, Team> teamMap = new HashMap<String, Team>();
    public static String PACKAGE_NAME;
    public static Context APPLICATION_CONTEXT;
    public static Activity TEAMS_ACTIVITY;
    public static int teamPosition = -1, offset = 0;
    public static List<Team> teams = new ArrayList<Team>();
    public static TeamAdapter teamAdapter;
    public static boolean initial_load;
    public static Context m;

    protected Menu mOptionsMenu;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.cardList)
    RecyclerView teamsList;
    public static SwipeRefreshLayout refreshTeamsList;
    private StickyRecyclerHeadersDecoration srhd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        APPLICATION_CONTEXT = getApplicationContext();
        PACKAGE_NAME = getApplicationContext().getPackageName();
        TEAMS_ACTIVITY = this;

        m = TeamsActivity.this;

        setTheme(R.style.nfl_theme);

        setContentView(R.layout.activity_teams);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        initial_load = preferences.getBoolean("INITIAL_LOAD", true);
        TEAMS_SORT_ORDER = preferences.getInt("TEAMS_SORT_ORDER", SORT_BY_LOCATION);

        ButterKnife.inject(this);
        refreshTeamsList = (SwipeRefreshLayout) findViewById(R.id.refreshTeamsList);
        setSupportActionBar(toolbar);

        //Tint status bar and toolbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setTintColor(getResources().getColor(R.color.nfl_primary_color_dark));
        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.nfl_primary_color)));
        toolbar.setPopupTheme(R.style.nfl_popup_theme);

        setSortOrder();

        teamsList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        teamsList.setLayoutManager(new LinearLayoutManager(this));

        if (initial_load) {
            initTeamsDB();
        }

        refreshTeamsList.setColorSchemeColors(getResources().getColor(R.color.nfl_primary_color), getResources().getColor(R.color.nfl_color_accent));
        refreshTeamsList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTeamsList.setRefreshing(true);
                UpdateSchedule updateScheduleTask = new UpdateSchedule(getApplicationContext());
                updateScheduleTask.setmSingleTeam(false);
                updateScheduleTask.setTeamAdapter(teamAdapter);
                Team[] teams_array = new Team[32];
                for (int i = 0; i < teams.size(); i++) {
                    teams_array[i] = teams.get(i);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    updateScheduleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, teams_array);
                } else {
                    updateScheduleTask.execute(teams_array);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teams, menu);
        mOptionsMenu = menu;
        mOptionsMenu.getItem(0).setIcon(colorizeIcon(R.drawable.ic_menu_sort_by_size, getResources().getColor(R.color.nfl_color_accent)));
        handleMenuCheckboxes(mOptionsMenu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        mOptionsMenu.getItem(0).setIcon(colorizeIcon(R.drawable.ic_menu_sort_by_size, getResources().getColor(R.color.nfl_color_accent)));
        handleMenuCheckboxes(mOptionsMenu);
        return super.onPrepareOptionsMenu(mOptionsMenu);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_options) {
            handleMenuCheckboxes(mOptionsMenu);
            return super.onOptionsItemSelected(item);
        }
        if (TEAMS_SORT_ORDER == SORT_BY_DIVISION && id != R.id.teams_sort_by_division) {
            if (id != R.id.sort_options) {
                teamsList.removeItemDecoration(srhd);
            }
        }
        if (id == R.id.teams_sort_by_location) {
            TEAMS_SORT_ORDER = SORT_BY_LOCATION;
            sortByLocation(teams);
        }
        if (id == R.id.teams_sort_by_name) {
            TEAMS_SORT_ORDER = SORT_BY_TEAMNAME;
            sortByTeamName(teams);
        }
        if (id == R.id.teams_sort_by_record) {
            TEAMS_SORT_ORDER = SORT_BY_RECORD;
            sortByRecord(teams);
        }
        if (id == R.id.teams_sort_by_division) {
            if (TEAMS_SORT_ORDER != SORT_BY_DIVISION) {
                teamsList.addItemDecoration(srhd);
            }
            TEAMS_SORT_ORDER = SORT_BY_DIVISION;
            sortByDivision(teams);
        }
        teamAdapter.updateList(teams);
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("TEAMS_SORT_ORDER", TEAMS_SORT_ORDER);
        editor.commit();
        return super.onOptionsItemSelected(item);
    }

    public void handleMenuCheckboxes(Menu menu) {
        MenuItem sortByLocation = menu.findItem(R.id.teams_sort_by_location);
        MenuItem sortByTeamName = menu.findItem(R.id.teams_sort_by_name);
        MenuItem sortByRecord = menu.findItem(R.id.teams_sort_by_record);
        MenuItem sortByDivision = menu.findItem(R.id.teams_sort_by_division);
        switch (TEAMS_SORT_ORDER) {
            case SORT_BY_LOCATION:
                sortByLocation.setChecked(true);
                sortByTeamName.setChecked(false);
                sortByRecord.setChecked(false);
                sortByDivision.setChecked(false);
                break;
            case SORT_BY_TEAMNAME:
                sortByLocation.setChecked(false);
                sortByTeamName.setChecked(true);
                sortByRecord.setChecked(false);
                sortByDivision.setChecked(false);
                break;
            case SORT_BY_RECORD:
                sortByLocation.setChecked(false);
                sortByTeamName.setChecked(false);
                sortByRecord.setChecked(true);
                sortByDivision.setChecked(false);
                break;
            case SORT_BY_DIVISION:
                sortByLocation.setChecked(false);
                sortByTeamName.setChecked(false);
                sortByRecord.setChecked(false);
                sortByDivision.setChecked(true);
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (teamPosition != -1) {
            ((LinearLayoutManager) teamsList.getLayoutManager()).scrollToPositionWithOffset(teamPosition, offset);
        }
        teams = Team.listAll(Team.class);
        setSortOrder();
        teamAdapter.updateList(teams);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (teamPosition != -1) {
            ((LinearLayoutManager) teamsList.getLayoutManager()).scrollToPositionWithOffset(teamPosition, offset);
        }
        teams = Team.listAll(Team.class);
        setSortOrder();
        if (teamAdapter != null) {
            teamAdapter.updateList(teams);
        } else {
            teamAdapter = new TeamAdapter(teams, getApplicationContext());
        }

        srhd = new StickyRecyclerHeadersDecoration(teamAdapter);

        if (TEAMS_SORT_ORDER == SORT_BY_DIVISION) {
            teamsList.addItemDecoration(srhd);
        }

        teamsList.setAdapter(teamAdapter);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        initial_load = preferences.getBoolean("INITIAL_LOAD", false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("INITIAL_LOAD", initial_load);
        editor.putInt("TEAMS_SORT_ORDER", TEAMS_SORT_ORDER);
        editor.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("INITIAL_LOAD", initial_load);
        editor.putInt("TEAMS_SORT_ORDER", TEAMS_SORT_ORDER);
        editor.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        teamPosition = ((LinearLayoutManager) teamsList.getLayoutManager()).findFirstVisibleItemPosition();
        try {
            offset = ((LinearLayoutManager) teamsList.getLayoutManager()).getChildAt(0).getTop() - 15;
        } catch (NullPointerException npe) {
            Log.e("ERROR", "Cannot get top of null object");
        }
    }

    private void initTeamsDB() {
        String[] team_locations = getResources().getStringArray(R.array.team_locations);
        String[] team_names = getResources().getStringArray(R.array.team_names);
        String[] team_conference = getResources().getStringArray(R.array.team_conferences);
        String[] team_division = getResources().getStringArray(R.array.team_divisions);
        TypedArray team_logos = getResources().obtainTypedArray(R.array.team_logos);
        TypedArray team_color_primary = getResources().obtainTypedArray(R.array.team_color_primary);
        TypedArray team_color_primary_dark = getResources().obtainTypedArray(R.array.team_color_primary_dark);
        TypedArray team_color_accent = getResources().obtainTypedArray(R.array.team_color_accent);
        TypedArray team_themes = getResources().obtainTypedArray(R.array.team_themes);
        TypedArray team_popup_themes = getResources().obtainTypedArray(R.array.popup_themes);

        for (int i = 0; i < 32; i++) {
            Team ci = new Team(i, team_locations[i], team_names[i], team_logos.getResourceId(i, 0), team_color_primary.getColor(i, 0),
                    team_color_primary_dark.getColor(i, 0), team_color_accent.getColor(i, 0), team_conference[i], team_division[i], team_themes.getResourceId(i, 0), team_popup_themes.getResourceId(i, 0), 0, 0, 0);
            ci.save();
        }

        team_logos.recycle();
        team_color_primary.recycle();
        team_color_primary_dark.recycle();
        team_color_accent.recycle();
        team_themes.recycle();
        team_popup_themes.recycle();

        teams = Team.listAll(Team.class);

        teamAdapter = new TeamAdapter(teams, getApplicationContext());
        teamsList.setAdapter(teamAdapter);

        initial_load = false;
        initPlayersDB();
        initSchedulesDB();
        initTeamInfoFiles();
    }

    private void initPlayersDB() {
        UpdateRoster updateRosterTask = new UpdateRoster(TeamsActivity.this);
        updateRosterTask.setSingleTeam(false);
        Team[] teams_array = new Team[32];
        for (int i = 0; i < teams.size(); i++) {
            teams_array[i] = teams.get(i);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            updateRosterTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, teams_array);
        } else {
            updateRosterTask.execute(teams_array);
        }
    }

    private void initSchedulesDB() {
        UpdateSchedule updateScheduleTask = new UpdateSchedule(getApplicationContext());
        updateScheduleTask.setmSingleTeam(false);
        updateScheduleTask.setTeamAdapter(teamAdapter);
        Team[] teams_array = new Team[32];
        for (int i = 0; i < teams.size(); i++) {
            teams_array[i] = teams.get(i);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            updateScheduleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, teams_array);
        } else {
            updateScheduleTask.execute(teams_array);
        }
    }

    private void initTeamInfoFiles() {
        UpdateTeamInfo updateTeamInfoTask = new UpdateTeamInfo(getApplicationContext());
        updateTeamInfoTask.setSingleTeam(false);
        Team[] teams_array = new Team[32];
        for (int i = 0; i < teams.size(); i++) {
            teams_array[i] = teams.get(i);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            updateTeamInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, teams_array);
        } else {
            updateTeamInfoTask.execute(teams_array);
        }
    }

    public void setSortOrder() {
        switch (TEAMS_SORT_ORDER) {
            case SORT_BY_LOCATION:
                sortByLocation(teams);
                break;
            case SORT_BY_TEAMNAME:
                sortByTeamName(teams);
                break;
            case SORT_BY_RECORD:
                sortByRecord(teams);
                break;
            case SORT_BY_DIVISION:
                sortByDivision(teams);
                break;
        }
    }

    private class UpdateWins extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            for (Team t : Team.listAll(Team.class)) {
                UpdateSchedule updateScheduleTask = new UpdateSchedule(getApplicationContext());
                updateScheduleTask.setmSingleTeam(false);
                updateScheduleTask.setTeamAdapter(teamAdapter);
                Team[] teams_array = new Team[32];
                for (int i = 0; i < teams.size(); i++) {
                    teams_array[i] = teams.get(i);
                }

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                    updateScheduleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, teams_array);
//                } else {
//                    updateScheduleTask.execute(teams_array);
//                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            teams = Team.listAll(Team.class);
            setSortOrder();
            teamAdapter.updateList(teams);
            teamAdapter.notifyDataSetChanged();
            refreshTeamsList.setRefreshing(false);
        }
    }

    private class ClearWins extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            for (Team t : Team.listAll(Team.class)) {
                t.setWins(0);
                t.save();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            teamAdapter.updateList(Team.listAll(Team.class));
            teamAdapter.notifyDataSetChanged();
            refreshTeamsList.setRefreshing(false);
        }
    }
}