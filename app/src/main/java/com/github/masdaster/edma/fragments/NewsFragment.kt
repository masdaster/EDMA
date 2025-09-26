package com.github.masdaster.edma.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.github.masdaster.edma.R
import com.github.masdaster.edma.adapters.NewsAdapter
import com.github.masdaster.edma.models.ProxyResult
import com.github.masdaster.edma.models.events.News
import com.github.masdaster.edma.utils.NotificationsUtils
import com.github.masdaster.edma.utils.SettingsUtils
import com.github.masdaster.edma.view_models.NewsViewModel

class NewsFragment : AbstractListFragment<NewsAdapter>() {

    private val viewModel: NewsViewModel by activityViewModels()
    private lateinit var currentLanguage: String

    private val newsLanguage: String
        get() = SettingsUtils.getString(context, getString(R.string.settings_news_lang))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentLanguage = newsLanguage

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        viewModel.getNews().observe(this, ::onNewsEvent)
        return view
    }

    override fun getData() {
        viewModel.fetchNews(currentLanguage)
    }

    override fun onResume() {
        super.onResume()

        // Refresh if language changed
        if (currentLanguage != newsLanguage) {
            getData()
        }
    }

    override fun getNewRecyclerViewAdapter(): NewsAdapter {
        return NewsAdapter(context, binding.recyclerView, false, false)
    }

    private fun onNewsEvent(news: ProxyResult<News>) {
        // Error case
        if (news.error != null || news.data == null) {
            endLoading(true)
            NotificationsUtils.displayGenericDownloadErrorSnackbar(activity)
            return
        }

        endLoading(news.data.articles.isEmpty())
        recyclerViewAdapter.submitList(news.data.articles)
    }

    companion object {
        const val NEWS_FRAGMENT_TAG = "news_fragment"
    }
}
