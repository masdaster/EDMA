package com.github.masdaster.edma.models

import com.google.gson.JsonElement

data class Ship(
    val information: ShipInformation,
    val state: ShipState,
    val raw: JsonElement
)