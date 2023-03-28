package com.github.masdaster.edma.models

data class CommanderLoadOutInformation(
    val loadOutSlotId: Int,
    val name: String?,
    val suit: Suit,
    val isCurrentLoadOut: Boolean
)