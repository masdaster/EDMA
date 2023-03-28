package com.github.masdaster.edma.adapters;

import android.content.Context;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.masdaster.edma.R;
import com.github.masdaster.edma.databinding.ListItemLoadOutListLoadOutBinding;
import com.github.masdaster.edma.models.CommanderLoadOutInformation;
import com.github.masdaster.edma.models.Suit;

import java.text.NumberFormat;

public class CommanderLoadOutListAdapter extends ListAdapter<CommanderLoadOutInformation, CommanderLoadOutListAdapter.LoadOutViewHolder> {

    private final Context context;

    public CommanderLoadOutListAdapter(Context ctx) {
        // Parent class setup
        super(new DiffUtil.ItemCallback<CommanderLoadOutInformation>() {
            @Override
            public boolean areItemsTheSame(@NonNull CommanderLoadOutInformation oldItem,
                                           @NonNull CommanderLoadOutInformation newItem) {
                return areContentsTheSame(oldItem, newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull CommanderLoadOutInformation oldItem,
                                              @NonNull CommanderLoadOutInformation newItem) {
                return oldItem.getLoadOutSlotId() == newItem.getLoadOutSlotId();
            }
        });

        this.context = ctx;
    }

    @NonNull
    @Override
    public LoadOutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemLoadOutListLoadOutBinding itemBinding = ListItemLoadOutListLoadOutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LoadOutViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final LoadOutViewHolder holder, final int position) {
        CommanderLoadOutInformation loadOutInformation = getItem(holder.getAdapterPosition());
        Suit suit = loadOutInformation.getSuit();

        // Load out name / Suit name ...
        holder.viewBinding.titleTextView.setText(suit.getName());
        if (loadOutInformation.getName() != null && !loadOutInformation.getName().equals(suit.getName())) {
            holder.viewBinding.subtitleTextView.setVisibility(View.VISIBLE);
            holder.viewBinding.subtitleTextView.setText(loadOutInformation.getName());
        } else {
            holder.viewBinding.subtitleTextView.setVisibility(View.GONE);
        }

        // Current load out
        holder.viewBinding.currentLoadOutLabelTextView.setVisibility(loadOutInformation.isCurrentLoadOut() ? View.VISIBLE : View.GONE);

        // Suit grade
        holder.viewBinding.gradeRatingBar.setProgress(suit.getGrade());

        // Suit picture
        holder.viewBinding.suitImageView.setImageResource(getSuitPicture(suit.getType()));
        final Matrix matrix = holder.viewBinding.suitImageView.getImageMatrix();
        final float imageWidth = holder.viewBinding.suitImageView.getDrawable().getIntrinsicWidth();
        final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        final float scaleRatio = screenWidth / imageWidth;
        matrix.postScale(scaleRatio, scaleRatio);
        holder.viewBinding.suitImageView.setImageMatrix(matrix);
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

    public static class LoadOutViewHolder extends RecyclerView.ViewHolder {
        private final ListItemLoadOutListLoadOutBinding viewBinding;

        LoadOutViewHolder(final ListItemLoadOutListLoadOutBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
        }
    }
}
