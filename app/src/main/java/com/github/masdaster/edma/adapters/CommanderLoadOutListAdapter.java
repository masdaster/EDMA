package com.github.masdaster.edma.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.masdaster.edma.databinding.ListItemLoadOutListLoadOutBinding;
import com.github.masdaster.edma.models.CommanderLoadOutInformation;
import com.github.masdaster.edma.models.Suit;
import com.github.masdaster.edma.utils.MiscUtils;

public class CommanderLoadOutListAdapter extends ListAdapter<CommanderLoadOutInformation, CommanderLoadOutListAdapter.LoadOutViewHolder> {

    public CommanderLoadOutListAdapter() {
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
        MiscUtils.loadLoadOutImage(holder.viewBinding.suitImageView, suit);
    }

    public static class LoadOutViewHolder extends RecyclerView.ViewHolder {
        private final ListItemLoadOutListLoadOutBinding viewBinding;

        LoadOutViewHolder(final ListItemLoadOutListLoadOutBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
        }
    }
}
