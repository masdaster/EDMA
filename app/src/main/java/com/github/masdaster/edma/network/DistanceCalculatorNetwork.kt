package com.github.masdaster.edma.network

import android.content.Context
import com.github.masdaster.edma.models.ProxyResult
import com.github.masdaster.edma.models.SystemsDistance
import com.github.masdaster.edma.singletons.RetrofitSingleton

object DistanceCalculatorNetwork {

    suspend fun getDistance(
        ctx: Context,
        firstSystemName: String,
        secondSystemName: String
    ): ProxyResult<SystemsDistance> {

        val edApiV4Retrofit = RetrofitSingleton.getInstance()
            .getEdApiV4Retrofit(ctx.applicationContext)

        return try {
            val res = edApiV4Retrofit.getSystemsDistance(firstSystemName, secondSystemName)
            ProxyResult(
                SystemsDistance(
                    res.DistanceInLy,
                    res.FirstSystem.Name,
                    res.FirstSystem.PermitRequired,
                    res.SecondSystem.Name,
                    res.SecondSystem.PermitRequired,
                )
            )
        } catch (t: Throwable) {
            ProxyResult(null, t)
        }
    }
}