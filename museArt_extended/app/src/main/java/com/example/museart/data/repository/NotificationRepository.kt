package com.example.museart.data.repository

import com.example.museart.model.Notification
import com.example.museart.model.NotificationType
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun createNotification(
        userId: String,
        triggerUserId: String,
        type: NotificationType,
        postId: String? = null,
        commentId: String? = null
    ): Result<Notification>
    
    suspend fun getNotificationById(id: String): Result<Notification>
    suspend fun markAsRead(id: String): Result<Boolean>
    suspend fun markAllAsRead(userId: String): Result<Boolean>
    suspend fun deleteNotification(id: String): Result<Boolean>
    suspend fun getUnreadCount(userId: String): Result<Int>
    fun getNotificationsByUserId(userId: String): Flow<List<Notification>>
    fun getMentionsByUserId(userId: String): Flow<List<Notification>>
}

