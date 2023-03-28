package com.github.masdaster.edma.models.events

import com.github.masdaster.edma.models.CommodityBestPricesStationResult

data class CommodityDetailsBuy(
    val success: Boolean,
    val stations: List<CommodityBestPricesStationResult>
)
