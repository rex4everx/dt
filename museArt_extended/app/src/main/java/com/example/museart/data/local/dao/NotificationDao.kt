package com.example.museart.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.museart.data.local.entity.NotificationEntity
import com.example.museart.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationEntity>)

    @Update
    suspend fun update(notification: NotificationEntity)

    @Delete
    suspend fun delete(notification: NotificationEntity)

    @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun getNotificationById(id: String): NotificationEntity?

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsByUserId(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND type = 'MENTION' ORDER BY createdAt DESC")
    fun getMentionsByUserId(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT u.* FROM users u INNER JOIN notifications n ON u.id = n.triggerUserId WHERE n.id = :notificationId")
    suspend fun getTriggerUserForNotification(notificationId: String): UserEntity?

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    suspend fun getUnreadCount(userId: String): Int
}

