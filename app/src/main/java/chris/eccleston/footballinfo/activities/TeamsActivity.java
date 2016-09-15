package chris.eccleston.footballinfo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
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

import com.orm.SugarContext;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.adapters.TeamAdapter;
import chris.eccleston.footballinfo.tasks.UpdateSchedule;
import chris.eccleston.footballinfo.tasks.UpdateWeeklySchedules;
import chris.eccleston.footballinfo.types.Team;

public class TeamsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static int teamPosition = -1, offset = 0;
    public static List<Team> teams = new ArrayList<Team>();
    public static TeamAdapter teamAdapter;
    public static SwipeRefreshLayout refreshTeamsList;
    protected Menu mOptionsMenu;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cardList)
    RecyclerView teamsList;
    @BindColor(R.color.nfl_color_accent)
    int NFLColorAccent;
    @BindColor(R.color.nfl_primary_color)
    int NFLColorPrimary;
    @BindColor(R.color.nfl_primary_color_dark)
    int NFLColorDark;
    @BindArray(R.array.team_locations)
    String[] teamLocations;
    @BindArray(R.array.team_names)
    String[] teamNames;
    @BindArray(R.array.team_conferences)
    String[] teamConferences;
    @BindArray(R.array.team_divisions)
    String[] teamDivisions;
    @BindArray(R.array.team_logos)
    TypedArray teamLogos;
    @BindArray(R.array.team_color_primary)
    TypedArray teamPrimaryColors;
    @BindArray(R.array.team_color_accent)
    TypedArray teamAccentColors;
    @BindArray(R.array.popup_themes)
    TypedArray teamPopupThemes;
    private StickyRecyclerHeadersDecoration srhd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SugarContext.init(this);
        setContentView(R.layout.activity_teams);
        ButterKnife.bind(this);

        mContext = getApplicationContext();

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        TEAMS_SORT_ORDER = preferences.getInt("TEAMS_SORT_ORDER", SORT_BY_LOCATION);

        refreshTeamsList = (SwipeRefreshLayout) findViewById(R.id.refreshTeamsList);
        setSupportActionBar(toolbar);
        setToolbarColors(toolbar, NFLColorPrimary, NFLColorDark, NFLColorAccent);
        toolbar.setPopupTheme(R.style.nfl_popup_theme);

        setSortOrder();

        teamsList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        teamsList.setLayoutManager(new LinearLayoutManager(this));

        refreshTeamsList.setColorSchemeColors(NFLColorPrimary, NFLColorAccent);
        refreshTeamsList.setOnRefreshListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        long teamsSize = Team.count(Team.class);
        if (teamsSize <= 0) {
            initTeamsDB();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teams, menu);
        mOptionsMenu = menu;
        handleMenuCheckboxes(mOptionsMenu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
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

        if (item.isChecked() && item.getItemId() != R.id.sort_options) {
            Collections.reverse(teams);
        } else {

        if (id == R.id.weekly_schedule) {
            Intent intent = new Intent(mContext, WeeklyScheduleActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            return super.onOptionsItemSelected(item);
        }

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
            }
            if (id == R.id.teams_sort_by_name) {
                TEAMS_SORT_ORDER = SORT_BY_TEAMNAME;
            }
            if (id == R.id.teams_sort_by_record) {
                TEAMS_SORT_ORDER = SORT_BY_RECORD;
            }
            if (id == R.id.teams_sort_by_division) {
                if (TEAMS_SORT_ORDER != SORT_BY_DIVISION) {
                    teamsList.addItemDecoration(srhd);
                }
                TEAMS_SORT_ORDER = SORT_BY_DIVISION;
            }
            Collections.sort(teams);
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
        try {
            teams = Team.listAll(Team.class);
        } catch (Exception e) {
        }
        setSortOrder();
        if (teamAdapter != null) {
            teamAdapter.updateList(teams);
        } else {
            teamAdapter = new TeamAdapter(teams, getApplicationContext());
        }

        if (srhd == null) {
            srhd = new StickyRecyclerHeadersDecoration(teamAdapter);


            if (TEAMS_SORT_ORDER == SORT_BY_DIVISION) {
                teamsList.addItemDecoration(srhd);
            }
        }

        teamsList.setAdapter(teamAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("TEAMS_SORT_ORDER", TEAMS_SORT_ORDER);
        editor.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("TEAMS_SORT_ORDER", TEAMS_SORT_ORDER);
        editor.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        teamPosition = ((LinearLayoutManager) teamsList.getLayoutManager()).findFirstVisibleItemPosition();
        try {
            offset = teamsList.getLayoutManager().getChildAt(0).getTop() - 15;
        } catch (NullPointerException npe) {
            Log.e("ERROR", "Cannot get top of null object");
        }
    }

    private void initTeamsDB() {
        TypedArray team_color_primary_dark = getResources().obtainTypedArray(R.array.team_color_primary_dark);
        TypedArray team_themes = getResources().obtainTypedArray(R.array.team_themes);

        for (int i = 0; i < 32; i++) {
            Team ci = new Team(i, teamLocations[i], teamNames[i], teamLogos.getResourceId(i, 0), teamPrimaryColors.getColor(i, 0),
                    team_color_primary_dark.getColor(i, 0), teamAccentColors.getColor(i, 0), teamConferences[i], teamDivisions[i], team_themes.getResourceId(i, 0), teamPopupThemes.getResourceId(i, 0), 0, 0, 0);
            ci.save();
        }

        teams = Team.listAll(Team.class);

        teamAdapter = new TeamAdapter(teams, getApplicationContext());
        teamsList.setAdapter(teamAdapter);
        teamAdapter.notifyDataSetChanged();

        teamLogos.recycle();
        teamPrimaryColors.recycle();
        team_color_primary_dark.recycle();
        teamAccentColors.recycle();
        team_themes.recycle();
        teamPopupThemes.recycle();

//        initPlayersDB();
//        initWeeklySchedules();
        initSchedulesDB();
//        initTeamInfoFiles();
    }

    private void initWeeklySchedules() {
        UpdateWeeklySchedules updateRosterTask = new UpdateWeeklySchedules();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            updateRosterTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            updateRosterTask.execute();
        }
    }

//    private void initPlayersDB() {
//        UpdateRoster updateRosterTask = new UpdateRoster(TeamsActivity.this);
//        updateRosterTask.setSingleTeam(false);
//        Team[] teams_array = new Team[32];
//        for (int i = 0; i < teams.size(); i++) {
//            teams_array[i] = teams.get(i);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            updateRosterTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, teams_array);
//        } else {
//            updateRosterTask.execute(teams_array);
//        }
//    }

    private void initSchedulesDB() {
        UpdateSchedule updateScheduleTask = new UpdateSchedule(TeamsActivity.this);
        updateScheduleTask.setSingleTeam(false);
        updateScheduleTask.setInitialLoad(true);
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

//    private void initTeamInfoFiles() {
//        UpdateTeamInfo updateTeamInfoTask = new UpdateTeamInfo(getApplicationContext());
//        updateTeamInfoTask.setSingleTeam(false);
//        Team[] teams_array = new Team[32];
//        for (int i = 0; i < teams.size(); i++) {
//            teams_array[i] = teams.get(i);
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            updateTeamInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, teams_array);
//        } else {
//            updateTeamInfoTask.execute(teams_array);
//        }
//    }

    public void setSortOrder() {
        Collections.sort(teams);
    }

    @Override
    public void onRefresh() {
        refreshTeamsList.setRefreshing(true);
        UpdateSchedule updateScheduleTask = new UpdateSchedule(getApplicationContext());
        updateScheduleTask.setSingleTeam(false);
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
}