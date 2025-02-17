package com.github.masdaster.edma.models

import com.github.masdaster.edma.models.apis.EDAPIV4.NewsArticleResponse.FactionHistoryResponse
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant

data class FactionHistory(
    val state: String, val updateDate: Instant,
    val influence: Float
) {
    companion object {
        fun fromFactionHistoryResponse(res: FactionHistoryResponse): FactionHistory {
            return FactionHistory(
                res.State, DateTimeUtils.toInstant(res.UpdatedAt),
                res.Influence
            )
        }
    }
}
