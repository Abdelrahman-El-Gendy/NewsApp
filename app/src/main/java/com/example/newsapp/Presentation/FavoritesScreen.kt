package com.example.newsapp.Presentation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.newsapp.TopicNewsCard
import com.example.newsapp.data.local.news.FavoriteArticleEntity
import com.example.newsapp.data.local.news.FavouriteViewModel
import com.example.newsapp.data.remote.Article
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.newsapp.data.remote.Source

@Composable
fun FavoritesScreen(navController: NavController, viewModel: FavouriteViewModel = viewModel()) {
    val favorites by viewModel.favoriteArticles.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Saved Articles", style = MaterialTheme.typography.titleLarge)

        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No favorites yet.")
            }
        } else {
            LazyColumn {
                items(favorites) { article ->
                    TopicNewsCard(
                        article = article.toArticle(),
                        onClick = {
                            navController.navigate("webview?url=${Uri.encode(article.url)}")
                        },
                        onSaveClick = {
                            viewModel.removeFromFavorites(article.toArticle())
                        },
                        isFavorite = true
                    )
                }
            }
        }
    }
}

private fun FavoriteArticleEntity.toArticle(): Article {
    return Article(
        source = Source(id = null, name = "Favorite"),
        author = null,
        title = title,
        description = "",
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = ""
    )
}
