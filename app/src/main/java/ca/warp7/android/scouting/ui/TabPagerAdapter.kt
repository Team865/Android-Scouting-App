package ca.warp7.android.scouting.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager

/**
 * Adapter that returns the proper fragment as pages are navigated
 *
 * includes an extra page for the qr code
 */

class TabPagerAdapter(
    fragmentManager: FragmentManager,
    private val layoutsSize: Int,
    private val pager: ViewPager
) :
    FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return if (position < layoutsSize) {
            EntryScreenFragment.createInstance(position)
        } else {
            QRCodeFragment()
        }
    }

    override fun getCount(): Int {
        return layoutsSize + 1
    }

    operator fun get(index: Int): ScoutingEntryTab {
        return instantiateItem(pager, index) as ScoutingEntryTab
    }
}
