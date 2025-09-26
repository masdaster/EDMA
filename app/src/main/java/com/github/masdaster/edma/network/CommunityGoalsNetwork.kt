package com.github.masdaster.edma.network

import android.content.Context
import com.github.masdaster.edma.models.CommunityGoal
import com.github.masdaster.edma.models.ProxyResult
import com.github.masdaster.edma.models.events.CommunityGoals
import com.github.masdaster.edma.singletons.RetrofitSingleton

object CommunityGoalsNetwork {

    suspend fun getCommunityGoals(ctx: Context): ProxyResult<CommunityGoals> {
        val retrofit = RetrofitSingleton.getInstance()
            .getEdApiV4Retrofit(ctx.applicationContext)

        try {
            val goalsRet = retrofit.getCommunityGoals()

            val goals = try {
                val goals: MutableList<CommunityGoal> = ArrayList()
                for (item in goalsRet) {
                    goals.add(CommunityGoal.fromCommunityGoalsItemResponse(item))
                }
                CommunityGoals(goals)
            } catch (e: Exception) {
                CommunityGoals(ArrayList())
            }
            return ProxyResult(goals, null)
        } catch (t: Throwable) {
            return ProxyResult(null, t)
        }
    }
}