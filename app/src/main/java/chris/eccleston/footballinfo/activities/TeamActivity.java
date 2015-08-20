package chris.eccleston.footballinfo.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import chris.eccleston.footballinfo.DepthPageTransformer;
import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.adapters.MyFragmentAdapter;
import chris.eccleston.footballinfo.fragments.FragmentTeamRoster;
import chris.eccleston.footballinfo.tasks.GetPlayerInfo;
import chris.eccleston.footballinfo.types.Player;
import chris.eccleston.footballinfo.types.Team;

public class TeamActivity extends BaseActivity implements ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener {

    public Team mTeam;

    public int mColorAccent;
    public int mColorPrimary;

    protected String mTeamLocation;
    protected String mTeamName;

    protected List<Player> mRoster;

    protected Menu mOptionsMenu;

    @InjectView(R.id.tabs)
    TabLayout mTabs;
    @InjectView(R.id.my_pager)
    ViewPager mPager;

    boolean mPlayerCardShowing = false;

    MyFragmentAdapter mfa;

    Context mContext;

    int current_tab = 1;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mTeam = Team.find(Team.class, "team_id = ?", String.valueOf(intent.getIntExtra(TEAM_ID, 0))).get(0);
        mRoster = Player.find(Player.class, "team_id = ?", String.valueOf(mTeam.getTeamId()));

        sortRosterByName(mRoster);
        mTeamLocation = mTeam.getLocation();
        mTeamName = mTeam.getTeamName();
        mColorPrimary = mTeam.getColorPrimary();
        mColorAccent = mTeam.getColorAccent();
        int theme = mTeam.getThemeId();
        int popup_theme = mTeam.getTeamPopupId();

        setTheme(theme);

        setContentView(R.layout.activity_team);

        mContext = this;

        ButterKnife.inject(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setPopupTheme(popup_theme);
        setTheme(theme);
        setSupportActionBar(mToolbar);

        // Initialize the ViewPager and set an adapter
        mfa = new MyFragmentAdapter(getSupportFragmentManager(), this, mTeam, mRoster);
        mPager.setAdapter(mfa);

        mTabs.setupWithViewPager(mPager);
        mPager.addOnPageChangeListener(this);

        setTitle(mTeamLocation + " " + mTeamName);
        updateSubtitle(mTeam);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setTintColor(darken(mColorPrimary, 0.25));
            getWindow().setStatusBarColor(darken(mColorPrimary, 0.25));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mColorPrimary));
        mTabs.setTabTextColors(mColorAccent, mColorAccent);
        mTabs.setBackgroundColor(mColorPrimary);
        mTabs.setOnTabSelectedListener(this);
        mToolbar.setTitleTextColor(mColorAccent);
        mToolbar.setSubtitleTextColor(mColorAccent);
        getSupportActionBar().setHomeAsUpIndicator(colorizeIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha, mColorAccent));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPager.setPageTransformer(true, new DepthPageTransformer());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        SORT_ORDER = savedInstanceState.getInt("TEAMS_SORT_ORDER");
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
        if (GetPlayerInfo.dialog != null) {
            if (GetPlayerInfo.dialog.isShowing()) {
                GetPlayerInfo.dialog.dismiss();
                outState.putBoolean("CARD_SHOWING", true);
            } else {
                outState.putBoolean("CARD_SHOWING", false);
            }

        }
    }

    public void handleMenuCheckboxes(Menu menu) {
        mOptionsMenu = menu;
        MenuItem sort_item_name = mOptionsMenu.findItem(R.id.roster_sort_by_name);
        MenuItem sort_item_position = mOptionsMenu.findItem(R.id.roster_sort_by_position);
        MenuItem sort_item_number = mOptionsMenu.findItem(R.id.roster_sort_by_number);
        switch (SORT_ORDER) {
            case SORT_BY_NAME:
                sort_item_name.setChecked(true);
                sort_item_position.setChecked(false);
                sort_item_number.setChecked(false);
                break;
            case SORT_BY_NUMBER:
                sort_item_name.setChecked(false);
                sort_item_position.setChecked(false);
                sort_item_number.setChecked(true);
                break;
            case SORT_BY_POSITION:
                sort_item_name.setChecked(false);
                sort_item_position.setChecked(true);
                sort_item_number.setChecked(false);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        getMenuInflater().inflate(R.menu.menu_team_roster, mOptionsMenu);

        if (mPager.getCurrentItem() != 1) {
            return false;
        }

        mOptionsMenu.getItem(0).setIcon(colorizeIcon(R.drawable.ic_menu_sort_by_size, mColorAccent));
        handleMenuCheckboxes(mOptionsMenu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        mOptionsMenu.getItem(0).setIcon(colorizeIcon(R.drawable.ic_menu_sort_by_size, mColorAccent));

        handleMenuCheckboxes(mOptionsMenu);
        return super.onPrepareOptionsMenu(mOptionsMenu);
    }

    public void setSortIcon(MenuItem item) {
        Drawable arrowDrawable = getResources().getDrawable(R.drawable.ic_menu_sort_by_size);
        Drawable wrapped = DrawableCompat.wrap(arrowDrawable);

        if (arrowDrawable != null && wrapped != null) {
            arrowDrawable.mutate();
            DrawableCompat.setTint(wrapped, mColorAccent);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.isChecked() || item.getItemId() != R.id.sort) {
            Collections.reverse(mRoster);
        } else {
            switch (id) {
                case R.id.sort:
                    handleMenuCheckboxes(mOptionsMenu);
                    return super.onOptionsItemSelected(item);
                case R.id.roster_sort_by_name:
                    sortRosterByName(mRoster);
                    break;
                case R.id.roster_sort_by_number:
                    sortRosterByNumber(mRoster);
                    break;
                case R.id.roster_sort_by_position:
                    sortRosterByPosition(mRoster);
                    break;
                default:
                    sortRosterByName(mRoster);
            }
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

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getText().equals("SCHEDULE")) {
            mPager.setCurrentItem(0);
        }
        if (tab.getText().equals("ROSTER")) {
            mPager.setCurrentItem(1);
        }
        if (tab.getText().equals("INFO")) {
            mPager.setCurrentItem(2);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        View view = mPager.getFocusedChild();
        if (mPager.getCurrentItem() == 0 || mPager.getCurrentItem() == 1) {
            ((RecyclerView) view.findViewById(R.id.cardList)).scrollToPosition(0);
        }
        if (mPager.getCurrentItem() == 2) {
            view.findViewById(R.id.webScroll).scrollTo(0, 0);
        }
    }
}