package com.github.masdaster.edma.models

data class CommanderLoadout(
    val hasLoadout: Boolean,
    val loadoutId: Int,
    val loadoutName: String?,
    val suitName: String,
    val suitType: String,
    val suitGrade: Int,
    val firstPrimaryWeapon: CommanderLoadoutWeapon?,
    val secondPrimaryWeapon: CommanderLoadoutWeapon?,
    val secondaryWeapon: CommanderLoadoutWeapon?,
)
