package com.example.museart.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.museart.data.local.entity.ChatEntity
import com.example.museart.data.local.entity.MessageEntity
import com.example.museart.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chat: ChatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chats: List<ChatEntity>)

    @Update
    suspend fun update(chat: ChatEntity)

    @Delete
    suspend fun delete(chat: ChatEntity)

    @Query("SELECT * FROM chats WHERE id = :id")
    suspend fun getChatById(id: String): ChatEntity?

    @Query("""
        SELECT * FROM chats 
        WHERE user1Id = :userId OR user2Id = :userId 
        ORDER BY updatedAt DESC
    """)
    fun getChatsByUserId(userId: String): Flow<List<ChatEntity>>

    @Query("""
        SELECT * FROM chats 
        WHERE (user1Id = :userId1 AND user2Id = :userId2) 
        OR (user1Id = :userId2 AND user2Id = :userId1)
    """)
    suspend fun getChatBetweenUsers(userId1: String, userId2: String): ChatEntity?

    @Query("SELECT u.* FROM users u WHERE u.id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): MessageEntity?

    @Query("UPDATE chats SET unreadCount = :unreadCount WHERE id = :chatId")
    suspend fun updateUnreadCount(chatId: String, unreadCount: Int)

    @Query("UPDATE chats SET lastMessageId = :messageId, updatedAt = :updatedAt WHERE id = :chatId")
    suspend fun updateLastMessage(chatId: String, messageId: String, updatedAt: Long)

    @Transaction
    suspend fun incrementUnreadCount(chatId: String) {
        val chat = getChatById(chatId) ?: return
        updateUnreadCount(chatId, chat.unreadCount + 1)
    }

    @Transaction
    suspend fun resetUnreadCount(chatId: String) {
        updateUnreadCount(chatId, 0)
    }
}

