package com.github.masdaster.edma.models

import com.github.masdaster.edma.models.apis.EDAPIV4.NewsArticleResponse.SystemHistoryResponse

data class SystemHistoryResult(val name: String, val history: List<FactionHistory>) {
    companion object {
        fun fromSystemHistoryResponse(res: SystemHistoryResponse): SystemHistoryResult {
            val history = ArrayList<FactionHistory>()
            res.History.mapTo(history) { FactionHistory.fromFactionHistoryResponse(it) }
            return SystemHistoryResult(res.FactionName, history)
        }
    }
}
