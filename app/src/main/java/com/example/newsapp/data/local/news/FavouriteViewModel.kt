package com.example.newsapp.data.local.news

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.remote.Article
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavouriteViewModel(application: Application) : AndroidViewModel(application = application) {

    private val dao = NewsAppDatabase.getDatabase(application).newsDao()

    val favoriteArticles = dao.getAllFavorites().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )


    fun removeFromFavorites(article: Article) {
        viewModelScope.launch {
            dao.removeFromFavorites(article.toEntity())
        }
    }


    private fun Article.toEntity(): FavoriteArticleEntity {
        return FavoriteArticleEntity(
            url = this.url,
            title = this.title,
            urlToImage = this.urlToImage,
            publishedAt = this.publishedAt,
            description = this.description,
            sourceName = this.source.name
        )
    }
}