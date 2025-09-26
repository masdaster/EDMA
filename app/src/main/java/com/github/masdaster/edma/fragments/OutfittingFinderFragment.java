package com.github.masdaster.edma.fragments;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.github.masdaster.edma.adapters.OutfittingFinderAdapter;
import com.github.masdaster.edma.models.OutfittingFinderResult;
import com.github.masdaster.edma.models.events.OutfittingFinderSearch;
import com.github.masdaster.edma.models.events.ResultsList;
import com.github.masdaster.edma.network.OutfittingFinderNetwork;
import com.github.masdaster.edma.utils.NotificationsUtils;

public class OutfittingFinderFragment extends AbstractFinderFragment<OutfittingFinderAdapter> {

    public static final String OUTFITTING_FINDER_FRAGMENT_TAG = "outfitting_finder_fragment";

    private OutfittingFinderSearch lastSearch;

    @Override
    public void onSwipeToRefresh() {
        if (lastSearch != null) {
            onFindButtonEvent(lastSearch);
        } else {
            endLoading(true);
        }
    }

    @Override
    public OutfittingFinderAdapter getNewRecyclerViewAdapter() {
        return new OutfittingFinderAdapter(getContext());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOutfittingFinderResult(ResultsList<OutfittingFinderResult> results) {
        // Error
        if (!results.getSuccess()) {
            endLoading(true);
            NotificationsUtils.displayGenericDownloadErrorSnackbar(getActivity());
            return;
        }

        endLoading(results.getResults().isEmpty());
        recyclerViewAdapter.setResults(results.getResults());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFindButtonEvent(OutfittingFinderSearch event) {
        startLoading();

        lastSearch = event;
        OutfittingFinderNetwork.findOutfitting(requireContext(), event.getSystemName(),
                event.getOutfittingName(), event.getLandingPadSize());
    }
}