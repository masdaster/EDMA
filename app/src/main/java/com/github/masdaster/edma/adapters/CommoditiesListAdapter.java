package com.github.masdaster.edma.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;

import com.github.masdaster.edma.R;
import com.github.masdaster.edma.activities.CommodityDetailsActivity;
import com.github.masdaster.edma.databinding.FragmentListCommoditiesHeaderBinding;
import com.github.masdaster.edma.databinding.ListItemCommoditiesListResultBinding;
import com.github.masdaster.edma.models.CommoditiesListResult;
import com.github.masdaster.edma.models.events.CommoditiesListSearch;
import com.github.masdaster.edma.utils.MathUtils;
import com.github.masdaster.edma.utils.MiscUtils;
import com.github.masdaster.edma.utils.ViewUtils;

public class CommoditiesListAdapter extends FinderAdapter<CommoditiesListAdapter.HeaderViewHolder,
        CommoditiesListAdapter.ResultViewHolder, CommoditiesListResult> {

    private final NumberFormat numberFormat;

    public CommoditiesListAdapter(Context context) {
        super(context);

        // Number format
        numberFormat = MathUtils.getNumberFormat(context);
    }

    @Override
    protected RecyclerView.ViewHolder getHeaderViewHolder(@NonNull @NotNull ViewGroup parent) {
        FragmentListCommoditiesHeaderBinding headerBinding = FragmentListCommoditiesHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HeaderViewHolder(headerBinding);
    }

    @Override
    protected RecyclerView.ViewHolder getResultViewHolder(@NonNull @NotNull ViewGroup parent) {
        ListItemCommoditiesListResultBinding resultBinding = ListItemCommoditiesListResultBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ResultViewHolder(resultBinding);
    }

    @Override
    protected void bindHeaderViewHolder(final HeaderViewHolder holder) {
        // Commodity autocomplete
        holder.binding.commodityInputEditText.setThreshold(3);
        holder.binding.commodityInputEditText.setAdapter(new AutoCompleteAdapter(context,
                AutoCompleteAdapter.TYPE_AUTOCOMPLETE_COMMODITIES));
        holder.binding.commodityInputEditText.setOnItemClickListener((adapterView, view, position, id) ->
                holder.binding.commodityInputEditText.setText((String) adapterView.getItemAtPosition(position)));

        // Find button
        final Runnable onSubmit = () -> {
            // Don't launch search on empty strings or if find already in progress
            if (!findButtonEnabled || holder.binding.commodityInputEditText.getText() != null &&
                    holder.binding.commodityInputEditText.getText().length() == 0) {
                return;
            }

            ViewUtils.hideSoftKeyboard(holder.binding.findButton.getRootView());
            CommoditiesListSearch result = new CommoditiesListSearch(
                    holder.binding.commodityInputEditText.getText().toString());
            EventBus.getDefault().post(result);
        };
        holder.binding.findButton.setOnClickListener(view -> onSubmit.run());
        holder.binding.commodityInputEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                onSubmit.run();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void bindResultViewHolder(ResultViewHolder holder, int position) {
        final CommoditiesListResult currentResult = results.get(position - 1);

        holder.binding.titleTextView.setText(currentResult.getName());
        holder.binding.averageBuyPriceTextView.setText(context.getString(R.string.credits,
                numberFormat.format(currentResult.getAverageBuyPrice())));
        holder.binding.averageSellPriceTextView.setText(context.getString(R.string.credits,
                numberFormat.format(currentResult.getAverageSellPrice())));
        holder.binding.isRareTextView.setVisibility(currentResult.isRare() ? View.VISIBLE : View.GONE);
        holder.binding.categoryTextView.setText(currentResult.getCategory());

        holder.binding.itemLayout.setOnClickListener(v -> {
            Intent i = new Intent(context, CommodityDetailsActivity.class);
            i.putExtra("data", currentResult.getName());
            MiscUtils.startIntentWithFadeAnimation(context, i);
        });
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        private final ListItemCommoditiesListResultBinding binding;

        public ResultViewHolder(ListItemCommoditiesListResultBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final FragmentListCommoditiesHeaderBinding binding;

        public HeaderViewHolder(FragmentListCommoditiesHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
