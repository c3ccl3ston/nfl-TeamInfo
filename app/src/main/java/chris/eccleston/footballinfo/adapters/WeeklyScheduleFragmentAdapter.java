package chris.eccleston.footballinfo.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import chris.eccleston.footballinfo.fragments.FragmentWeeklySchedule;
import chris.eccleston.footballinfo.types.Game;

/**
 * Created by Chris on 8/20/2015.
 */
public class WeeklyScheduleFragmentAdapter extends FragmentPagerAdapter {

    protected List<List<Game>> mWeeklySchedules;
    protected Context mContext;

    public WeeklyScheduleFragmentAdapter(Context context, FragmentManager fm, List<List<Game>> weeklySchedules) {
        super(fm);
        mWeeklySchedules = weeklySchedules;
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        System.out.println("Position: " + position);
        switch (position) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                FragmentWeeklySchedule mFWS = new FragmentWeeklySchedule();
                return mFWS.newInstance(mContext, mWeeklySchedules.get(position));
            default:
                return null;
        }
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
