package com.github.masdaster.edma.models

import android.content.Context
import android.os.Parcelable
import android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE
import com.github.masdaster.edma.R
import com.github.masdaster.edma.models.apis.EDAPIV4.CommunityGoalsResponse
import kotlinx.parcelize.Parcelize
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant

@Parcelize
data class CommunityGoal(
    val isOngoing: Boolean, val id: Long, val title: String,
    val description: String, val objective: String, val reward: String,
    val currentTier: Int,
    val totalTier: Int, val contributors: Long, val station: String,
    val system: String, val endDate: Instant,
    val refreshDate: Instant, var distanceToPlayer: Float?
) : Parcelable {

    fun getTierString(): String {
        return "$currentTier / $totalTier"
    }

    fun getRefreshDateString(ctx: Context): String {
        return try {
            val date = android.text.format.DateUtils.getRelativeTimeSpanString(
                refreshDate.toEpochMilli(),
                Instant.now().toEpochMilli(),
                0, FORMAT_ABBREV_RELATIVE
            )
                .toString()
            ctx.getString(R.string.cg_last_update, date)
        } catch (e: Exception) {
            ctx.getString(R.string.cg_last_update, ctx.getString(R.string.unknown))
        }
    }

    fun getEndDate(ctx: Context): String {
        if (!isOngoing) {
            return ctx.getString(R.string.finished)
        }
        return try {
            android.text.format.DateUtils.getRelativeTimeSpanString(
                endDate.toEpochMilli(),
                Instant.now().toEpochMilli(), 0, FORMAT_ABBREV_RELATIVE
            ).toString()
        } catch (e: Exception) {
            ctx.getString(R.string.unknown)
        }
    }

    companion object {
        fun fromCommunityGoalsItemResponse(res: CommunityGoalsResponse):
                CommunityGoal {

            return CommunityGoal(
                res.Ongoing, res.Id, res.Title, res.Description, res.Objective,
                res.Reward, res.CurrentTier, res.MaxTier,
                res.Contributors, res.Station, res.System,
                DateTimeUtils.toInstant(res.EndDate),
                DateTimeUtils.toInstant(res.LastUpdate), null
            )
        }
    }
}
