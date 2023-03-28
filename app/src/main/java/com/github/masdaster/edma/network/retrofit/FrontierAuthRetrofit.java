package com.github.masdaster.edma.network.retrofit;

import com.github.masdaster.edma.models.apis.FrontierAuth.FrontierAccessTokenRequestBody;
import com.github.masdaster.edma.models.apis.FrontierAuth.FrontierAccessTokenResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FrontierAuthRetrofit {
    @POST("token")
    Call<FrontierAccessTokenResponse> getAccessToken(@Body FrontierAccessTokenRequestBody body);
}
