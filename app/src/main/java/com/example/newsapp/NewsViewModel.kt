package com.example.newsapp

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.local.news.FavoriteArticleEntity
import com.example.newsapp.data.local.news.NewsAppDatabase
import com.example.newsapp.data.remote.Article
import com.example.newsapp.data.remote.NewsApiService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
class NewsViewModel : ViewModel() {
private val apiKey = "344e477ac241420db1e7501341b3ff48"

private val retrofit = Retrofit.Builder()
.baseUrl("https://newsapi.org/")
.addConverterFactory(GsonConverterFactory.create())
.build()

private val api = retrofit.create(NewsApiService::class.java)

var topicNews = mutableStateOf<List<Article>>(emptyList())
private set

var breakingNews = mutableStateOf<List<Article>>(emptyList())
private set

var isLoading = mutableStateOf(false)
var errorMessage = mutableStateOf<String?>(null)

private val _favorites = mutableStateListOf<Article>()
val favorites: List<Article> get() = _favorites


fun toggleFavorite(article: Article) {
if (_favorites.contains(article)) _favorites.remove(article)
else _favorites.add(article)
}

fun isFavorite(article: Article): Boolean = _favorites.contains(article)

fun fetchNews(topic: String = "technology") {
viewModelScope.launch {
isLoading.value = true
errorMessage.value = null

try {
val topicResponse = api.getTopicNews(topic, apiKey)
val breakingResponse = api.getBreakingNews(apiKey = apiKey)

if (topicResponse.isSuccessful && breakingResponse.isSuccessful) {
topicNews.value = topicResponse.body()?.articles ?: emptyList()
breakingNews.value = breakingResponse.body()?.articles ?: emptyList()
} else {
errorMessage.value = "Failed to fetch news: ${topicResponse.message()}"
}
} catch (e: Exception) {
errorMessage.value = "Error: ${e.localizedMessage}"
} finally {
isLoading.value = false
}
}
}
}
 **/


class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val apiKey = "344e477ac241420db1e7501341b3ff48"

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://newsapi.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(NewsApiService::class.java)

    private val dao = NewsAppDatabase.getDatabase(application).newsDao()

    // Observe the favorites from the database using Flow.
    val favoriteArticles = dao.getAllFavorites().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    var topicNews = mutableStateOf<List<Article>>(emptyList())
        private set

    var breakingNews = mutableStateOf<List<Article>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    // Track the in-memory list of favorites for quick access
    private val _favorites = mutableStateListOf<Article>()
    val favorites: List<Article> get() = _favorites

    // Toggle the favorite status of an article, and update the UI accordingly
    fun toggleFavorite(article: Article) {
        viewModelScope.launch {
            val isFav = dao.getAllFavorites().first().any { it.url == article.url }

            // If it's already a favorite, remove it; otherwise, add it to favorites
            if (isFav) {
                dao.removeFromFavorites(article.toEntity())
                _favorites.remove(article) // Update in-memory list
            } else {
                dao.addToFavorites(article.toEntity())
                _favorites.add(article) // Update in-memory list
            }
        }
    }

    // Check if the article is a favorite
    fun isFavorite(article: Article): Boolean {
        return _favorites.contains(article) || favoriteArticles.value.any { it.url == article.url }
    }

    // Fetch the latest news
    fun fetchNews(topic: String = "technology") {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val topicResponse = api.getTopicNews(topic, apiKey)
                val breakingResponse = api.getBreakingNews(apiKey = apiKey)

                if (topicResponse.isSuccessful && breakingResponse.isSuccessful) {
                    topicNews.value = topicResponse.body()?.articles ?: emptyList()
                    breakingNews.value = breakingResponse.body()?.articles ?: emptyList()
                } else {
                    errorMessage.value = "Failed to fetch news: ${topicResponse.message()}"
                }
            } catch (e: Exception) {
                errorMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // Convert Article to FavoriteArticleEntity
    private fun Article.toEntity(): FavoriteArticleEntity {
        return FavoriteArticleEntity(
            title = this.title,
            description = this.description,
            url = this.url,
            urlToImage = this.urlToImage,
            publishedAt = this.publishedAt,
            sourceName = this.source.name
        )
    }
}
