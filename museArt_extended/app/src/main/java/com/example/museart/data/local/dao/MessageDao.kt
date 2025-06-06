package com.example.museart.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.museart.data.local.entity.MessageEntity
import com.example.museart.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<MessageEntity>)

    @Update
    suspend fun update(message: MessageEntity)

    @Delete
    suspend fun delete(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getMessageById(id: String): MessageEntity?

    @Query("""
        SELECT * FROM messages 
        WHERE (senderId = :userId1 AND receiverId = :userId2) 
        OR (senderId = :userId2 AND receiverId = :userId1) 
        ORDER BY createdAt DESC
    """)
    fun getMessagesBetweenUsers(userId1: String, userId2: String): Flow<List<MessageEntity>>

    @Query("SELECT u.* FROM users u WHERE u.id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("UPDATE messages SET isRead = 1 WHERE id = :messageId")
    suspend fun markAsRead(messageId: String)

    @Query("UPDATE messages SET isRead = 1 WHERE receiverId = :userId AND senderId = :otherUserId")
    suspend fun markAllAsRead(userId: String, otherUserId: String)

    @Query("SELECT COUNT(*) FROM messages WHERE receiverId = :userId AND senderId = :otherUserId AND isRead = 0")
    suspend fun getUnreadCount(userId: String, otherUserId: String): Int

    @Query("SELECT COUNT(*) FROM messages WHERE receiverId = :userId AND isRead = 0")
    suspend fun getTotalUnreadCount(userId: String): Int
}

