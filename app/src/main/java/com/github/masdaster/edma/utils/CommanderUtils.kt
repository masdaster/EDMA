package com.github.masdaster.edma.utils

import android.content.Context
import com.github.masdaster.edma.R
import com.github.masdaster.edma.network.player.EDSMPlayer
import com.github.masdaster.edma.network.player.FrontierPlayer
import com.github.masdaster.edma.network.player.InaraPlayer

object CommanderUtils {
    fun getCommanderName(context: Context): String {
        return SettingsUtils.getString(
            context,
            context.getString(R.string.settings_cmdr_name),
            R.string.commander
        )
    }

    fun hasFleetData(context: Context): Boolean {
        val frontierPlayer = FrontierPlayer(context)
        if (frontierPlayer.isUsable()) {
            return true
        }

        return false
    }

    fun hasLoadoutData(context: Context): Boolean {
        val frontierPlayer = FrontierPlayer(context)
        if (frontierPlayer.isUsable()) {
            return true
        }

        return false
    }

    fun hasCreditsData(context: Context): Boolean {
        val edsmPlayer = EDSMPlayer(context)
        if (edsmPlayer.isUsable()) {
            return true
        }

        val frontierPlayer = FrontierPlayer(context)
        if (frontierPlayer.isUsable()) {
            return true
        }

        return false
    }

    fun hasPositionData(context: Context): Boolean {
        val edsmPlayer = EDSMPlayer(context)
        if (edsmPlayer.isUsable()) {
            return true
        }

        val frontierPlayer = FrontierPlayer(context)
        if (frontierPlayer.isUsable()) {
            return true
        }

        return false
    }

    fun hasCurrentLoadoutData(context: Context): Boolean = FrontierPlayer(context).isUsable()

    fun hasCommanderStatus(context: Context): Boolean {
        val edsmPlayer = EDSMPlayer(context)
        val inaraPlayer = InaraPlayer(context)
        val frontierPlayer = FrontierPlayer(context)

        return edsmPlayer.isUsable() || inaraPlayer.isUsable() || frontierPlayer.isUsable()
    }

    fun setCachedCurrentCommanderPosition(context: Context, systemName: String) {
        SettingsUtils.setString(
            context,
            context.getString(R.string.commander_position_cache_key),
            systemName
        )
    }

    fun getCachedCurrentCommanderPosition(context: Context): String? {
        return SettingsUtils.getString(
            context,
            context.getString(R.string.commander_position_cache_key)
        )
    }
}