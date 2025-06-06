package com.example.museart.model

import java.util.Date

enum class NotificationType {
    LIKE,
    COMMENT,
    REPOST,
    FOLLOW,
    MENTION
}

data class Notification(
    val id: String,
    val userId: String,
    val triggerUserId: String,
    val triggerUser: User? = null,
    val type: NotificationType,
    val postId: String? = null,
    val commentId: String? = null,
    val createdAt: Date,
    val isRead: Boolean = false
)

