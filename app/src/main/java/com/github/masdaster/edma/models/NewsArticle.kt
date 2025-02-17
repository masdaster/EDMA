package com.github.masdaster.edma.models

import android.os.Parcelable
import com.github.masdaster.edma.models.apis.EDAPIV4.NewsArticleResponse
import kotlinx.parcelize.Parcelize
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant

@Parcelize
data class NewsArticle(
    val title: String, val content: String, val picture: String?,
    val publishedDate: Instant
) : Parcelable {
    companion object {
        fun fromNewsArticleResponse(res: NewsArticleResponse): NewsArticle {
            return NewsArticle(
                res.Title,
                res.Content.replace('\r', '\n'),
                res.Picture,
                DateTimeUtils.toInstant(res.PublishedDate)
            )
        }
    }
}
