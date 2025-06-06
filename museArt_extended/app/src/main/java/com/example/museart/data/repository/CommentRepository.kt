package com.example.museart.data.repository

import com.example.museart.model.Comment
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    suspend fun createComment(userId: String, postId: String, content: String): Result<Comment>
    suspend fun getCommentById(id: String): Result<Comment>
    suspend fun deleteComment(id: String): Result<Boolean>
    suspend fun likeComment(userId: String, commentId: String): Result<Boolean>
    suspend fun unlikeComment(userId: String, commentId: String): Result<Boolean>
    suspend fun isLiked(userId: String, commentId: String): Result<Boolean>
    fun getCommentsByPostId(postId: String): Flow<List<Comment>>
    fun getCommentsByUserId(userId: String): Flow<List<Comment>>
}

