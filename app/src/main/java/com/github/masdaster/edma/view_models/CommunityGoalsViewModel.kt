package com.github.masdaster.edma.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.masdaster.edma.models.ProxyResult
import com.github.masdaster.edma.models.events.CommunityGoals
import com.github.masdaster.edma.network.CommunityGoalsNetwork
import kotlinx.coroutines.launch

class CommunityGoalsViewModel(application: Application) : AndroidViewModel(application) {
    private val communityGoalsResult = MutableLiveData<ProxyResult<CommunityGoals>>()

    fun fetchCommunityGoals() {
        viewModelScope.launch {
            communityGoalsResult.postValue(
                CommunityGoalsNetwork.getCommunityGoals(getApplication())
            )
        }
    }

    fun getCommunityGoals(): LiveData<ProxyResult<CommunityGoals>> {
        return communityGoalsResult
    }
}