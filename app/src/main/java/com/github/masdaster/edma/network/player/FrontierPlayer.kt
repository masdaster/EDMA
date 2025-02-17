package com.github.masdaster.edma.network.player

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.github.masdaster.edma.R
import com.github.masdaster.edma.models.*
import com.github.masdaster.edma.models.apis.Frontier.FrontierProfileResponse
import com.github.masdaster.edma.models.apis.Frontier.FrontierProfileResponse.FrontierProfileCommanderRankResponse
import com.github.masdaster.edma.models.exceptions.FrontierAuthNeededException
import com.github.masdaster.edma.network.retrofit.FrontierRetrofit
import com.github.masdaster.edma.singletons.RetrofitSingleton
import com.github.masdaster.edma.utils.InternalNamingUtils
import com.github.masdaster.edma.utils.SettingsUtils
import org.threeten.bp.Instant


class FrontierPlayer(val context: Context) : PlayerNetwork {

    private val frontierRetrofit: FrontierRetrofit = RetrofitSingleton.getInstance()
        .getFrontierRetrofit(context.applicationContext)

    private var lastFetch: Instant = Instant.MIN

    private var cachedRanks: CommanderRanks? = null
    private var cachedCredits: CommanderCredits? = null
    private var cachedPosition: CommanderPosition? = null
    private var cachedFleet: CommanderFleet? = null
    private var cachedLoadOutList: CommanderLoadOutList? = null
    private var cachedCurrentShip: Ship? = null
    private var cachedCurrentLoadOut: CommanderLoadOut? = null

    override fun isUsable(): Boolean {
        return SettingsUtils.getBoolean(
            context,
            context.getString(R.string.settings_cmdr_frontier_enable)
        )
    }

    private fun shouldFetchNewData(): Boolean {
        synchronized(lastFetch) {
            val ret = when {
                lastFetch.isBefore(Instant.now().minusSeconds(60)) -> true
                else -> false
            }

            // Update last fetch date
            lastFetch = Instant.now()

            return ret
        }
    }

    private fun getRanksFromApiResponse(profileResponse: FrontierProfileResponse): CommanderRanks {
        val apiRanks: FrontierProfileCommanderRankResponse = profileResponse.Commander.Rank

        // Combat
        val combatRank = CommanderRank(
            context.resources
                .getStringArray(R.array.ranks_combat)[apiRanks.Combat],
            apiRanks.Combat, -1
        )

        // Trade
        val tradeRank = CommanderRank(
            context.resources
                .getStringArray(R.array.ranks_trade)[apiRanks.Trade],
            apiRanks.Trade, -1
        )

        // Explore
        val exploreRank = CommanderRank(
            context.resources
                .getStringArray(R.array.ranks_explorer)[apiRanks.Explore],
            apiRanks.Explore, -1
        )

        // CQC
        val cqcRank = CommanderRank(
            context.resources
                .getStringArray(R.array.ranks_cqc)[apiRanks.Cqc],
            apiRanks.Cqc, -1
        )

        // Mercenary
        val mercenary = CommanderRank(
            context.resources
                .getStringArray(R.array.ranks_mercenary)[apiRanks.Mercenary],
            apiRanks.Mercenary, -1
        )

        // Exobiologist
        val exobiologist = CommanderRank(
            context.resources
                .getStringArray(R.array.ranks_exobiologist)[apiRanks.Exobiologist],
            apiRanks.Exobiologist, -1
        )

        // Federation
        val federationRank = CommanderRank(
            context.resources
                .getStringArray(R.array.ranks_federation)[apiRanks.Federation],
            apiRanks.Federation, -1
        )

        // Empire
        val empireRank = CommanderRank(
            context.resources
                .getStringArray(R.array.ranks_empire)[apiRanks.Empire],
            apiRanks.Empire, -1
        )

        return CommanderRanks(
            combatRank, tradeRank, exploreRank,
            cqcRank, mercenary, exobiologist,
            federationRank, empireRank
        )
    }

    private suspend fun getProfile() {
        try {
            val apiResponse = frontierRetrofit.getProfileRaw()
            val rawResponse = JsonParser.parseString(apiResponse.string()).asJsonObject
            val profileResponse = Gson()
                .fromJson(rawResponse, FrontierProfileResponse::class.java)

            cachedPosition = CommanderPosition(profileResponse.LastSystem.Name, false)
            cachedCredits = CommanderCredits(profileResponse.Commander.Credits, profileResponse.Commander.Debt)
            cachedRanks = getRanksFromApiResponse(profileResponse)
            cachedFleet = getFleetFromApiResponse(rawResponse)
            cachedCurrentShip = getCurrentShipFromApiResponse(rawResponse)
            cachedLoadOutList = getLoadOutListFromApiResponse(rawResponse)
            cachedCurrentLoadOut = getCurrentLoadOutFromApiResponse(rawResponse)
        } catch (t: FrontierAuthNeededException) {
            lastFetch = Instant.MIN
            cachedCredits = null
            cachedRanks = null
            cachedFleet = null
            cachedCurrentShip = null
            cachedLoadOutList = null
            cachedCurrentLoadOut = null
            throw t
        }
    }

    private fun getFleetFromApiResponse(profileResponse: JsonObject): CommanderFleet {
        val currentShipId: Int = profileResponse.get("commander")
            .asJsonObject
            .get("currentShipId")
            .asInt

        // Sometimes the cAPI return an array, sometimes an object with indexes
        val responseList: MutableList<JsonElement> = ArrayList()
        if (profileResponse.get("ships").isJsonObject) {
            for ((_, value) in profileResponse.get("ships").asJsonObject.entrySet()) {
                responseList.add(value)
            }
        } else {
            for (ship in profileResponse.get("ships").asJsonArray) {
                responseList.add(ship)
            }
        }

        val shipsList: MutableList<ShipInformation> = ArrayList()

        for (entry in responseList) {
            val newShipInformation = createShipInformation(entry.asJsonObject, currentShipId)
            if (newShipInformation.isCurrentShip) {
                shipsList.add(0, newShipInformation)
            } else {
                shipsList.add(newShipInformation)
            }
        }

        return CommanderFleet(shipsList)
    }

    private fun createShipInformation(rawShip: JsonObject, currentShipId: Int): ShipInformation{
        var shipName: String? = null
        if (rawShip.has("shipName")) {
            shipName = rawShip["shipName"].asString
        }
        val value = rawShip["value"].asJsonObject
        val isCurrentShip = rawShip["id"].asInt == currentShipId
        return ShipInformation(
            rawShip["id"].asInt,
            InternalNamingUtils.getShipName(rawShip["name"].asString),
            rawShip["name"].asString.lowercase(),
            shipName,
            rawShip["starsystem"].asJsonObject["name"].asString,
            rawShip["station"].asJsonObject["name"].asString,
            value["hull"].asLong,
            value["modules"].asLong,
            value["cargo"].asLong,
            value["total"].asLong,
            isCurrentShip
        )
    }

    private fun getCurrentShipFromApiResponse(profileResponse: JsonObject): Ship {
        val rawShip = profileResponse.get("ship").asJsonObject
        val rawShipHealth = rawShip.get("health").asJsonObject

        return Ship(
            createShipInformation(rawShip, rawShip["id"].asInt),
            ShipState(
                rawShip["alive"].asBoolean,
                rawShip["cockpitBreached"].asBoolean,
                rawShipHealth["hull"].asInt,
                rawShipHealth["integrity"].asInt,
                rawShipHealth["paintwork"].asInt,
                rawShipHealth["shield"].asInt,
                rawShipHealth["shieldup"].asBoolean,
                rawShip["oxygenRemaining"].asInt
            ),
            rawShip
        )
    }

    private fun createSuit(rawSuit: JsonObject): Suit{
        val suitName = rawSuit["name"].asString
        val type = suitName.substringBefore('_')
        val className = suitName.substringAfter('_', "")
        return Suit(
            rawSuit["locName"].asString,
            type,
            if(className.isEmpty()) -1 else className.last().digitToInt()
        )
    }

    private fun createLoadOutInformation(rawLoadOut: JsonObject, currentLoadOutSlotId: Int): CommanderLoadOutInformation {
        val loadOutSlotId = rawLoadOut["loadoutSlotId"].asInt
        var loadOutName: String? = null
        if (rawLoadOut.has("name")) {
            loadOutName = rawLoadOut["name"].asString
        }
        return CommanderLoadOutInformation(
            loadOutSlotId,
            loadOutName,
            createSuit(rawLoadOut.getAsJsonObject("suit")),
            loadOutSlotId == currentLoadOutSlotId
        )
    }

    private fun getLoadOutListFromApiResponse(profileResponse: JsonObject): CommanderLoadOutList {
        val currentLoadOutSlotId: Int = profileResponse.getAsJsonObject("loadout")["loadoutSlotId"].asInt

        // Sometimes the cAPI return an array, sometimes an object with indexes
        val responseList: MutableList<JsonElement> = ArrayList()
        if (profileResponse.get("loadouts").isJsonObject) {
            for ((_, value) in profileResponse.get("loadouts").asJsonObject.entrySet()) {
                responseList.add(value)
            }
        } else {
            for (loadout in profileResponse.get("loadouts").asJsonArray) {
                responseList.add(loadout)
            }
        }

        val loadOutList: MutableList<CommanderLoadOutInformation> = ArrayList()
        for (entry in responseList) {
            val loadOutInformation = createLoadOutInformation(entry.asJsonObject, currentLoadOutSlotId)
            if (loadOutInformation.isCurrentLoadOut) {
                loadOutList.add(0, loadOutInformation)
            } else {
                loadOutList.add(loadOutInformation)
            }
        }
        return CommanderLoadOutList(loadOutList)
    }

    private fun getCurrentLoadOutFromApiResponse(profileResponse: JsonObject): CommanderLoadOut {
        val rawLoadOut = profileResponse.getAsJsonObject("loadout")
        val rawLoadOutState = rawLoadOut.getAsJsonObject("state")
        val rawSuitHealth = profileResponse.getAsJsonObject("suit").getAsJsonObject("state").getAsJsonObject("health")

        return CommanderLoadOut(
            createLoadOutInformation(rawLoadOut, rawLoadOut["loadoutSlotId"].asInt),
            CommanderLoadOutState(
                rawSuitHealth["hull"].asInt,
                rawLoadOutState["oxygenRemaining"].asInt,
                rawLoadOutState["energy"].asDouble
            )
        )
    }

    suspend fun getCurrentLoadOut(): ProxyResult<CommanderLoadOut> {
        if (!shouldFetchNewData() && cachedCurrentLoadOut != null) {
            return ProxyResult(cachedCurrentLoadOut)
        }

        return try {
            getProfile()
            ProxyResult(cachedCurrentLoadOut)
        } catch (t: Throwable) {
            ProxyResult(data = null, error = t)
        }
    }

    suspend fun getCurrentShip(): ProxyResult<Ship> {
        if (!shouldFetchNewData() && cachedCurrentShip != null) {
            return ProxyResult(cachedCurrentShip)
        }

        return try {
            getProfile()
            ProxyResult(cachedCurrentShip)
        } catch (t: Throwable) {
            ProxyResult(data = null, error = t)
        }
    }

    override suspend fun getRanks(): ProxyResult<CommanderRanks> {
        if (!shouldFetchNewData() && cachedRanks != null) {
            return ProxyResult(cachedRanks)
        }

        return try {
            getProfile()
            ProxyResult(cachedRanks)
        } catch (t: Throwable) {
            ProxyResult(data = null, error = t)
        }
    }

    override suspend fun getCredits(): ProxyResult<CommanderCredits> {
        if (!shouldFetchNewData() && cachedCredits != null) {
            return ProxyResult(cachedCredits)
        }

        return try {
            getProfile()
            ProxyResult(cachedCredits)
        } catch (t: Throwable) {
            ProxyResult(data = null, error = t)
        }
    }

    override suspend fun getFleet(): ProxyResult<CommanderFleet> {
        if (!shouldFetchNewData() && cachedFleet != null) {
            return ProxyResult(cachedFleet)
        }

        return try {
            getProfile()
            ProxyResult(cachedFleet)
        } catch (t: Throwable) {
            ProxyResult(data = null, error = t)
        }
    }

    suspend fun getLoadOutList(): ProxyResult<CommanderLoadOutList> {
        if (!shouldFetchNewData() && cachedLoadOutList != null) {
            return ProxyResult(cachedLoadOutList)
        }

        return try {
            getProfile()
            ProxyResult(cachedLoadOutList)
        } catch (t: Throwable) {
            ProxyResult(data = null, error = t)
        }
    }

    override suspend fun getPosition(): ProxyResult<CommanderPosition> {
        if (!shouldFetchNewData() && cachedPosition != null) {
            return ProxyResult(cachedPosition)
        }

        return try {
            getProfile()
            ProxyResult(cachedPosition)
        } catch (t: Throwable) {
            ProxyResult(data = null, error = t)
        }
    }
}