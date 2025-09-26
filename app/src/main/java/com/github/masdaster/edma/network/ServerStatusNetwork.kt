package com.github.masdaster.edma.network

import android.content.Context
import com.github.masdaster.edma.models.ProxyResult
import com.github.masdaster.edma.models.ServerStatus
import com.github.masdaster.edma.singletons.RetrofitSingleton

object ServerStatusNetwork {

    suspend fun getStatus(ctx: Context): ProxyResult<ServerStatus> {

        val edApiRetrofit = RetrofitSingleton.getInstance()
            .getEdApiV4Retrofit(ctx.applicationContext)

        return try {
            val serverStatus = edApiRetrofit.getGameServerHealth()
            ProxyResult(
                ServerStatus(serverStatus.Status)
            )
        } catch (t: Throwable) {
            ProxyResult(null, t)
        }
    }
}