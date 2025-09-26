package com.github.masdaster.edma.fragments;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.github.masdaster.edma.adapters.CommoditiesListAdapter;
import com.github.masdaster.edma.models.CommoditiesListResult;
import com.github.masdaster.edma.models.events.CommoditiesListSearch;
import com.github.masdaster.edma.models.events.ResultsList;
import com.github.masdaster.edma.network.CommoditiesNetwork;
import com.github.masdaster.edma.utils.NotificationsUtils;

public class CommoditiesListFragment extends AbstractFinderFragment<CommoditiesListAdapter> {

    public static final String COMMODITIES_LIST_FRAGMENT_TAG = "commodities_list_fragment";

    private CommoditiesListSearch lastSearch;

    @Override
    public void onSwipeToRefresh() {
        if (lastSearch != null) {
            onFindButtonEvent(lastSearch);
        } else {
            endLoading(true);
        }
    }

    @Override
    public CommoditiesListAdapter getNewRecyclerViewAdapter() {
        return new CommoditiesListAdapter(getContext());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommoditiesListResult(ResultsList<CommoditiesListResult> results) {
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
    public void onFindButtonEvent(CommoditiesListSearch event) {
        lastSearch = event;
        startLoading();
        CommoditiesNetwork.getCommoditiesPrices(requireContext(), event.getCommodityName());
    }
}