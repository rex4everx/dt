package com.example.museart.data.repository

import com.example.museart.model.Post
import kotlinx.coroutines.flow.Flow
import java.io.File

interface PostRepository {
    suspend fun createPost(userId: String, content: String, imageFile: File? = null): Result<Post>
    suspend fun getPostById(id: String): Result<Post>
    suspend fun deletePost(id: String): Result<Boolean>
    suspend fun likePost(userId: String, postId: String): Result<Boolean>
    suspend fun unlikePost(userId: String, postId: String): Result<Boolean>
    suspend fun repostPost(userId: String, originalPostId: String, content: String = ""): Result<Post>
    suspend fun isLiked(userId: String, postId: String): Result<Boolean>
    suspend fun isReposted(userId: String, postId: String): Result<Boolean>
    fun getFeedPosts(userId: String): Flow<List<Post>>
    fun getUserPosts(userId: String): Flow<List<Post>>
    fun searchPosts(query: String): Flow<List<Post>>
}

