package com.github.masdaster.edma.fragments;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.github.masdaster.edma.adapters.CommodityFinderAdapter;
import com.github.masdaster.edma.models.CommodityFinderResult;
import com.github.masdaster.edma.models.events.CommodityFinderSearch;
import com.github.masdaster.edma.models.events.ResultsList;
import com.github.masdaster.edma.network.CommodityFinderNetwork;
import com.github.masdaster.edma.utils.NotificationsUtils;

public class CommodityFinderFragment extends AbstractFinderFragment<CommodityFinderAdapter> {

    public static final String COMMODITY_FINDER_FRAGMENT_TAG = "commodity_finder_fragment";

    private CommodityFinderSearch lastSearch;

    @Override
    public void onSwipeToRefresh() {
        if (lastSearch != null) {
            onFindButtonEvent(lastSearch);
        } else {
            endLoading(true);
        }
    }

    @Override
    public CommodityFinderAdapter getNewRecyclerViewAdapter() {
        return new CommodityFinderAdapter(getContext());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShipFinderResultEvent(ResultsList<CommodityFinderResult> results) {
        // Error
        if (!results.getSuccess()) {
            endLoading(true);
            NotificationsUtils.displayGenericDownloadErrorSnackbar(getActivity());
            return;
        }

        endLoading(results.getResults().size() == 0);
        recyclerViewAdapter.setResults(results.getResults());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFindButtonEvent(CommodityFinderSearch event) {
        startLoading();

        lastSearch = event;
        CommodityFinderNetwork.findCommodity(getContext(), event.getSystemName(),
                event.getCommodityName(), event.getLandingPadSize(), event.getStockOrDemand(),
                event.isSellingMode());
    }
}