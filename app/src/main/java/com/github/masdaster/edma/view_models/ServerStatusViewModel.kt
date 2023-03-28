package com.github.masdaster.edma.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.masdaster.edma.models.ProxyResult
import com.github.masdaster.edma.models.ServerStatus
import com.github.masdaster.edma.network.ServerStatusNetwork
import kotlinx.coroutines.launch

class ServerStatusViewModel(application: Application) : AndroidViewModel(application) {
    private val serverStatus = MutableLiveData<ProxyResult<ServerStatus>>()

    fun fetchServerStatus() {
        viewModelScope.launch {
            serverStatus.postValue(ServerStatusNetwork.getStatus(getApplication()))
        }
    }

    fun getServerStatus(): LiveData<ProxyResult<ServerStatus>> {
        return serverStatus
    }
}