package com.github.masdaster.edma.network.player

import com.github.masdaster.edma.models.CommanderCredits
import com.github.masdaster.edma.models.CommanderFleet
import com.github.masdaster.edma.models.CommanderPosition
import com.github.masdaster.edma.models.CommanderRanks
import com.github.masdaster.edma.models.CommanderLoadout
import com.github.masdaster.edma.models.CommanderLoadouts
import com.github.masdaster.edma.models.ProxyResult


interface PlayerNetwork {
    fun isUsable(): Boolean
    fun getCommanderName(): String

    suspend fun getPosition(): ProxyResult<CommanderPosition>
    suspend fun getRanks(): ProxyResult<CommanderRanks>
    suspend fun getCredits(): ProxyResult<CommanderCredits>
    suspend fun getFleet(): ProxyResult<CommanderFleet>
    suspend fun getCurrentLoadout(): ProxyResult<CommanderLoadout>
    suspend fun getAllLoadouts(): ProxyResult<CommanderLoadouts>
}