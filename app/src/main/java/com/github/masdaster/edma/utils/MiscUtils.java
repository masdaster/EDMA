package com.github.masdaster.edma.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.github.masdaster.edma.R;
import com.github.masdaster.edma.activities.SystemDetailsActivity;
import com.github.masdaster.edma.models.ShipInformation;
import com.github.masdaster.edma.models.Suit;

public class MiscUtils {

    public static void putTextInClipboard(Context ctx, String label, String content,
                                          boolean displayNotification) {
        try {
            ClipboardManager clipboard = (ClipboardManager)
                    ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard == null) {
                throw new Exception(ctx.getString(R.string.content_copied_to_clipboard_error));
            }
            ClipData clip = ClipData.newPlainText(label, content);
            clipboard.setPrimaryClip(clip);

            if (displayNotification) {
                Toast t = Toast.makeText(ctx, R.string.content_copied_to_clipboard, Toast.LENGTH_SHORT);
                t.show();
            }
        } catch (Exception e) {

            if (displayNotification) {
                Toast t = Toast.makeText(ctx, R.string.content_copied_to_clipboard_error,
                        Toast.LENGTH_SHORT);
                t.show();
            }

        }
    }

    public static void startIntentWithFadeAnimation(Context ctx, Intent i) {
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(ctx,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();

        ctx.startActivity(i, bundle);
    }

    public static void startIntentToSystemDetails(Context ctx, String systemName) {
        Intent i = new Intent(ctx, SystemDetailsActivity.class);
        i.putExtra("data", systemName);
        ctx.startActivity(i);
    }

    public static void setTextFromHtml(TextView textView, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml(text));
        }
    }

    public static void loadShipImage(ImageView imageView, ShipInformation shipInformation) {
        String shipUrl = String.format("https://ed.9cw.eu/ships/%s/picture", shipInformation.getInternalModel());
        Glide.with(imageView).load(shipUrl).error(R.drawable.ship_placeholder).centerCrop().into(imageView);
    }

    public static void loadLoadOutImage(ImageView imageView, Suit suit) {
        imageView.setImageResource(getSuitPicture(suit.getType()));
        final Matrix matrix = imageView.getImageMatrix();
        final float imageWidth = imageView.getDrawable().getIntrinsicWidth();
        final int screenWidth = imageView.getResources().getDisplayMetrics().widthPixels;
        final float scaleRatio = screenWidth / imageWidth;
        matrix.postScale(scaleRatio, scaleRatio);
        imageView.setImageMatrix(matrix);
    }

    @DrawableRes
    private static int getSuitPicture(String suitType){
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
}
