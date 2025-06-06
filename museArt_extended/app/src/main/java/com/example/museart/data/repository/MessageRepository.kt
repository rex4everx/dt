package com.example.museart.data.repository

import com.example.museart.model.Chat
import com.example.museart.model.Message
import kotlinx.coroutines.flow.Flow
import java.io.File

interface MessageRepository {
    suspend fun sendMessage(
        senderId: String,
        receiverId: String,
        content: String,
        imageFile: File? = null
    ): Result<Message>
    
    suspend fun getMessageById(id: String): Result<Message>
    suspend fun markAsRead(id: String): Result<Boolean>
    suspend fun markAllAsRead(userId: String, otherUserId: String): Result<Boolean>
    suspend fun deleteMessage(id: String): Result<Boolean>
    suspend fun getUnreadCount(userId: String, otherUserId: String): Result<Int>
    suspend fun getTotalUnreadCount(userId: String): Result<Int>
    suspend fun createOrGetChat(userId1: String, userId2: String): Result<Chat>
    suspend fun getChatById(id: String): Result<Chat>
    suspend fun deleteChatById(id: String): Result<Boolean>
    fun getMessagesBetweenUsers(userId1: String, userId2: String): Flow<List<Message>>
    fun getChatsByUserId(userId: String): Flow<List<Chat>>
}

