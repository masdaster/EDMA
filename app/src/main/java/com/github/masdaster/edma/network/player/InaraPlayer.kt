package com.github.masdaster.edma.network.player

import android.content.Context
import com.github.masdaster.edma.BuildConfig
import com.github.masdaster.edma.R
import com.github.masdaster.edma.models.CommanderCredits
import com.github.masdaster.edma.models.CommanderFleet
import com.github.masdaster.edma.models.CommanderLoadout
import com.github.masdaster.edma.models.CommanderLoadouts
import com.github.masdaster.edma.models.CommanderPosition
import com.github.masdaster.edma.models.CommanderRank
import com.github.masdaster.edma.models.CommanderRanks
import com.github.masdaster.edma.models.ProxyResult
import com.github.masdaster.edma.models.apis.Inara.InaraProfileRequestBody
import com.github.masdaster.edma.models.apis.Inara.InaraProfileRequestBody.InaraRequestBodyEvent
import com.github.masdaster.edma.models.apis.Inara.InaraProfileRequestBody.InaraRequestBodyEvent.InaraRequestBodyEventData
import com.github.masdaster.edma.models.apis.Inara.InaraProfileRequestBody.InaraRequestBodyHeader
import com.github.masdaster.edma.network.retrofit.InaraRetrofit
import com.github.masdaster.edma.singletons.RetrofitSingleton
import com.github.masdaster.edma.utils.DateUtils
import com.github.masdaster.edma.utils.SettingsUtils


class InaraPlayer(val context: Context) : PlayerNetwork {

    private val inaraRetrofit: InaraRetrofit = RetrofitSingleton.getInstance()
        .getInaraRetrofit(context.applicationContext)
    private val apiKey =
        SettingsUtils.getString(context, context.getString(R.string.settings_cmdr_inara_api_key))

    private var commanderName = context.getString(R.string.commander)

    override fun isUsable(): Boolean {
        val enabled =
            SettingsUtils.getBoolean(
                context,
                context.getString(R.string.settings_cmdr_inara_enable),
                false
            )
        return enabled && !apiKey.isNullOrEmpty()
    }

    override fun getCommanderName(): String {
        return commanderName
    }

    private fun buildRequestBody(): InaraProfileRequestBody {
        val res = InaraProfileRequestBody()

        // Build header
        res.header = InaraRequestBodyHeader()
        res.header.ApiKey = apiKey
        res.header.ApplicationName = context.getString(R.string.app_name)
        res.header.ApplicationVersion = BuildConfig.VERSION_NAME
        res.header.IsBeingDeveloped = BuildConfig.DEBUG

        // Build event
        val profileEvent = InaraRequestBodyEvent()
        profileEvent.EventName = "getCommanderProfile"
        profileEvent.EventTimestamp = DateUtils.getUtcIsoDate()
        profileEvent.EventData = InaraRequestBodyEventData()
        profileEvent.EventData.SearchName = null
        res.events = ArrayList()
        res.events.add(profileEvent)
        return res
    }

    override suspend fun getRanks(): ProxyResult<CommanderRanks> {
        return try {
            val apiRanks = inaraRetrofit.getProfile(buildRequestBody())

            lateinit var combatRank: CommanderRank
            lateinit var tradeRank: CommanderRank
            lateinit var explorationRank: CommanderRank
            lateinit var cqcRank: CommanderRank
            lateinit var empireRank: CommanderRank
            lateinit var federationRank: CommanderRank

            for (rank in apiRanks.events[0].EventData.CommanderRanksPilot) {

                when (rank.RankName) {
                    "combat" -> {
                        combatRank = CommanderRank(
                            context.resources
                                .getStringArray(R.array.ranks_combat)[rank.RankValue],
                            rank.RankValue,
                            (rank.RankProgress * 100).toInt()
                        )
                    }

                    "trade" -> {
                        tradeRank = CommanderRank(
                            context.resources
                                .getStringArray(R.array.ranks_trade)[rank.RankValue],
                            rank.RankValue,
                            (rank.RankProgress * 100).toInt()
                        )
                    }

                    "exploration" -> {
                        explorationRank = CommanderRank(
                            context.resources
                                .getStringArray(R.array.ranks_explorer)[rank.RankValue],
                            rank.RankValue,
                            (rank.RankProgress * 100).toInt()
                        )
                    }

                    "cqc" -> {
                        cqcRank = CommanderRank(
                            context.resources
                                .getStringArray(R.array.ranks_cqc)[rank.RankValue],
                            rank.RankValue,
                            (rank.RankProgress * 100).toInt()
                        )
                    }

                    "empire" -> {
                        empireRank = CommanderRank(
                            context.resources
                                .getStringArray(R.array.ranks_empire)[rank.RankValue],
                            rank.RankValue,
                            (rank.RankProgress * 100).toInt()
                        )
                    }

                    "federation" -> {
                        federationRank = CommanderRank(
                            context.resources
                                .getStringArray(R.array.ranks_federation)[rank.RankValue],
                            rank.RankValue,
                            (rank.RankProgress * 100).toInt()
                        )
                    }
                }
            }

            commanderName = apiRanks.events[0].EventData.CommanderName
            ProxyResult(
                data = CommanderRanks(
                    combatRank,
                    tradeRank,
                    explorationRank,
                    cqcRank,
                    null,
                    null,
                    federationRank,
                    empireRank
                ), error = null
            )
        } catch (t: Throwable) {
            ProxyResult(data = null, error = t)
        }
    }

    override suspend fun getCredits(): ProxyResult<CommanderCredits> {
        throw UnsupportedOperationException("Inara cannot fetch credits")
    }

    override suspend fun getFleet(): ProxyResult<CommanderFleet> {
        throw UnsupportedOperationException("EDSM cannot fetch fleet")
    }

    override suspend fun getCurrentLoadout(): ProxyResult<CommanderLoadout> {
        throw UnsupportedOperationException("Inara cannot fetch loadout")
    }

    override suspend fun getAllLoadouts(): ProxyResult<CommanderLoadouts> {
        throw UnsupportedOperationException("Inara cannot fetch loadouts")
    }

    override suspend fun getPosition(): ProxyResult<CommanderPosition> {
        throw UnsupportedOperationException("Inara cannot fetch position")
    }

}