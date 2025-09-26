package com.github.masdaster.edma.adapters;

import android.content.Context;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.masdaster.edma.R;
import com.github.masdaster.edma.databinding.ListItemLoadoutBinding;
import com.github.masdaster.edma.models.CommanderLoadout;
import com.github.masdaster.edma.models.CommanderLoadoutWeapon;

public class CommanderLoadoutsAdapter extends ListAdapter<CommanderLoadout,
        CommanderLoadoutsAdapter.loadoutViewHolder> {

    private final Context context;


    public CommanderLoadoutsAdapter(Context ctx) {
        // Parent class setup
        super(new DiffUtil.ItemCallback<>() {
            @Override
            public boolean areItemsTheSame(@NonNull CommanderLoadout oldItem,
                                           @NonNull CommanderLoadout newItem) {
                return areContentsTheSame(oldItem, newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull CommanderLoadout oldItem,
                                              @NonNull CommanderLoadout newItem) {
                return oldItem.getLoadoutId() == newItem.getLoadoutId();
            }
        });

        this.context = ctx;
    }

    @NonNull
    @Override
    public loadoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemLoadoutBinding itemBinding = ListItemLoadoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new loadoutViewHolder(itemBinding);
    }

    private void setLoadoutWeaponDisplay(CommanderLoadoutWeapon weapon, TextView labelTextView, TextView textView) {
        if (weapon != null) {
            labelTextView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);

            if (weapon.getMagazineName() != null) {
                textView.setText(context.getString(
                        R.string.weapon_display,
                        weapon.getName(),
                        weapon.getMagazineName()
                ));
            }
            else {
                textView.setText(context.getString(
                        R.string.weapon_display_without_magazine,
                        weapon.getName()
                ));
            }

        } else {
            labelTextView.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final loadoutViewHolder holder, final int position) {
        CommanderLoadout currentLoadout = getItem(holder.getAdapterPosition());

        // Suit picture
        holder.viewBinding.suitImageView.setImageResource(getSuitPicture(currentLoadout.getSuitType()));
        final Matrix matrix = holder.viewBinding.suitImageView.getImageMatrix();
        final float imageWidth = holder.viewBinding.suitImageView.getDrawable().getIntrinsicWidth();
        final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        final float scaleRatio = screenWidth / imageWidth;
        matrix.postScale(scaleRatio, scaleRatio);
        holder.viewBinding.suitImageView.setImageMatrix(matrix);

        // Name
        if (currentLoadout.getLoadoutName() != null) {
            holder.viewBinding.currentLoadoutLayout.loadoutTextView.setText(currentLoadout.getLoadoutName());
        } else {
            holder.viewBinding.currentLoadoutLayout.loadoutTextView.setText(context.getString(R.string.loadout_with_number, currentLoadout.getLoadoutId()));
        }

        holder.viewBinding.currentLoadoutLayout.suitGradeRatingBar.setProgress(currentLoadout.getSuitGrade());
        holder.viewBinding.currentLoadoutLayout.suitTextView.setText(currentLoadout.getSuitName());

        // Weapons
        setLoadoutWeaponDisplay(
                currentLoadout.getFirstPrimaryWeapon(),
                holder.viewBinding.currentLoadoutLayout.firstPrimaryWeaponLabelTextView,
                holder.viewBinding.currentLoadoutLayout.firstPrimaryWeaponTextView
        );
        setLoadoutWeaponDisplay(
                currentLoadout.getSecondPrimaryWeapon(),
                holder.viewBinding.currentLoadoutLayout.secondaryPrimaryWeaponLabelTextView,
                holder.viewBinding.currentLoadoutLayout.secondaryPrimaryWeaponTextView
        );
        setLoadoutWeaponDisplay(
                currentLoadout.getSecondaryWeapon(),
                holder.viewBinding.currentLoadoutLayout.secondaryWeaponLabelTextView,
                holder.viewBinding.currentLoadoutLayout.secondarWeaponTextView
        );
    }

    @DrawableRes
    private int getSuitPicture(String suitType){
        switch(suitType){
            default:
                return R.drawable.remlok_flight_suit;
            case "UtilitySuit":
                return R.drawable.maverick_utility_suit;
            case "TacticalSuit":
                return R.drawable.dominator_tactical_suit;
            case "ExplorationSuit":
                return R.drawable.artemis_explorer_suit;
        }
    }

    public static class loadoutViewHolder extends RecyclerView.ViewHolder {
        private final ListItemLoadoutBinding viewBinding;

        loadoutViewHolder(final ListItemLoadoutBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
        }
    }
}
