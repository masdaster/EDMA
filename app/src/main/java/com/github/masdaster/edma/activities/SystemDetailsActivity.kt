package com.github.masdaster.edma.activities

import androidx.fragment.app.FragmentPagerAdapter

import com.github.masdaster.edma.adapters.SystemDetailsPagerAdapter
import com.github.masdaster.edma.network.SystemNetwork

class SystemDetailsActivity : AbstractViewPagerActivity() {

    override fun getDefaultData(): String {
        return "Sol"
    }

    override fun getPagerAdapter(): FragmentPagerAdapter {
        return SystemDetailsPagerAdapter(supportFragmentManager, this)
    }

    override fun getData() {
        SystemNetwork.getSystemDetails(this, dataName)
        SystemNetwork.getSystemHistory(this, dataName)
        SystemNetwork.getSystemStations(this, dataName)
    }
}
