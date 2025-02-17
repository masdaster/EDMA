package com.github.masdaster.edma.models.apis.EDSM;

import com.google.gson.annotations.SerializedName;

public class EDSMPositionResponse extends EDSMBaseResponse {

    @SerializedName("system")
    public String system;

    @SerializedName("systemId")
    public int systemId;

    @SerializedName("firstDiscover")
    public boolean firstDiscover;
}
