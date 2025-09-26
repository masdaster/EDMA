package com.github.masdaster.edma.utils;


import android.app.Activity;

import com.google.android.material.snackbar.Snackbar;

import com.github.masdaster.edma.R;

public class NotificationsUtils {

    public static void displayGenericDownloadErrorSnackbar(Activity activity) {
        displaySnackbar(activity, activity.getString(R.string.download_error));
    }

    public static void displaySnackbar(Activity activity, String message) {
        Snackbar snackbar = Snackbar
                .make(activity.findViewById(android.R.id.content),
                        message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
