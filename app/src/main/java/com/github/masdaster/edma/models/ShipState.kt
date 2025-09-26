package com.github.masdaster.edma.models

data class ShipState(
    val alive: Boolean,
    val cockpitBreached: Boolean,
    val hullHealth: Int,
    val integrityHealth: Int,
    val paintworkHealth: Int,
    val shieldHealth: Int,
    val shieldUp: Boolean,
    val oxygenRemaining: Int
)