package chris.eccleston.footballinfo.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import chris.eccleston.footballinfo.fragments.FragmentWeeklySchedule;
import chris.eccleston.footballinfo.types.Game;

/**
 * Created by Chris on 8/20/2015.
 */
public class WeeklyScheduleFragmentAdapter extends FragmentPagerAdapter {

    protected List<List<Game>> mWeeklySchedules;
    protected Context mContext;

    protected List<Fragment> fragments = new ArrayList<Fragment>();

    public WeeklyScheduleFragmentAdapter(Context context, FragmentManager fm, List<List<Game>> weeklySchedules) {
        super(fm);
        mWeeklySchedules = weeklySchedules;
        mContext = context;
        for (int i = 1; i <= 17; i++) {
            fragments.add(FragmentWeeklySchedule.newInstance(mContext, i));
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return mWeeklySchedules.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "WEEK " + (position + 1);
    }
}