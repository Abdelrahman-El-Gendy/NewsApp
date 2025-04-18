package com.example.newsapp.data.local.news

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteArticleEntity(
    @PrimaryKey val url: String,
    val title: String,
    val urlToImage: String?,
    val publishedAt: String,
    val description: String,
    val sourceName: String
)
