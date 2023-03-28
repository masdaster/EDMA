package com.github.masdaster.edma.models.events

import com.github.masdaster.edma.models.CommunityGoal

data class CommunityGoals(val success: Boolean, val goalsList: List<CommunityGoal>)
