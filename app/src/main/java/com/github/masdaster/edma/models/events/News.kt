package com.github.masdaster.edma.models.events

import com.github.masdaster.edma.models.NewsArticle

data class News(val success: Boolean, val articles: List<NewsArticle>)