package com.github.masdaster.edma.network

import android.content.Context
import com.github.masdaster.edma.models.ProxyResult
import com.github.masdaster.edma.models.ServerStatus
import com.github.masdaster.edma.singletons.RetrofitSingleton

object ServerStatusNetwork {

    suspend fun getStatus(ctx: Context): ProxyResult<ServerStatus> {

        val edsmRetrofit = RetrofitSingleton.getInstance()
            .getEDSMRetrofit(ctx.applicationContext)

        return try {
            val edsmServerStatus = edsmRetrofit.getServerStatus()
            ProxyResult(
                ServerStatus(edsmServerStatus.Message)
            )
        } catch (t: Throwable) {
            ProxyResult(null, t)
        }
    }
}