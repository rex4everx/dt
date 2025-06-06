package com.example.museart.data.repository

import com.example.museart.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun registerUser(username: String, email: String, password: String, displayName: String): Result<User>
    suspend fun loginUser(email: String, password: String): Result<User>
    suspend fun getUserById(id: String): Result<User>
    suspend fun getUserByUsername(username: String): Result<User>
    suspend fun updateUser(user: User): Result<User>
    suspend fun followUser(followerId: String, followingId: String): Result<Boolean>
    suspend fun unfollowUser(followerId: String, followingId: String): Result<Boolean>
    suspend fun isFollowing(followerId: String, followingId: String): Result<Boolean>
    fun getFollowers(userId: String): Flow<List<User>>
    fun getFollowing(userId: String): Flow<List<User>>
    fun searchUsers(query: String): Flow<List<User>>
    suspend fun getCurrentUser(): Result<User>
    suspend fun logout(): Result<Boolean>
}

