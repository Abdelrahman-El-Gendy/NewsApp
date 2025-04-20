package com.example.newsapp.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApiService {
    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country") country: String = "us",
        @Query("apiKey") apiKey: String
    ): Response<Root>

    @GET("v2/everything")
    suspend fun getTopicNews(
        @Query("q") topic: String,
        @Query("apiKey") apiKey: String
    ): Response<Root>
}
