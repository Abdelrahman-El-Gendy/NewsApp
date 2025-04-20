package com.example.newsapp.data.local.news.article

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_articles")
data class CachedArticleEntity(
    @PrimaryKey val url: String,
    val title: String,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: String,
    val sourceName: String?,
    val type: String // "topic" or "breaking"
)

