package com.github.masdaster.edma.utils;

import android.content.Context;

import java.text.NumberFormat;
import java.util.Locale;

public class MathUtils {

    public static NumberFormat getNumberFormat(Context context) {
        Locale userLocale = DateUtils.getCurrentLocale(context);
        return NumberFormat.getIntegerInstance(userLocale);
    }

    public static String getPriceDifferenceString(NumberFormat numberFormat, int priceDifference) {
        String result = numberFormat.format(priceDifference);
        if (priceDifference >= 0) {
            return "+" + result;
        } else {
            return result;
        }
    }

    public static int divAndRound(int x, int y){
        return (int)Math.round((double)x/y);
    }
}
