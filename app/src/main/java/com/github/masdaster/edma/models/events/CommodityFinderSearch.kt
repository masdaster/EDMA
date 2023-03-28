package com.github.masdaster.edma.models.events

data class CommodityFinderSearch(
    val commodityName: String, val systemName: String,
    val landingPadSize: String, val stockOrDemand: Int,
    val isSellingMode: Boolean
)