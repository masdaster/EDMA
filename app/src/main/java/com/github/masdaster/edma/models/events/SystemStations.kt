package com.github.masdaster.edma.models.events

import com.github.masdaster.edma.models.Station

data class SystemStations(val success: Boolean, val stations: List<Station>)
