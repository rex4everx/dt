package com.example.museart.data.repository

import com.example.museart.data.local.dao.NotificationDao
import com.example.museart.data.local.dao.UserDao
import com.example.museart.data.local.entity.NotificationEntity
import com.example.museart.model.Notification
import com.example.museart.model.NotificationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class NotificationRepositoryImpl(
    private val notificationDao: NotificationDao,
    private val userDao: UserDao
) : NotificationRepository {

    override suspend fun createNotification(
        userId: String,
        triggerUserId: String,
        type: NotificationType,
        postId: String?,
        commentId: String?
    ): Result<Notification> {
        return try {
            // Проверяем, существуют ли пользователи
            val user = userDao.getUserById(userId)
                ?: return Result.failure(Exception("Пользователь-получатель не найден"))

            val triggerUser = userDao.getUserById(triggerUserId)
                ?: return Result.failure(Exception("Пользователь-инициатор не найден"))

            // Создаем уведомление
            val notificationId = UUID.randomUUID().toString()
            val notificationEntity = NotificationEntity(
                id = notificationId,
                userId = userId,
                triggerUserId = triggerUserId,
                type = type.name,
                postId = postId,
                commentId = commentId,
                createdAt = System.currentTimeMillis(),
                isRead = false
            )

            notificationDao.insert(notificationEntity)

            val notification = notificationEntity.toNotification(triggerUser = triggerUser.toUser())
            Result.success(notification)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNotificationById(id: String): Result<Notification> {
        return try {
            val notificationEntity = notificationDao.getNotificationById(id)
                ?: return Result.failure(Exception("Уведомление не найдено"))

            val triggerUser = notificationDao.getTriggerUserForNotification(id)?.toUser()
            val notification = notificationEntity.toNotification(triggerUser = triggerUser)

            Result.success(notification)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsRead(id: String): Result<Boolean> {
        return try {
            val notification = notificationDao.getNotificationById(id)
                ?: return Result.failure(Exception("Уведомление не найдено"))

            notificationDao.markAsRead(id)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAllAsRead(userId: String): Result<Boolean> {
        return try {
            notificationDao.markAllAsRead(userId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNotification(id: String): Result<Boolean> {
        return try {
            val notification = notificationDao.getNotificationById(id)
                ?: return Result.failure(Exception("Уведомление не найдено"))

            notificationDao.delete(notification)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUnreadCount(userId: String): Result<Int> {
        return try {
            val count = notificationDao.getUnreadCount(userId)
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getNotificationsByUserId(userId: String): Flow<List<Notification>> {
        return notificationDao.getNotificationsByUserId(userId).map { notificationEntities ->
            notificationEntities.map { notificationEntity ->
                val triggerUser = notificationDao.getTriggerUserForNotification(notificationEntity.id)?.toUser()
                notificationEntity.toNotification(triggerUser = triggerUser)
            }
        }
    }

    override fun getMentionsByUserId(userId: String): Flow<List<Notification>> {
        return notificationDao.getMentionsByUserId(userId).map { notificationEntities ->
            notificationEntities.map { notificationEntity ->
                val triggerUser = notificationDao.getTriggerUserForNotification(notificationEntity.id)?.toUser()
                notificationEntity.toNotification(triggerUser = triggerUser)
            }
        }
    }
}

