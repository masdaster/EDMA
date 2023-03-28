package com.github.masdaster.edma.fragments;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.github.masdaster.edma.adapters.ShipFinderAdapter;
import com.github.masdaster.edma.models.ShipFinderResult;
import com.github.masdaster.edma.models.events.ResultsList;
import com.github.masdaster.edma.models.events.ShipFinderSearch;
import com.github.masdaster.edma.network.ShipFinderNetwork;
import com.github.masdaster.edma.utils.NotificationsUtils;

public class ShipFinderFragment extends AbstractFinderFragment<ShipFinderAdapter> {

    public static final String SHIP_FINDER_FRAGMENT_TAG = "ship_finder_fragment";

    private ShipFinderSearch lastSearch;

    @Override
    public ShipFinderAdapter getNewRecyclerViewAdapter() {
        return new ShipFinderAdapter(getContext());
    }

    @Override
    public void onSwipeToRefresh() {
        if (lastSearch != null) {
            onFindButtonEvent(lastSearch);
        } else {
            endLoading(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShipFinderResultEvent(ResultsList<ShipFinderResult> results) {
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
    public void onFindButtonEvent(ShipFinderSearch event) {
        lastSearch = event;
        startLoading();
        ShipFinderNetwork.findShip(getContext(), event.getSystemName(), event.getShipName());
    }
}
