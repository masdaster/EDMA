package com.github.masdaster.edma.models.apis.Inara;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InaraProfileRequestBody {

    @SerializedName("header")
    public InaraRequestBodyHeader header;

    @SerializedName("events")
    public List<InaraRequestBodyEvent> events;

    public static class InaraRequestBodyHeader {
        @SerializedName("appName")
        public String ApplicationName;

        @SerializedName("appVersion")
        public String ApplicationVersion;

        @SerializedName("isBeingDeveloped")
        public boolean IsBeingDeveloped;

        @SerializedName("APIkey")
        public String ApiKey;

        @SerializedName("commanderName")
        public String CommanderName;
    }

    public static class InaraRequestBodyEvent {
        @SerializedName("eventName")
        public String EventName;

        @SerializedName("eventTimestamp")
        public String EventTimestamp;

        @SerializedName("eventData")
        public InaraRequestBodyEventData EventData;

        public static class InaraRequestBodyEventData {
            @SerializedName("searchName")
            public String SearchName;
        }
    }
}