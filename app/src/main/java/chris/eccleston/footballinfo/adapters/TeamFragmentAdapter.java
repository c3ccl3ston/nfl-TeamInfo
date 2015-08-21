package chris.eccleston.footballinfo.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;
import java.util.Locale;

import chris.eccleston.footballinfo.R;
import chris.eccleston.footballinfo.fragments.FragmentTeamInfo;
import chris.eccleston.footballinfo.fragments.FragmentTeamRoster;
import chris.eccleston.footballinfo.fragments.FragmentTeamSchedule;
import chris.eccleston.footballinfo.types.Player;
import chris.eccleston.footballinfo.types.Team;

public class TeamFragmentAdapter extends FragmentPagerAdapter {

    protected Team mTeam;
    protected List<Player> mRoster;
    private Context mContext;

    public TeamFragmentAdapter(FragmentManager fm, Context c, Team team, List<Player> roster) {
        super(fm);
        mContext = c;
        mTeam = team;
        mRoster = roster;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FragmentTeamRoster.newInstance(mTeam, mRoster);
            case 1:
                return FragmentTeamSchedule.newInstance(mTeam);
            case 2:
                return FragmentTeamInfo.newInstance(mTeam);
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
                return mContext.getResources().getString(R.string.team_roster_fragment_title).toUpperCase(l);
            case 1:
                return mContext.getResources().getString(R.string.team_schedule_fragment_title).toUpperCase(l);
            case 2:
                return mContext.getResources().getString(R.string.team_info_fragment_title).toUpperCase(l);
        }
        return null;
    }
}