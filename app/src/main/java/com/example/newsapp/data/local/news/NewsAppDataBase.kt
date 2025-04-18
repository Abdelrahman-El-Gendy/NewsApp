package com.example.newsapp.data.local.news

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlin.jvm.java

@Database(
    entities = [FavoriteArticleEntity::class],
    version = 2,
    exportSchema = false
)
abstract class NewsAppDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao

    companion object {
        @Volatile
        private var INSTANCE: NewsAppDatabase? = null

        fun getDatabase(context: Context): NewsAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NewsAppDatabase::class.java,
                    "news_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
