package com.github.masdaster.edma.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.github.masdaster.edma.fragments.CommodityDetailsBuyFragment
import com.github.masdaster.edma.fragments.CommodityDetailsFragment
import com.github.masdaster.edma.fragments.CommodityDetailsSellFragment


class CommodityDetailsPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> CommodityDetailsSellFragment()
            2 -> CommodityDetailsBuyFragment()
            else -> CommodityDetailsFragment()
        }
    }
}
