package com.github.masdaster.edma.models

import com.github.masdaster.edma.models.apis.EDAPIV4.CommodityWithPriceResponse

data class CommoditiesListResult(
    val name: String, val id: Long, val averageBuyPrice: Long, val averageSellPrice: Long,
    val isRare: Boolean, val category: String
) {
    companion object {
        fun fromEDApiCommodityPrice(res: CommodityWithPriceResponse): CommoditiesListResult {
            return CommoditiesListResult(
                res.Commodity.Name,
                res.Commodity.Id,
                res.AverageBuyPrice,
                res.AverageSellPrice,
                res.Commodity.IsRare,
                res.Commodity.Category
            )
        }
    }
}

