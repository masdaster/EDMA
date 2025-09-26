package com.github.masdaster.edma.utils

import android.content.Context
import com.github.masdaster.edma.R

object StationTypeUtils {
    fun getStationTypeText(context: Context, isPlanetary: Boolean, isFleetCarrier: Boolean, isSettlement: Boolean): String? {
        return when {
            isPlanetary -> {
                context.getString(R.string.planetary)
            }

            isFleetCarrier -> {
                context.getString(R.string.fleet_carrier)
            }

            isSettlement -> {
                context.getString(R.string.settlement)
            }

            else -> null
        }
    }
}