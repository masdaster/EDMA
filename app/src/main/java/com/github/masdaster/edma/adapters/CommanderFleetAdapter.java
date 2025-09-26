package com.github.masdaster.edma.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;

import com.github.masdaster.edma.R;
import com.github.masdaster.edma.databinding.ListItemFleetShipBinding;
import com.github.masdaster.edma.models.ShipInformation;
import com.github.masdaster.edma.utils.MathUtils;
import com.github.masdaster.edma.utils.MiscUtils;

public class CommanderFleetAdapter extends ListAdapter<ShipInformation,
        CommanderFleetAdapter.shipViewHolder> {

    private final NumberFormat numberFormat;
    private final Context context;


    public CommanderFleetAdapter(Context ctx) {
        // Parent class setup
        super(new DiffUtil.ItemCallback<>() {
            @Override
            public boolean areItemsTheSame(@NonNull ShipInformation oldItem,
                                           @NonNull ShipInformation newItem) {
                return areContentsTheSame(oldItem, newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull ShipInformation oldItem,
                                              @NonNull ShipInformation newItem) {
                return oldItem.getId() == newItem.getId();
            }
        });

        this.context = ctx;
        this.numberFormat = MathUtils.getNumberFormat(ctx);
    }

    @NonNull
    @Override
    public shipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemFleetShipBinding itemBinding = ListItemFleetShipBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new shipViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final shipViewHolder holder, final int position) {
        ShipInformation currentShipInformation = getItem(holder.getAdapterPosition());

        // Name / model ...
        holder.viewBinding.titleTextView.setText(currentShipInformation.getModel());
        if (currentShipInformation.getName() != null && !currentShipInformation.getName().equals(currentShipInformation.getModel())) {
            holder.viewBinding.subtitleTextView.setVisibility(View.VISIBLE);
            holder.viewBinding.subtitleTextView.setText(currentShipInformation.getName());
        } else {
            holder.viewBinding.subtitleTextView.setVisibility(View.GONE);
        }

        // Current ship
        holder.viewBinding.currentShipLabelTextView.setVisibility(currentShipInformation.isCurrentShip() ?
                View.VISIBLE : View.GONE);

        // System and station
        holder.viewBinding.systemTextView.setText(currentShipInformation.getSystemName());
        holder.viewBinding.stationTextView.setText(currentShipInformation.getStationName());

        // Ship value
        String shipValue = numberFormat.format(currentShipInformation.getHullValue() +
                currentShipInformation.getModulesValue());
        holder.viewBinding.shipValueTextView.setText(context.getString(R.string.credits, shipValue));

        // Cargo value
        if (currentShipInformation.getCargoValue() != 0) {
            holder.viewBinding.cargoValueTextView.setVisibility(View.VISIBLE);
            holder.viewBinding.cargoValueLabelTextView.setVisibility(View.VISIBLE);

            String cargoValue = numberFormat.format(currentShipInformation.getCargoValue());
            holder.viewBinding.cargoValueTextView.setText(context.getString(R.string.credits, cargoValue));
        } else {
            holder.viewBinding.cargoValueTextView.setVisibility(View.GONE);
            holder.viewBinding.cargoValueLabelTextView.setVisibility(View.GONE);
        }

        // Ship picture
        MiscUtils.loadShipImage(holder.viewBinding.shipImageView, currentShipInformation);
    }

    public static class shipViewHolder extends RecyclerView.ViewHolder {
        private final ListItemFleetShipBinding viewBinding;

        shipViewHolder(final ListItemFleetShipBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
        }
    }
}
