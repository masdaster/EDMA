package com.github.masdaster.edma.models.apis.EDAPIV4;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class CommodityBestPricesResponse {
    @SerializedName("best_stations_to_buy")
    public List<CommodityBestPricesStationResponse> BestStationsToBuy;

    @SerializedName("best_stations_to_sell")
    public List<CommodityBestPricesStationResponse> BestStationsToSell;

    public static class CommodityBestPricesStationResponse {
        @SerializedName("distance_to_arrival")
        public float DistanceToArrival;

        @SerializedName("distance_from_reference_system")
        public float DistanceFromReferenceSystem;

        @SerializedName("last_market_update")
        public Date LastMarketUpdate;

        @SerializedName("max_landing_pad_size")
        public String MaxLandingPad;

        @SerializedName("name")
        public String Name;

        @SerializedName("is_planetary")
        public boolean IsPlanetary;

        @SerializedName("is_settlement")
        public boolean IsSettlement;

        @SerializedName("type")
        public String Type;

        @SerializedName("system_name")
        public String SystemName;

        @SerializedName("quantity")
        public long Quantity;

        @SerializedName("price")
        public long Price;

        @SerializedName("price_percentage_difference")
        public int PricePercentageDifference;
    }
}
