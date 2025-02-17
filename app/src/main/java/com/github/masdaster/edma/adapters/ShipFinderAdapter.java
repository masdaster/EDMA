package com.github.masdaster.edma.adapters;

import static android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.threeten.bp.Instant;

import java.text.NumberFormat;

import com.github.masdaster.edma.R;
import com.github.masdaster.edma.databinding.FragmentFindShipHeaderBinding;
import com.github.masdaster.edma.databinding.ListItemShipFinderResultBinding;
import com.github.masdaster.edma.models.ShipFinderResult;
import com.github.masdaster.edma.models.events.ShipFinderSearch;
import com.github.masdaster.edma.utils.MathUtils;
import com.github.masdaster.edma.utils.ViewUtils;

public class ShipFinderAdapter extends FinderAdapter<ShipFinderAdapter.HeaderViewHolder,
        ShipFinderAdapter.ResultViewHolder, ShipFinderResult> {

    protected final NumberFormat numberFormat;

    public ShipFinderAdapter(Context context) {
        super(context);
        numberFormat = MathUtils.getNumberFormat(context);
    }

    @Override
    protected RecyclerView.ViewHolder getHeaderViewHolder(@NonNull @NotNull ViewGroup parent) {
        FragmentFindShipHeaderBinding headerBinding = FragmentFindShipHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HeaderViewHolder(headerBinding);
    }

    @Override
    protected RecyclerView.ViewHolder getResultViewHolder(@NonNull @NotNull ViewGroup parent) {
        ListItemShipFinderResultBinding resultBinding = ListItemShipFinderResultBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ResultViewHolder(resultBinding);
    }


    @Override
    protected void bindHeaderViewHolder(final HeaderViewHolder holder) {
        // Ship autocomplete
        holder.binding.shipInputEditText.setThreshold(3);
        holder.binding.shipInputEditText.setAdapter(new AutoCompleteAdapter(context,
                AutoCompleteAdapter.TYPE_AUTOCOMPLETE_SHIPS));
        holder.binding.shipInputEditText.setOnItemClickListener((adapterView, view, position, id) ->
                holder.binding.shipInputEditText.setText((String) adapterView.getItemAtPosition(position)));

        // Find button
        final Runnable onSubmit = () -> {
            if (!findButtonEnabled) {
                return;
            }

            ViewUtils.hideSoftKeyboard(holder.binding.findButton.getRootView());

            ShipFinderSearch result = new ShipFinderSearch(
                    holder.binding.shipInputEditText.getText().toString(),
                    holder.binding.systemInputView.getText().toString());
            EventBus.getDefault().post(result);
        };

        // On submit stuff
        holder.binding.findButton.setOnClickListener(view -> onSubmit.run());
        holder.binding.shipInputEditText.setOnSubmit(onSubmit);
        holder.binding.systemInputView.setOnSubmit(onSubmit);
    }

    @Override
    protected void bindResultViewHolder(ResultViewHolder holder, int position) {
        ShipFinderResult currentResult = results.get(position - 1);

        // Title
        holder.binding.titleTextView.setText(
                String.format("%s - %s", currentResult.getSystemName(),
                        currentResult.getStationName())
        );

        // Display is special type (fleet carrier, settlement etc...)
        boolean displayStationTypeText = false;
        String stationType = null;
        if (currentResult.isPlanetary()) {
            displayStationTypeText = true;
            stationType = context.getString(R.string.planetary);
        }
        if (currentResult.isFleetCarrier()) {
            displayStationTypeText = true;
            stationType = context.getString(R.string.fleet_carrier);
        }
        if (currentResult.isSettlement()) {
            displayStationTypeText = true;
            stationType = context.getString(R.string.settlement);
        }
        holder.binding.stationTypeTextView.setText(stationType);
        holder.binding.stationTypeTextView.setVisibility(displayStationTypeText ? View.VISIBLE : View.GONE);

        // Other informations
        holder.binding.distanceTextView.setText(context.getString(R.string.distance_ly,
                currentResult.getDistance()));
        holder.binding.starDistanceTextView.setText(context.getString(R.string.distance_ls,
                numberFormat.format(currentResult.getDistanceToStar())));
        holder.binding.isPlanetaryImageView.setVisibility(
                currentResult.isPlanetary() ? View.VISIBLE : View.GONE);
        holder.binding.landingPadTextView.setText(currentResult.getMaxLandingPad());

        // Update date
        String date = android.text.format.DateUtils.getRelativeTimeSpanString(
                currentResult.getLastShipyardUpdate().toEpochMilli(), Instant.now().toEpochMilli(),
                0, FORMAT_ABBREV_RELATIVE).toString();
        holder.binding.lastUpdateTextView.setText(date);
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder {

        private final ListItemShipFinderResultBinding binding;

        public ResultViewHolder(ListItemShipFinderResultBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final FragmentFindShipHeaderBinding binding;

        public HeaderViewHolder(FragmentFindShipHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
