package com.github.masdaster.edma.models.events

data class ResultsList<TDataType>(val success: Boolean, val results: List<TDataType>)