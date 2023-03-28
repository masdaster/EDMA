package com.github.masdaster.edma.fragments

import com.github.masdaster.edma.adapters.CommodityDetailsStationsAdapter
import com.github.masdaster.edma.models.events.CommodityDetailsSell
import com.github.masdaster.edma.utils.NotificationsUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CommodityDetailsSellFragment : AbstractListFragment<CommodityDetailsStationsAdapter>() {

    init {
        loadDataOnCreate = false
    }

    override fun getNewRecyclerViewAdapter(): CommodityDetailsStationsAdapter {
        return CommodityDetailsStationsAdapter(context, true)
    }

    override fun getData() {
        // none : data is sent by parent activity
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSellStationsEvents(stations: CommodityDetailsSell) {
        // Error case
        if (!stations.success) {
            endLoading(true)
            NotificationsUtils.displayGenericDownloadErrorSnackbar(activity)
            return
        }

        endLoading(stations.stations.isEmpty())
        recyclerViewAdapter.submitList(stations.stations)
    }

    companion object {

        const val COMMODITY_DETAILS_SELL_FRAGMENT_TAG = "commodity_details_sell"
    }
}
