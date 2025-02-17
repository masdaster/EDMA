package com.github.masdaster.edma.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.masdaster.edma.models.*
import com.github.masdaster.edma.models.exceptions.DataNotInitializedException
import com.github.masdaster.edma.network.player.EDSMPlayer
import com.github.masdaster.edma.network.player.FrontierPlayer
import com.github.masdaster.edma.network.player.InaraPlayer
import com.github.masdaster.edma.utils.CommanderUtils
import kotlinx.coroutines.launch

class CommanderViewModel(application: Application) : AndroidViewModel(application) {
    private val edsmPlayer: EDSMPlayer = EDSMPlayer(application)
    private val inaraPlayer: InaraPlayer = InaraPlayer(application)
    private val frontierPlayer: FrontierPlayer = FrontierPlayer(application)

    private val credits = MutableLiveData<ProxyResult<CommanderCredits>>()
    private val position = MutableLiveData<ProxyResult<CommanderPosition>>()
    private val ranks = MutableLiveData<ProxyResult<CommanderRanks>>()
    private val fleet = MutableLiveData<ProxyResult<CommanderFleet>>()
    private val currentShip = MutableLiveData<ProxyResult<Ship>>()
    private val loadOutList = MutableLiveData<ProxyResult<CommanderLoadOutList>>()
    private val currentLoadOut = MutableLiveData<ProxyResult<CommanderLoadOut>>()

    fun clearCachedData() {
        // We set the values to a null result with a specific exception to mark it as not initialized
        viewModelScope.launch {
            credits.postValue(ProxyResult(null, DataNotInitializedException()))
            position.postValue(ProxyResult(null, DataNotInitializedException()))
            ranks.postValue(ProxyResult(null, DataNotInitializedException()))
            fleet.postValue(ProxyResult(null, DataNotInitializedException()))
            currentShip.postValue(ProxyResult(null, DataNotInitializedException()))
            loadOutList.postValue(ProxyResult(null, DataNotInitializedException()))
            currentLoadOut.postValue(ProxyResult(null, DataNotInitializedException()))
        }
    }

    fun fetchCredits() {
        val servicesToTries = listOf(frontierPlayer, edsmPlayer)
        viewModelScope.launch {
            for ((index, service) in servicesToTries.withIndex()) {
                if (!service.isUsable()) {
                    continue
                }

                val retValue = service.getCredits()

                if (index == servicesToTries.size - 1 || (retValue.error == null && retValue.data != null)) {
                    credits.postValue(retValue)
                    break
                }
            }
        }
    }

    fun getCredits(): LiveData<ProxyResult<CommanderCredits>> {
        return credits
    }

    private fun updateCachedPosition(newPosition: ProxyResult<CommanderPosition>) {
        if (newPosition.data != null && newPosition.error == null) {
            CommanderUtils.setCachedCurrentCommanderPosition(
                getApplication(),
                newPosition.data.systemName
            )
        }
    }

    fun fetchPosition() {
        val servicesToTries = listOf(frontierPlayer, edsmPlayer)

        viewModelScope.launch {
            for ((index, service) in servicesToTries.withIndex()) {
                if (!service.isUsable()) {
                    continue
                }

                val retValue = service.getPosition()

                if (index == servicesToTries.size - 1 || (retValue.error == null && retValue.data != null)) {
                    position.postValue(retValue)
                    updateCachedPosition(retValue)
                    break
                }
            }
        }
    }

    fun getPosition(): LiveData<ProxyResult<CommanderPosition>> {
        return position
    }

    fun fetchRanks() {
        val servicesToTries = listOf(inaraPlayer, frontierPlayer, edsmPlayer)

        viewModelScope.launch {
            for ((index, service) in servicesToTries.withIndex()) {
                if (!service.isUsable()) {
                    continue
                }

                val retValue = service.getRanks()
                if (index == servicesToTries.size - 1 || (retValue.error == null && retValue.data != null)) {
                    ranks.postValue(retValue)
                    break
                }
            }
        }
    }

    fun getRanks(): LiveData<ProxyResult<CommanderRanks>> {
        return ranks
    }

    fun fetchFleet() {
        if (frontierPlayer.isUsable()) {
            viewModelScope.launch {
                fleet.postValue(frontierPlayer.getFleet())
            }
        }
    }

    fun getFleet(): LiveData<ProxyResult<CommanderFleet>> {
        return fleet
    }

    fun fetchCurrentShip() {
        if (frontierPlayer.isUsable()) {
            viewModelScope.launch {
                currentShip.postValue(frontierPlayer.getCurrentShip())
            }
        }
    }

    fun getCurrentShip(): LiveData<ProxyResult<Ship>> {
        return currentShip
    }

    fun fetchCurrentLoadOut() {
        if (frontierPlayer.isUsable()) {
            viewModelScope.launch {
                currentLoadOut.postValue(frontierPlayer.getCurrentLoadOut())
            }
        }
    }

    fun getCurrentLoadOut(): LiveData<ProxyResult<CommanderLoadOut>> {
        return currentLoadOut
    }

    fun fetchLoadOutList() {
        if (frontierPlayer.isUsable()) {
            viewModelScope.launch {
                loadOutList.postValue(frontierPlayer.getLoadOutList())
            }
        }
    }

    fun getLoadOutList(): LiveData<ProxyResult<CommanderLoadOutList>> {
        return loadOutList
    }
}