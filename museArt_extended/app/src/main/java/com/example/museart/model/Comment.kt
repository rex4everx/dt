package com.example.museart.model

import java.util.Date

data class Comment(
    val id: String,
    val postId: String,
    val userId: String,
    val user: User? = null,
    val content: String,
    val createdAt: Date,
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

