package com.example.newsapp.data.local.news

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.newsapp.data.local.news.article.CachedArticleEntity
import com.example.newsapp.data.local.news.article.CachedNewsDao
import kotlin.jvm.java


@Database(
    entities = [FavoriteArticleEntity::class, CachedArticleEntity::class],
    version = 5, // Make sure to bump the version if you already have existing tables
    exportSchema = false
)
abstract class NewsAppDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun cachedNewsDao(): CachedNewsDao

    companion object {
        @Volatile
        private var INSTANCE: NewsAppDatabase? = null

        fun getDatabase(context: Context): NewsAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NewsAppDatabase::class.java,
                    "news_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}