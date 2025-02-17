package com.github.masdaster.edma.models

data class CommanderRanks constructor(
    val combat: CommanderRank,
    val trade: CommanderRank,
    val explore: CommanderRank,
    val cqc: CommanderRank,
    val federation: CommanderRank,
    val empire: CommanderRank,
    val mercenary: CommanderRank? = null,
    val exobiologist: CommanderRank? = null
)
