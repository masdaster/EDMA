package com.github.masdaster.edma.models

import org.threeten.bp.Instant

data class FactionChartEntryData(
    val name: String, val state: String, val updateDate: Instant,
    val influence: Float
)
