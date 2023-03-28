package com.github.masdaster.edma.models

data class SystemsDistance(
    val distanceInLy: Float,
    val firstSystemName: String,
    val firstSystemNeedsPermit: Boolean,
    val secondSystemName: String,
    val secondSystemNeedsPermit: Boolean,
)
