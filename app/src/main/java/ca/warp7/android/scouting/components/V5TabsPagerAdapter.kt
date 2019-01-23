package ca.warp7.android.scouting.components

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import ca.warp7.android.scouting.abstraction.ScoutingTab

/**
 * Adapter that returns the proper fragment as pages are navigated
 *
 * includes an extra page for the qr code
 */

class V5TabsPagerAdapter(
    fragmentManager: FragmentManager,
    private val layoutsSize: Int,
    private val pager: ViewPager) :
    FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return if (position < layoutsSize) {
            ScoutingInputsFragment.createInstance(position)
        } else {
            QRFragment.createInstance()
        }
    }

    override fun getCount(): Int {
        return layoutsSize + 1
    }

    fun getTabAt(index: Int): ScoutingTab {
        return instantiateItem(pager, index) as ScoutingTab
    }
}
