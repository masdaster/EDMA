package com.github.masdaster.edma.models.apis.EDSM;

import com.google.gson.annotations.SerializedName;

public class EDSMBaseResponse {
    @SerializedName("msgnum")
    public int responseCode;

    @SerializedName("msg")
    public String responseStatus;
}
