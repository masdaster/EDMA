package com.github.masdaster.edma.fragments

import android.os.Bundle
import com.github.masdaster.edma.R
import com.github.masdaster.edma.adapters.NewsAdapter
import com.github.masdaster.edma.models.events.GalnetNews
import com.github.masdaster.edma.network.NewsNetwork
import com.github.masdaster.edma.utils.NotificationsUtils
import com.github.masdaster.edma.utils.SettingsUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class GalnetFragment : AbstractListFragment<NewsAdapter>() {
    private lateinit var currentLanguage: String

    private val newsLanguage: String
        get() = SettingsUtils.getString(context, getString(R.string.settings_news_lang))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentLanguage = newsLanguage
    }

    override fun onResume() {
        super.onResume()

        // Refresh if language changed
        if (currentLanguage != newsLanguage) {
            currentLanguage = newsLanguage
            getData()
        }
    }

    override fun getData() {
        NewsNetwork.getGalnetNews(context, currentLanguage)
    }

    override fun getNewRecyclerViewAdapter(): NewsAdapter {
        return NewsAdapter(context, binding.recyclerView, false, true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewsEvent(news: GalnetNews) {
        // Error case
        if (!news.success) {
            endLoading(true)
            NotificationsUtils.displayGenericDownloadErrorSnackbar(activity)
            return
        }

        endLoading(news.articles.isEmpty())
        recyclerViewAdapter.submitList(news.articles)
    }

    companion object {
        const val GALNET_FRAGMENT_TAG = "galnet_fragment"
    }
}
