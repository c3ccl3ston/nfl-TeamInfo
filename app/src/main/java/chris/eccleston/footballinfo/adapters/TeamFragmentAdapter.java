package chris.eccleston.footballinfo.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
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

    private List<Fragment> fragments = new ArrayList<Fragment>();

    public TeamFragmentAdapter(FragmentManager fm, Context c, Team team, List<Player> roster) {
        super(fm);
        mContext = c;
        mTeam = team;
        mRoster = roster;
        fragments.add(FragmentTeamRoster.newInstance(c, team, roster));
        fragments.add(FragmentTeamSchedule.newInstance(team));
        fragments.add(FragmentTeamInfo.newInstance(c, team));
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
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