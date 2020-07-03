package ca.warp7.android.scouting.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager


/**
 * Adapter that returns the proper fragment as pages are navigated
 *
 * includes an extra page for the qr code
 */

class TabPagerAdapter(
        fragmentManager: FragmentManager,
        private val layoutsSize: Int,
        private val pager: ViewPager
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

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
