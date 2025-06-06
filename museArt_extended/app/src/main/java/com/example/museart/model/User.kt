package com.example.museart.model

data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val bio: String = "",
    val profileImageUrl: String = "",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val postsCount: Int = 0,
    val isVerified: Boolean = false,
    val isFollowing: Boolean = false
)

