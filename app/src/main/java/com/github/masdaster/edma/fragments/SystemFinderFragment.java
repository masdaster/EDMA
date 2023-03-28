package com.github.masdaster.edma.fragments;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.github.masdaster.edma.adapters.SystemFinderAdapter;
import com.github.masdaster.edma.models.SystemFinderResult;
import com.github.masdaster.edma.models.events.ResultsList;
import com.github.masdaster.edma.models.events.SystemFinderSearch;
import com.github.masdaster.edma.network.SystemNetwork;
import com.github.masdaster.edma.utils.NotificationsUtils;

public class SystemFinderFragment extends AbstractFinderFragment<SystemFinderAdapter> {

    public static final String SYSTEM_FINDER_FRAGMENT_TAG = "system_finder_fragment";

    private SystemFinderSearch lastSearch;

    @Override
    public SystemFinderAdapter getNewRecyclerViewAdapter() {
        return new SystemFinderAdapter(getContext());
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
    public void onShipFinderResultEvent(ResultsList<SystemFinderResult> results) {
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
    public void onFindButtonEvent(SystemFinderSearch event) {
        lastSearch = event;
        startLoading();
        SystemNetwork.findSystem(getContext(), event.getSystemName());
    }
}
