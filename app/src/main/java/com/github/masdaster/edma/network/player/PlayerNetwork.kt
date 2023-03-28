package com.github.masdaster.edma.network.player

import com.github.masdaster.edma.models.*


interface PlayerNetwork {
    fun isUsable(): Boolean
    fun getCommanderName(): String

    suspend fun getPosition(): ProxyResult<CommanderPosition>
    suspend fun getRanks(): ProxyResult<CommanderRanks>
    suspend fun getCredits(): ProxyResult<CommanderCredits>
    suspend fun getFleet(): ProxyResult<CommanderFleet>
}