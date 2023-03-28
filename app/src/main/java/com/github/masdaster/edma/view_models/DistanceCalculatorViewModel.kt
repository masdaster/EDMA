package com.github.masdaster.edma.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.masdaster.edma.models.ProxyResult
import com.github.masdaster.edma.models.SystemsDistance
import com.github.masdaster.edma.network.DistanceCalculatorNetwork
import kotlinx.coroutines.launch

class DistanceCalculatorViewModel(application: Application) : AndroidViewModel(application) {
    private val distanceCalculationResult = MutableLiveData<ProxyResult<SystemsDistance>>()

    fun computeDistanceBetweenSystems(firstSystemName: String, secondSystemName: String) {
        viewModelScope.launch {
            distanceCalculationResult.postValue(
                DistanceCalculatorNetwork.getDistance(
                    getApplication(),
                    firstSystemName,
                    secondSystemName
                )
            )
        }
    }

    fun getDistanceBetweenSystemsResult(): LiveData<ProxyResult<SystemsDistance>> {
        return distanceCalculationResult
    }
}