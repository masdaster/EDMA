package com.github.masdaster.edma.network.retrofit

import com.github.masdaster.edma.models.apis.Inara.InaraProfileRequestBody
import com.github.masdaster.edma.models.apis.Inara.InaraProfileResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface InaraRetrofit {
    @POST("inapi/v1/")
    suspend fun getProfile(@Body body: InaraProfileRequestBody): InaraProfileResponse
}