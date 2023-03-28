package com.github.masdaster.edma.models.events

import com.github.masdaster.edma.models.CommodityBestPricesStationResult

data class CommodityBestPrices(
    val success: Boolean,
    val bestStationsToBuy: List<CommodityBestPricesStationResult>,
    val bestStationsToSell: List<CommodityBestPricesStationResult>,
)