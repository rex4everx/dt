package com.example.museart.model

import java.util.Date

data class Post(
    val id: String,
    val userId: String,
    val user: User? = null,
    val content: String,
    val imageUrl: String? = null,
    val createdAt: Date,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val repostsCount: Int = 0,
    val isLiked: Boolean = false,
    val isReposted: Boolean = false,
    val originalPost: Post? = null // Для репостов
)

