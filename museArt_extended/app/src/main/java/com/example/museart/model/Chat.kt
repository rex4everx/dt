package com.example.museart.model

import java.util.Date

data class Chat(
    val id: String,
    val user1Id: String,
    val user2Id: String,
    val user1: User? = null,
    val user2: User? = null,
    val lastMessage: Message? = null,
    val unreadCount: Int = 0,
    val updatedAt: Date
)

