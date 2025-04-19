package com.example.newsapp.Presentation

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.local.news.FavoriteArticleEntity
import com.example.newsapp.data.local.news.NewsAppDatabase
import com.example.newsapp.data.local.news.article.CachedArticleEntity
import com.example.newsapp.data.remote.Article
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.data.remote.Source
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

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

    private val database = NewsAppDatabase.Companion.getDatabase(application)
    private val dao = database.newsDao()
    private val cachedDao = database.cachedNewsDao()

    val favoriteArticles = dao.getAllFavorites().stateIn(
        viewModelScope,
        SharingStarted.Companion.WhileSubscribed(),
        emptyList()
    )

    var topicNews = mutableStateOf<List<Article>>(emptyList())
        private set

    var breakingNews = mutableStateOf<List<Article>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    private val _favorites = mutableStateListOf<Article>()
    val favorites: List<Article> get() = _favorites


//    var isOffline = mutableStateOf(false)
//        private set


    fun toggleFavorite(article: Article) {
        viewModelScope.launch {
            val isFav = dao.getAllFavorites().first().any { it.url == article.url }
            if (isFav) {
                dao.removeFromFavorites(article.toEntity())
                _favorites.remove(article)
            } else {
                dao.addToFavorites(article.toEntity())
                _favorites.add(article)
            }
        }
    }

    fun isFavorite(article: Article): Boolean {
        return _favorites.contains(article) || favoriteArticles.value.any { it.url == article.url }
    }

    val cachedArticles = cachedDao.getAllCachedArticles()
        .map { list -> list.map { it.toArticle() } }
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(), emptyList())

    fun fetchNews(topic: String = "technology") {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
//            isOffline.value = false // Initially


            try {
                val topicResponse = api.getTopicNews(topic, apiKey)
                val breakingResponse = api.getBreakingNews(apiKey = apiKey)

                if (topicResponse.isSuccessful && breakingResponse.isSuccessful) {
                    val topicArticles = topicResponse.body()?.articles ?: emptyList()
                    val breakingArticles = breakingResponse.body()?.articles ?: emptyList()

                    topicNews.value = topicArticles
                    breakingNews.value = breakingArticles

                    // Cache both topic and breaking news
                    cachedDao.clearAll()
                    val cachedTopic = topicArticles.map { it.toCachedEntity("topic") }
                    val cachedBreaking = breakingArticles.map { it.toCachedEntity("breaking") }
                    cachedDao.insertAll(cachedTopic + cachedBreaking)

                } else {
                    throw Exception("Server error: ${topicResponse.message()}")
                }

            } catch (e: IOException) {
//                isOffline.value = true
                // Offline - show cached topic & breaking news
                val cachedTopic =
                    cachedDao.getArticlesByType("topic").firstOrNull()?.map { it.toArticle() }
                        ?: emptyList()
                val cachedBreaking =
                    cachedDao.getArticlesByType("breaking").firstOrNull()?.map { it.toArticle() }
                        ?: emptyList()

                topicNews.value = cachedTopic
                breakingNews.value = cachedBreaking

                errorMessage.value = null // optional: silent fallback
            } catch (e: Exception) {
                errorMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

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

    private fun Article.toCachedEntity(type: String): CachedArticleEntity {
        return CachedArticleEntity(
            url = url,
            title = title,
            description = description,
            urlToImage = urlToImage,
            publishedAt = publishedAt,
            sourceName = source.name,
            type = type
        )
    }


    private fun CachedArticleEntity.toArticle(): Article {
        return Article(
            source = Source(id = null, name = this.sourceName.toString()),
            author = null,
            title = this.title,
            description = this.description.toString(),
            url = this.url,
            urlToImage = this.urlToImage,
            publishedAt = this.publishedAt,
            content = ""
        )
    }
}