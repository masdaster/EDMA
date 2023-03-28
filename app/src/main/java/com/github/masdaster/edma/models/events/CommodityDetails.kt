package com.github.masdaster.edma.models.events

import com.github.masdaster.edma.models.CommodityDetailsResult

data class CommodityDetails(val success: Boolean, val commodityDetails: CommodityDetailsResult?)
