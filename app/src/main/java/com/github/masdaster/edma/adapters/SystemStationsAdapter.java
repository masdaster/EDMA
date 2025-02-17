package com.github.masdaster.edma.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;

import com.github.masdaster.edma.R;
import com.github.masdaster.edma.databinding.ListItemStationBinding;
import com.github.masdaster.edma.models.Station;
import com.github.masdaster.edma.utils.MathUtils;

public class SystemStationsAdapter extends androidx.recyclerview.widget.ListAdapter<Station, SystemStationsAdapter.stationViewHolder> {

    private final NumberFormat numberFormat;
    private final View.OnClickListener onClickListener;
    private final Context context;


    public SystemStationsAdapter(Context ctx, final RecyclerView recyclerView) {
        // Parent class setup
        super(new DiffUtil.ItemCallback<Station>() {
            @Override
            public boolean areItemsTheSame(@NonNull Station oldItem,
                                           @NonNull Station newItem) {
                return areContentsTheSame(oldItem, newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Station oldItem,
                                              @NonNull Station newItem) {
                return oldItem.getName().equals(newItem.getName()) &&
                        oldItem.getSystemName().equals(newItem.getSystemName());
            }
        });

        this.context = ctx;
        this.numberFormat = MathUtils.getNumberFormat(ctx);

        this.onClickListener = v -> {
            int position = recyclerView.getChildAdapterPosition(v);
            if (getItemCount() < position || getItemCount() == 0) {
                return;
            }
            final Station station = getItem(recyclerView.getChildAdapterPosition(v));
            // : TODO : go to station details
            /*Intent i = new Intent(context, DetailsActivity.class);
            i.putExtra("article", article);

            MiscUtils.startIntentWithFadeAnimation(context, i);*/
        };
    }

    @NonNull
    @Override
    public stationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemStationBinding itemBinding = ListItemStationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        itemBinding.getRoot().setOnClickListener(onClickListener);
        return new stationViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final stationViewHolder holder, final int position) {
        Station currentStation = getItem(holder.getAdapterPosition());

        holder.viewBinding.titleTextView.setText(currentStation.getName());
        holder.viewBinding.typeTextView.setText(currentStation.getType());
        holder.viewBinding.starDistanceTextView.setText(context.getString(R.string.distance_ls,
                numberFormat.format(currentStation.getDistanceToStar())));
        holder.viewBinding.landingPadTextView.setText(currentStation.getMaxLandingPad() == null ? context.getString(R.string.unknown) : currentStation.getMaxLandingPad());
        holder.viewBinding.logoImageView.setVisibility(currentStation.isPlanetary() ? View.VISIBLE : View.GONE);
    }

    public static class stationViewHolder extends RecyclerView.ViewHolder {
        private final ListItemStationBinding viewBinding;

        stationViewHolder(final ListItemStationBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
        }
    }
}
