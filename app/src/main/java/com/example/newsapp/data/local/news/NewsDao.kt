package com.example.newsapp.data.local.news

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.local.news.article.CachedArticleEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface NewsDao {
    // Favorites
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(article: FavoriteArticleEntity)

    @Delete
    suspend fun removeFromFavorites(article: FavoriteArticleEntity)

    @Query("SELECT * FROM favorite_articles")
    fun getAllFavorites(): Flow<List<FavoriteArticleEntity>>

}

