package chris.eccleston.footballinfo.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.fragments.FragmentTeamInfo;
import chris.eccleston.footballinfo.fragments.FragmentTeamNews;
import chris.eccleston.footballinfo.fragments.FragmentTeamRoster;
import chris.eccleston.footballinfo.fragments.FragmentTeamSchedule;
import chris.eccleston.footballinfo.types.Team;

public class MyFragmentAdapter extends FragmentPagerAdapter {

    private Context mContext;
    protected Team mTeam;

    public MyFragmentAdapter(FragmentManager fm, Context c, Team team) {
        super(fm);
        mContext = c;
        mTeam = team;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentTeamSchedule mFragmentTeamSchedule = new FragmentTeamSchedule();
                return mFragmentTeamSchedule.newInstance(mTeam);
            case 1:
                FragmentTeamRoster mFragmentTeamRoster = new FragmentTeamRoster();
                return mFragmentTeamRoster.newInstance(mTeam);
            case 2:
                FragmentTeamInfo mFragmentTeamInfo = new FragmentTeamInfo();
                return mFragmentTeamInfo.newInstance(mTeam);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getResources().getString(R.string.team_schedule_fragment_title).toUpperCase(l);
            case 1:
                return mContext.getResources().getString(R.string.team_roster_fragment_title).toUpperCase(l);
            case 2:
                return mContext.getResources().getString(R.string.team_info_fragment_title).toUpperCase(l);
        }
        return null;
    }
}

