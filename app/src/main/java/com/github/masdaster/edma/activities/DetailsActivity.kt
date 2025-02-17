package com.github.masdaster.edma.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.masdaster.edma.adapters.CommunityGoalsAdapter
import com.github.masdaster.edma.adapters.NewsAdapter
import com.github.masdaster.edma.databinding.ActivityDetailsBinding
import com.github.masdaster.edma.models.CommunityGoal
import com.github.masdaster.edma.models.NewsArticle
import com.github.masdaster.edma.utils.ThemeUtils

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(ThemeUtils.getThemeToUse(this))
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Set toolbar
        setSupportActionBar(binding.includeAppBar.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Common recycler view setup
        val linearLayoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = linearLayoutManager

        // Get the goal or the article
        if (intent.extras != null) {
            val communityGoal = intent.extras?.getParcelable<CommunityGoal>("goal")
            val article = intent.extras?.getParcelable<NewsArticle>("article")
            if (communityGoal == null && article != null) {
                val isGalnet = intent.extras?.getBoolean("isGalnet") ?: false
                newsArticleSetup(article, isGalnet)
            } else if (communityGoal != null) {
                communityGoalSetup(communityGoal)
            }
        }

        binding.recyclerView.smoothScrollToPosition(-10) // because recycler view may not start on top
    }

    private fun communityGoalSetup(communityGoal: CommunityGoal) {
        supportActionBar?.title = communityGoal.title

        // Adapter setup
        val adapter = CommunityGoalsAdapter(this, binding.recyclerView, true)
        binding.recyclerView.adapter = adapter
        adapter.submitList(listOf(communityGoal))
    }

    private fun newsArticleSetup(article: NewsArticle, isGalnet: Boolean) {
        supportActionBar?.title = article.title

        // Adapter view setup
        val adapter = NewsAdapter(this, binding.recyclerView, true, isGalnet)
        binding.recyclerView.adapter = adapter
        adapter.submitList(listOf(article))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
