package chris.eccleston.footballinfo.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import chris.eccleston.footballinfo.DepthPageTransformer;
import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.adapters.WeeklyScheduleFragmentAdapter;
import chris.eccleston.footballinfo.types.Game;

/**
 * Created by Chris on 8/20/2015.
 */
public class WeeklyScheduleActivity extends BaseActivity implements ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener {

    Context mContext;
    List<List<Game>> mAllGames;
    WeeklyScheduleFragmentAdapter mFragmentAdapter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabs)
    TabLayout mTabs;

    @BindView(R.id.my_pager)
    ViewPager mPager;
    int mTabPosition = -1;

    @BindColor(R.color.nfl_color_accent)
    int NFLColorAccent;
    @BindColor(R.color.nfl_primary_color)
    int NFLColorPrimary;
    @BindColor(R.color.nfl_primary_color_dark)
    int NFLColorDark;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_schedules);

        mContext = this;

        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.nfl_primary_color)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Schedule");

        mAllGames = new ArrayList<List<Game>>();
        for (int i = 1; i <= 17; i++) {
            List<Game> mWeeklyGames = Game.find(Game.class, "week_number = ?", String.valueOf(i));
            Log.d("DEBUG", "Finding games for week " + i);
            Log.d("DEBUG", "Found " + mWeeklyGames.size() + " games");
            mAllGames.add(mWeeklyGames);
        }

        mTabs.setBackgroundColor(NFLColorPrimary);

        mTabs.setTabTextColors(Color.WHITE, Color.WHITE);
        mTabs.setSelectedTabIndicatorColor(Color.WHITE);
        mTabs.addOnTabSelectedListener(this);
        setToolbarColors(mToolbar, NFLColorPrimary, NFLColorDark, NFLColorAccent);

        mPager.setPageTransformer(true, new DepthPageTransformer());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("TAB", mTabPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mTabPosition = savedInstanceState.getInt("TAB");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mFragmentAdapter = new WeeklyScheduleFragmentAdapter(this, getSupportFragmentManager(), mAllGames);
        mPager.setAdapter(mFragmentAdapter);
        mTabs.setupWithViewPager(mPager);
        mTabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        mPager.addOnPageChangeListener(this);

        if (mTabPosition != -1) {
            mPager.setCurrentItem(mTabPosition);
        }
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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_weekly_schedule, menu);
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mTabPosition = position;
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mPager.setCurrentItem(tab.getPosition());
        mTabPosition = tab.getPosition();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}