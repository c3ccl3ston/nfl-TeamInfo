package chris.eccleston.footballinfo.activities;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import chris.eccleston.footballinfo.DepthPageTransformer;
import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.adapters.TeamFragmentAdapter;
import chris.eccleston.footballinfo.fragments.FragmentTeamRoster;
import chris.eccleston.footballinfo.tasks.GetPlayerInfo;
import chris.eccleston.footballinfo.types.Player;
import chris.eccleston.footballinfo.types.Team;

public class TeamActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    public static int sortOrder = R.id.roster_sort_by_name;
    protected static int SORT_ORDER = 0;
    public Team mTeam;
    public int mColorAccent;
    public int mColorPrimary;
    protected String mTeamLocation;
    protected String mTeamName;
    protected List<Player> mRoster;
    protected Menu mOptionsMenu;
    int mColorDark;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabs)
    TabLayout mTabs;
    @BindView(R.id.my_pager)
    ViewPager mPager;
    int mTabPosition = -1;
    TeamFragmentAdapter mfa;
    TeamActivity prevActivity = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Intent intent = getIntent();
        mTeam = Team.find(Team.class, "team_id = ?", String.valueOf(intent.getIntExtra(TEAM_ID, 0))).get(0);
        mRoster = Player.find(Player.class, "team_id = ?", String.valueOf(mTeam.getTeamId()));

        Collections.sort(mRoster);
        mTeamLocation = mTeam.getLocation();
        mTeamName = mTeam.getTeamName();
        mColorPrimary = mTeam.getColorPrimary();
        mColorAccent = mTeam.getColorAccent();
        mColorDark = mTeam.getColorPrimaryDark();
        int theme = mTeam.getThemeId();
        int popup_theme = mTeam.getTeamPopupId();

        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        mContext = this;

        ButterKnife.bind(this);
        mToolbar.setPopupTheme(popup_theme);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_material);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setToolbarColors(mToolbar, mColorPrimary, mColorDark, mColorAccent);

        setTitle(mTeamLocation + " " + mTeamName);
        updateSubtitle(mTeam);

        mTabs.setTabTextColors(mColorAccent, mColorAccent);
        mTabs.setBackgroundColor(mColorPrimary);

        mPager.setPageTransformer(true, new DepthPageTransformer());
        mPager.setOffscreenPageLimit(2);
    }

    public void updateSubtitle(Team team) {
        if (team.getTies() != 0) {
            mToolbar.setSubtitle("(" + team.getWins() + " - " + team.getLosses() + " - " + team.getTies() + ")");
        } else {
            mToolbar.setSubtitle("(" + team.getWins() + " - " + team.getLosses() + ")");
        }
    }

    public Drawable colorizeIcon(int icon, int color) {
        Drawable arrowDrawable = ContextCompat.getDrawable(mContext, icon);
        Drawable wrapped = DrawableCompat.wrap(arrowDrawable);

        if (arrowDrawable != null && wrapped != null) {
            arrowDrawable.mutate();
            DrawableCompat.setTint(wrapped, color);
        }

        return wrapped;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTabPosition = mTabs.getSelectedTabPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTabPosition != -1) {
            mPager.setCurrentItem(mTabPosition);
        } else {
            mPager.setCurrentItem(1);
        }

        Bitmap icon = BitmapFactory.decodeResource(getResources(), mTeam.getTeamLogo());
        ActivityManager.TaskDescription tDesc = new ActivityManager.TaskDescription(mTeamLocation + " " + mTeamName, icon, mColorPrimary);
        this.setTaskDescription(tDesc);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Initialize the ViewPager and set an adapter
        mfa = new TeamFragmentAdapter(getSupportFragmentManager(), this, mTeam, mRoster);
        mPager.setAdapter(mfa);

        mTabs.setupWithViewPager(mPager);
        mTabs.setTabMode(TabLayout.MODE_FIXED);
        mPager.addOnPageChangeListener(this);

        prevActivity = (TeamActivity) getLastCustomNonConfigurationInstance();
        if (prevActivity != null) {
            mPager.setCurrentItem(prevActivity.mTabPosition);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        SORT_ORDER = savedInstanceState.getInt("TEAMS_SORT_ORDER");
        if (savedInstanceState != null) {
            mTabPosition = savedInstanceState.getInt("TAB");
        }
        if (savedInstanceState.getBoolean("CARD_SHOWING")) {
            GetPlayerInfo.showCard();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (GetPlayerInfo.progress != null) {
            if (GetPlayerInfo.progress.isShowing()) {
                GetPlayerInfo.progress.cancel();
            }
        }
        super.onSaveInstanceState(outState);
        outState.putInt("TEAMS_SORT_ORDER", SORT_ORDER);
        outState.putInt("TAB", mTabs.getSelectedTabPosition());
        if (GetPlayerInfo.dialog != null) {
            if (GetPlayerInfo.dialog.isShowing()) {
                GetPlayerInfo.dialog.dismiss();
                outState.putBoolean("CARD_SHOWING", true);
            } else {
                outState.putBoolean("CARD_SHOWING", false);
            }

        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        getMenuInflater().inflate(R.menu.menu_team_roster, mOptionsMenu);
        menu.findItem(R.id.sort).setVisible(mPager.getCurrentItem() != 0 ? false : true);
        mOptionsMenu.getItem(0).setIcon(colorizeIcon(R.drawable.ic_menu_sort_by_size, mColorAccent));
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        mOptionsMenu.getItem(0).setIcon(colorizeIcon(R.drawable.ic_menu_sort_by_size, mColorAccent));
        return super.onPrepareOptionsMenu(mOptionsMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.isChecked() && item.getItemId() != R.id.sort) {
            Collections.reverse(mRoster);
        } else {
            MenuItem sort_item_name = mOptionsMenu.findItem(R.id.roster_sort_by_name);
            MenuItem sort_item_position = mOptionsMenu.findItem(R.id.roster_sort_by_position);
            MenuItem sort_item_number = mOptionsMenu.findItem(R.id.roster_sort_by_number);

            switch (id) {
                case R.id.sort:
                    return super.onOptionsItemSelected(item);
                case R.id.roster_sort_by_name:
                    sort_item_name.setChecked(true);
                    sort_item_position.setChecked(false);
                    sort_item_number.setChecked(false);
                    break;
                case R.id.roster_sort_by_number:
                    sort_item_name.setChecked(false);
                    sort_item_position.setChecked(false);
                    sort_item_number.setChecked(true);
                    break;
                case R.id.roster_sort_by_position:
                    sort_item_name.setChecked(false);
                    sort_item_position.setChecked(true);
                    sort_item_number.setChecked(false);
                    break;
            }

            sortOrder = id;
            Collections.sort(mRoster);
        }
        FragmentTeamRoster.ca.updateList(mRoster);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}