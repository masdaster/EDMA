package com.github.masdaster.edma.models.events

data class FrontierTokensEvent(
    val success: Boolean, val accessToken: String,
    val refreshToken: String
)
