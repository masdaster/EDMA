package com.github.masdaster.edma.models

data class CommodityBestPricesResult(
    val bestStationsToBuy: List<CommodityBestPricesStationResult>,
    val bestStationsToSell: List<CommodityBestPricesStationResult>
)

