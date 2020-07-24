package com.github.vkay94.doubletapplayerviewexample.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.github.vkay94.doubletapplayerviewexample.R

private val TAB_TITLES = arrayOf(
    R.string.tab_circle,
    R.string.tab_seconds_view
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    private val fragmentList: ArrayList<Fragment> = arrayListOf()

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }

    override fun getItem(position: Int): Fragment = fragmentList[position]

    override fun getPageTitle(position: Int): CharSequence? =
        context.resources.getString(TAB_TITLES[position])

    override fun getCount(): Int = fragmentList.size
}