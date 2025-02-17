package com.github.masdaster.edma.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.github.masdaster.edma.R;
import com.github.masdaster.edma.animation.ProgressBarAnimation;
import com.github.masdaster.edma.models.CommanderRank;

public class RankUtils {

    public static void setTempContent(Context ctx, View rootView, String name) {
        TextView titleView = rootView.findViewById(R.id.titleTextView);
        TextView progressView = rootView.findViewById(R.id.progressTextView);
        ProgressBar progressBar = rootView.findViewById(R.id.progressBar);

        titleView.setText(String.format("%s : %s", name, ctx.getString(R.string.unknown)));
        progressView.setText(ctx.getResources().getString(R.string.rank_progress, 0));
        progressBar.setProgress(0);
    }

    public static void setContent(Context ctx, View rootView, String logoUrl, CommanderRank rank, String description) {
        ImageView logoView = rootView.findViewById(R.id.itemImageView);
        TextView titleView = rootView.findViewById(R.id.titleTextView);
        TextView progressView = rootView.findViewById(R.id.progressTextView);
        ProgressBar progressBar = rootView.findViewById(R.id.progressBar);

        Glide.with(logoView).load(logoUrl).error(R.drawable.rank_placeholder).centerCrop().into(logoView);
        titleView.setText(String.format("%s : %s", description, rank.getName()));

        // If rank progress is -1 it means it's not supported by the network used so hide it
        if (rank.getProgress() == -1) {
            progressView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            return;
        }

        progressView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressView.setText(ctx.getResources().getString(R.string.rank_progress,
                rank.getProgress()));

        // Animate progress bar
        ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, progressBar.getProgress(),
                rank.getProgress());
        anim.setDuration(1000);
        progressBar.startAnimation(anim);
    }
}
