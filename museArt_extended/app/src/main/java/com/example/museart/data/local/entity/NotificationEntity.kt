package com.example.museart.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.museart.model.Notification
import com.example.museart.model.NotificationType
import java.util.Date

@Entity(
    tableName = "notifications",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["triggerUserId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId"),
        Index("triggerUserId")
    ]
)
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val triggerUserId: String,
    val type: String,
    val postId: String?,
    val commentId: String?,
    val createdAt: Long,
    val isRead: Boolean
) {
    fun toNotification(triggerUser: com.example.museart.model.User? = null): Notification {
        return Notification(
            id = id,
            userId = userId,
            triggerUserId = triggerUserId,
            triggerUser = triggerUser,
            type = NotificationType.valueOf(type),
            postId = postId,
            commentId = commentId,
            createdAt = Date(createdAt),
            isRead = isRead
        )
    }

    companion object {
        fun fromNotification(notification: Notification): NotificationEntity {
            return NotificationEntity(
                id = notification.id,
                userId = notification.userId,
                triggerUserId = notification.triggerUserId,
                type = notification.type.name,
                postId = notification.postId,
                commentId = notification.commentId,
                createdAt = notification.createdAt.time,
                isRead = notification.isRead
            )
        }
    }
}

