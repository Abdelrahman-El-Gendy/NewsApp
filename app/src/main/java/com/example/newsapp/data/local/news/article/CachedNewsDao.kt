package com.example.newsapp.data.local.news.article

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedNewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<CachedArticleEntity>)

    @Query("SELECT * FROM cached_articles")
    fun getAllCachedArticles(): Flow<List<CachedArticleEntity>>

    @Query("DELETE FROM cached_articles")
    suspend fun clearAll()

    @Query("SELECT * FROM cached_articles WHERE type = :type")
    fun getArticlesByType(type: String): Flow<List<CachedArticleEntity>>
}