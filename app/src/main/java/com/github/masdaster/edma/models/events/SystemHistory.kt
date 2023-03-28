package com.github.masdaster.edma.models.events

import com.github.masdaster.edma.models.SystemHistoryResult

data class SystemHistory(val success: Boolean, val history: List<SystemHistoryResult>)
