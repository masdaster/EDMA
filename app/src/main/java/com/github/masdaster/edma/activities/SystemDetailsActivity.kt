package com.github.masdaster.edma.activities

import com.github.masdaster.edma.adapters.SystemDetailsPagerAdapter
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.github.masdaster.edma.R
import com.github.masdaster.edma.network.SystemNetwork


class SystemDetailsActivity : AbstractViewPagerActivity() {

    override fun getDefaultData(): String {
        return "Sol"
    }

    override fun getAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter {
        return SystemDetailsPagerAdapter(fragmentActivity);
    }

    override fun getTabLayoutMediator(
        context: Context,
        tabLayout: TabLayout,
        viewPager: ViewPager2
    ): TabLayoutMediator {
        return TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                1 -> context.getString(R.string.stations)
                2 -> context.getString(R.string.factions)
                else -> context.getString(R.string.system)
            }
        }
    }

    override fun getData() {
        SystemNetwork.getSystemDetails(this, dataName)
        SystemNetwork.getSystemHistory(this, dataName)
        SystemNetwork.getSystemStations(this, dataName)
    }
}
