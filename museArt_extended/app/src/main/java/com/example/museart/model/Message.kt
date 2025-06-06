package com.example.museart.model

import java.util.Date

data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val sender: User? = null,
    val receiver: User? = null,
    val content: String,
    val imageUrl: String? = null,
    val createdAt: Date,
    val isRead: Boolean = false
)

