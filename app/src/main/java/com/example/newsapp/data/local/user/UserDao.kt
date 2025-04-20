package com.example.newsapp.data.local.user
import androidx.room.*

@Dao
interface UserDao {

    @Upsert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE email=:email AND password=:password")
    suspend fun login(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?


}