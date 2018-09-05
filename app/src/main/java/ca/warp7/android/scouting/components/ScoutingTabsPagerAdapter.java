package ca.warp7.android.scouting.components;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import ca.warp7.android.scouting.abstraction.ScoutingTab;

/**
 * Adapter that returns the proper fragment as pages are navigated
 */

public class ScoutingTabsPagerAdapter
        extends FragmentPagerAdapter {

    private int mLayoutsSize;
    private ViewPager mPager;

    public ScoutingTabsPagerAdapter(FragmentManager fm, int size, ViewPager pager) {
        super(fm);
        mLayoutsSize = size;
        mPager = pager;
    }

    @Override
    public Fragment getItem(int position) {
        if (position < mLayoutsSize) {
            return ScoutingInputsFragment.createInstance(position);
        } else {
            return QRFragment.createInstance();
        }
    }

    @Override
    public int getCount() {
        return mLayoutsSize + 1;
    }

    public ScoutingTab getTabAt(int index) {
        return (ScoutingTab) instantiateItem(mPager, index);
    }
}
