package com.github.masdaster.edma.models.apis.Frontier;

import com.google.gson.annotations.SerializedName;


public class FrontierProfileResponse {

    @SerializedName("commander")
    public FrontierProfileCommanderResponse Commander;

    @SerializedName("lastSystem")
    public FrontierProfileSystemResponse LastSystem;

    public class FrontierProfileCommanderResponse {
        @SerializedName("name")
        public String Name;

        @SerializedName("credits")
        public long Credits;

        @SerializedName("debt")
        public long Debt;

        @SerializedName("rank")
        public FrontierProfileCommanderRankResponse Rank;
    }

    public static class FrontierProfileCommanderRankResponse {
        @SerializedName("combat")
        public int Combat;

        @SerializedName("trade")
        public int Trade;

        @SerializedName("explore")
        public int Explore;

        @SerializedName("empire")
        public int Empire;

        @SerializedName("federation")
        public int Federation;

        @SerializedName("cqc")
        public int Cqc;

        @SerializedName("soldier")
        public int Mercenary;

        @SerializedName("exobiologist")
        public int Exobiologist;
    }

    public static class FrontierProfileSystemResponse {
        @SerializedName("name")
        public String Name;
    }
}