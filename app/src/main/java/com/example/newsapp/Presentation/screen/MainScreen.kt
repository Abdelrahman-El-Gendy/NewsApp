package com.example.newsapp.Presentation.screen

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.newsapp.Presentation.viewmodel.NewsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.example.newsapp.utils.BreakingNewsCard
import com.example.newsapp.utils.TopicNewsCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, viewModel: NewsViewModel = viewModel()) {
    val breakingNews by viewModel.breakingNews
    val topicNews by viewModel.topicNews
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.fetchNews("technology")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News App") },
                actions = {
                    IconButton(onClick = { navController.navigate("favorites") }) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Go to favorites"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                Text("Error: $errorMessage", color = Color.Red, modifier = Modifier.padding(16.dp))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text("Breaking News", style = MaterialTheme.typography.titleLarge)
                    LazyRow(contentPadding = PaddingValues(vertical = 8.dp)) {
                        items(breakingNews) { article ->
                            BreakingNewsCard(article = article) {
                                navController.navigate("webview?url=${Uri.encode(article.url)}")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Top Stories", style = MaterialTheme.typography.titleLarge)
                    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                        items(topicNews) { article ->
                            TopicNewsCard(
                                article = article,
                                onClick = {
                                    navController.navigate("webview?url=${Uri.encode(article.url)}")
                                },
                                onSaveClick = {
                                    if (!viewModel.isFavorite(article)) {
                                        viewModel.toggleFavorite(article)

                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Saved to favorites")
                                            navController.navigate("favorites")
                                        }
                                    } else {
                                        viewModel.toggleFavorite(article)
                                    }
                                },
                                isFavorite = viewModel.isFavorite(article)
                            )
                        }
                    }
                }
            }
        }
    }
}


