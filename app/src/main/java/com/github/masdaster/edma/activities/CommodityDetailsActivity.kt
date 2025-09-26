package com.github.masdaster.edma.activities

import com.github.masdaster.edma.adapters.CommodityDetailsPagerAdapter
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.github.masdaster.edma.R
import com.github.masdaster.edma.models.events.CommodityBestPrices
import com.github.masdaster.edma.models.events.CommodityDetails
import com.github.masdaster.edma.models.events.CommodityDetailsBuy
import com.github.masdaster.edma.models.events.CommodityDetailsSell
import com.github.masdaster.edma.network.CommoditiesNetwork
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class CommodityDetailsActivity : AbstractViewPagerActivity() {

    override fun getDefaultData(): String {
        return "Cobalt"
    }

    override fun getAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter {
        return CommodityDetailsPagerAdapter(fragmentActivity)
    }

    override fun getTabLayoutMediator(
        context: Context,
        tabLayout: TabLayout,
        viewPager: ViewPager2
    ): TabLayoutMediator {
        return TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text =  when (position) {
                1 -> context.getString(R.string.where_to_sell)
                2 -> context.getString(R.string.where_to_buy)
                else ->  context.getString(R.string.commodity)
            }
        }
    }

    override fun getData() {
        CommoditiesNetwork.getCommodityDetails(this, dataName)
        CommoditiesNetwork.getCommoditiesBestPrices(this, dataName)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCommodityDetailsEvent(commodityDetailsEvent: CommodityDetails) {
        if (commodityDetailsEvent.success && commodityDetailsEvent.commodityDetails != null) {

            EventBus.getDefault().post(commodityDetailsEvent.commodityDetails)

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCommodityBestPricesEvent(commodityBestPricesEvent: CommodityBestPrices) {
        EventBus.getDefault().post(
            CommodityDetailsSell(
                true,
                commodityBestPricesEvent.bestStationsToSell
            )
        )
        EventBus.getDefault().post(
            CommodityDetailsBuy(
                true,
                commodityBestPricesEvent.bestStationsToBuy
            )
        )
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}
