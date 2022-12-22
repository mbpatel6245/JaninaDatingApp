package com.jda.application.fragments.subscriptions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val mCOUNT = 2

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = FreeSubscriptionFragment.newInstance()
            1 -> fragment = PlusSubscriptionFragment.newInstance()
        }

        return fragment!!/**/
    }

    override fun getCount(): Int {
        return mCOUNT
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "Tab " + (position + 1)
    }
}