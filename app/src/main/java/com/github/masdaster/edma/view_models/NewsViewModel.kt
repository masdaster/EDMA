package com.github.masdaster.edma.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.masdaster.edma.models.ProxyResult
import com.github.masdaster.edma.models.events.News
import com.github.masdaster.edma.network.NewsNetwork
import kotlinx.coroutines.launch

class NewsViewModel(application: Application) : AndroidViewModel(application) {
    private val newsResult = MutableLiveData<ProxyResult<News>>()

    fun fetchNews(language: String) {
        viewModelScope.launch {
            newsResult.postValue(
                NewsNetwork.getNews(getApplication(), language)
            )
        }
    }

    fun getNews(): LiveData<ProxyResult<News>> {
        return newsResult
    }
}