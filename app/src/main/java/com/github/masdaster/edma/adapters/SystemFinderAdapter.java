package com.github.masdaster.edma.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import com.github.masdaster.edma.R;
import com.github.masdaster.edma.activities.SystemDetailsActivity;
import com.github.masdaster.edma.databinding.FragmentFindSystemHeaderBinding;
import com.github.masdaster.edma.databinding.ListItemSystemFinderResultBinding;
import com.github.masdaster.edma.models.SystemFinderResult;
import com.github.masdaster.edma.models.events.SystemFinderSearch;
import com.github.masdaster.edma.utils.MiscUtils;
import com.github.masdaster.edma.utils.ViewUtils;

public class SystemFinderAdapter extends FinderAdapter<SystemFinderAdapter.HeaderViewHolder,
        SystemFinderAdapter.ResultViewHolder, SystemFinderResult> {

    public SystemFinderAdapter(Context context) {
        super(context);
    }

    @Override
    protected RecyclerView.ViewHolder getHeaderViewHolder(@NonNull @NotNull ViewGroup parent) {
        FragmentFindSystemHeaderBinding headerBinding = FragmentFindSystemHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HeaderViewHolder(headerBinding);
    }

    @Override
    protected RecyclerView.ViewHolder getResultViewHolder(@NonNull @NotNull ViewGroup parent) {
        ListItemSystemFinderResultBinding resultBinding = ListItemSystemFinderResultBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ResultViewHolder(resultBinding);
    }

    @Override
    protected void bindHeaderViewHolder(final HeaderViewHolder holder) {
        // Find button
        final Runnable onSubmit = () -> {
            // Don't launch search on empty strings or if find already in progress
            if (!findButtonEnabled || holder.binding.systemInputView.getText().length() == 0) {
                return;
            }

            ViewUtils.hideSoftKeyboard(holder.binding.findButton.getRootView());

            SystemFinderSearch result = new SystemFinderSearch(
                    holder.binding.systemInputView.getText().toString());
            EventBus.getDefault().post(result);
        };
        holder.binding.findButton.setOnClickListener(view -> onSubmit.run());
        holder.binding.systemInputView.setOnSubmit(onSubmit);
    }

    @Override
    protected void bindResultViewHolder(ResultViewHolder holder, int position) {
        final SystemFinderResult currentResult = results.get(position - 1);

        holder.binding.titleTextView.setText(currentResult.getName());
        holder.binding.permitRequiredTextView.setVisibility(
                currentResult.isPermitRequired() ? View.VISIBLE : View.GONE);

        holder.binding.allegianceTextView.setText(getContentOrUnknown(currentResult.getAllegiance()));
        holder.binding.securityTextView.setText(getContentOrUnknown(currentResult.getSecurity()));
        holder.binding.governmentTextView.setText(getContentOrUnknown(currentResult.getGovernment()));
        holder.binding.economyTextView.setText(getContentOrUnknown(currentResult.getEconomy()));

        holder.binding.itemLayout.setOnClickListener(v -> {
            Intent i = new Intent(context, SystemDetailsActivity.class);
            i.putExtra("data", currentResult.getName());
            MiscUtils.startIntentWithFadeAnimation(context, i);
        });
    }

    private String getContentOrUnknown(String content) {
        return content == null || content.length() == 0 ?
                context.getString(R.string.unknown) : content;
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        private final ListItemSystemFinderResultBinding binding;

        public ResultViewHolder(ListItemSystemFinderResultBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final FragmentFindSystemHeaderBinding binding;

        public HeaderViewHolder(FragmentFindSystemHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
