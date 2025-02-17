package com.github.masdaster.edma.models

data class ShipInformation(
    val id: Int,
    val model: String,
    val internalModel: String,
    val name: String?,
    val systemName: String,
    val stationName: String,
    val hullValue: Long,
    val modulesValue: Long,
    val cargoValue: Long,
    val totalValue: Long,
    val isCurrentShip: Boolean
)