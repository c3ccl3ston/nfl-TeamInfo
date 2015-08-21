package chris.eccleston.footballinfo.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
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

    @InjectView(R.id.tabs)
    TabLayout mTabs;
    @InjectView(R.id.my_pager)
    ViewPager mPager;

    int mTabPosition = -1;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_schedules);

        mContext = this;

        ButterKnife.inject(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.nfl_primary_color)));
        getSupportActionBar().setHomeAsUpIndicator(colorizeIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha, getResources().getColor(R.color.nfl_color_accent)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            mTabPosition = savedInstanceState.getInt("TAB");
        }

        setTitle("Schedule");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setTintColor(darken(getResources().getColor(R.color.nfl_primary_color), 0.25));
            getWindow().setStatusBarColor(darken(getResources().getColor(R.color.nfl_primary_color), 0.25));
        }

        mTabs.setTabTextColors(Color.WHITE, Color.WHITE);
        mTabs.setOnTabSelectedListener(this);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.nfl_color_accent));
        mToolbar.setSubtitleTextColor(getResources().getColor(R.color.nfl_color_accent));

        mPager.setPageTransformer(true, new DepthPageTransformer());
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("TAB", mTabs.getSelectedTabPosition());
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAllGames = new ArrayList<>();
        for (int i = 1; i <= 17; i++) {
            List<Game> mWeeklyGames = Game.find(Game.class, "week_number = ?", String.valueOf(i));
            mAllGames.add(mWeeklyGames);
        }
        // Initialize the ViewPager and set an adapter
        mFragmentAdapter = new WeeklyScheduleFragmentAdapter(getSupportFragmentManager(), mAllGames);
        mPager.setAdapter(mFragmentAdapter);

        mTabs.setupWithViewPager(mPager);
        mTabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        mPager.addOnPageChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        WeeklyScheduleActivity prevActivity = (WeeklyScheduleActivity) getLastCustomNonConfigurationInstance();
        if (prevActivity != null) {
            mPager.setCurrentItem(prevActivity.mTabPosition);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_weekly_schedule, menu);
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

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
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}