package com.github.masdaster.edma.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.NumberFormat;

import com.github.masdaster.edma.R;
import com.github.masdaster.edma.activities.SystemDetailsActivity;
import com.github.masdaster.edma.databinding.FragmentCommodityDetailsBinding;
import com.github.masdaster.edma.models.CommodityDetailsResult;
import com.github.masdaster.edma.utils.MathUtils;

public class CommodityDetailsFragment extends Fragment {

    public static final String COMMODITY_DETAILS_FRAGMENT = "commodity_details_fragment";

    private FragmentCommodityDetailsBinding binding;
    private NumberFormat numberFormat;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCommodityDetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //Swipe to refresh setup
        SwipeRefreshLayout.OnRefreshListener listener = () -> {
            binding.swipeContainer.setRefreshing(true);
            if (getActivity() != null) {
                ((SystemDetailsActivity) getActivity()).getData();
            }
        };
        binding.swipeContainer.setOnRefreshListener(listener);

        // Init number format
        numberFormat = MathUtils.getNumberFormat(getContext());

        // Setup views
        binding.swipeContainer.setVisibility(View.VISIBLE);
        binding.swipeContainer.setRefreshing(true);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Register event and get the news
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    // Get results posted from parent activity
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommodityDetailsEvent(CommodityDetailsResult commodityDetailsResult) {
        endLoading();

        bindInformations(commodityDetailsResult);
    }

    private void bindInformations(CommodityDetailsResult details) {
        binding.commodityNameTextView.setText(details.getName());
        binding.isRareTextView.setVisibility(details.isRare() ? View.VISIBLE : View.GONE);
        binding.categoryTextView.setText(details.getCategory());

        binding.averageBuyTextView.setText(getString(R.string.credits,
                numberFormat.format(details.getAverageBuyPrice())));
        binding.averageSellTextView.setText(getString(R.string.credits,
                numberFormat.format(details.getAverageSellPrice())));

        binding.minBuyTextView.setText(getString(R.string.credits,
                numberFormat.format(details.getMinimumBuyPrice())));
        binding.maxSellTextView.setText(getString(R.string.credits,
                numberFormat.format(details.getMaximumSellPrice())));
    }

    public void endLoading() {
        binding.swipeContainer.setRefreshing(false);
    }
}
