package com.github.masdaster.edma.network.retrofit

import okhttp3.ResponseBody
import retrofit2.http.GET

interface FrontierRetrofit {
    @GET("profile")
    suspend fun getProfileRaw(): ResponseBody
}