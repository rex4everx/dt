package com.example.museart.data.repository

import com.example.museart.data.local.dao.ChatDao
import com.example.museart.data.local.dao.MessageDao
import com.example.museart.data.local.dao.UserDao
import com.example.museart.data.local.entity.ChatEntity
import com.example.museart.data.local.entity.MessageEntity
import com.example.museart.model.Chat
import com.example.museart.model.Message
import com.example.museart.util.ImageUploader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.util.UUID

class MessageRepositoryImpl(
    private val messageDao: MessageDao,
    private val chatDao: ChatDao,
    private val userDao: UserDao,
    private val imageUploader: ImageUploader
) : MessageRepository {

    override suspend fun sendMessage(
        senderId: String,
        receiverId: String,
        content: String,
        imageFile: File?
    ): Result<Message> {
        return try {
            // Проверяем, существуют ли пользователи
            val sender = userDao.getUserById(senderId)
                ?: return Result.failure(Exception("Отправитель не найден"))

            val receiver = userDao.getUserById(receiverId)
                ?: return Result.failure(Exception("Получатель не найден"))

            // Загружаем изображение, если оно есть
            var imageUrl: String? = null
            if (imageFile != null) {
                imageUrl = imageUploader.uploadImage(imageFile)
            }

            // Создаем сообщение
            val messageId = UUID.randomUUID().toString()
            val messageEntity = MessageEntity(
                id = messageId,
                senderId = senderId,
                receiverId = receiverId,
                content = content,
                imageUrl = imageUrl,
                createdAt = System.currentTimeMillis(),
                isRead = false
            )

            messageDao.insert(messageEntity)

            // Создаем или обновляем чат
            val chat = createOrGetChat(senderId, receiverId).getOrNull()
            if (chat != null) {
                chatDao.updateLastMessage(chat.id, messageId, System.currentTimeMillis())
                if (chat.user1Id == receiverId || chat.user2Id == receiverId) {
                    chatDao.incrementUnreadCount(chat.id)
                }
            }

            val message = messageEntity.toMessage(
                sender = sender.toUser(),
                receiver = receiver.toUser()
            )
            
            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMessageById(id: String): Result<Message> {
        return try {
            val messageEntity = messageDao.getMessageById(id)
                ?: return Result.failure(Exception("Сообщение не найдено"))

            val sender = messageDao.getUserById(messageEntity.senderId)?.toUser()
            val receiver = messageDao.getUserById(messageEntity.receiverId)?.toUser()
            
            val message = messageEntity.toMessage(sender = sender, receiver = receiver)
            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsRead(id: String): Result<Boolean> {
        return try {
            val message = messageDao.getMessageById(id)
                ?: return Result.failure(Exception("Сообщение не найдено"))

            messageDao.markAsRead(id)
            
            // Обновляем счетчик непрочитанных сообщений в чате
            val chat = chatDao.getChatBetweenUsers(message.senderId, message.receiverId)
            if (chat != null) {
                val unreadCount = messageDao.getUnreadCount(message.receiverId, message.senderId)
                chatDao.updateUnreadCount(chat.id, unreadCount)
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAllAsRead(userId: String, otherUserId: String): Result<Boolean> {
        return try {
            messageDao.markAllAsRead(userId, otherUserId)
            
            // Обновляем счетчик непрочитанных сообщений в чате
            val chat = chatDao.getChatBetweenUsers(userId, otherUserId)
            if (chat != null) {
                chatDao.resetUnreadCount(chat.id)
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMessage(id: String): Result<Boolean> {
        return try {
            val message = messageDao.getMessageById(id)
                ?: return Result.failure(Exception("Сообщение не найдено"))

            // Если у сообщения было изображение, удаляем его
            if (message.imageUrl != null) {
                imageUploader.deleteImage(message.imageUrl)
            }

            messageDao.delete(message)
            
            // Обновляем последнее сообщение в чате, если удаленное сообщение было последним
            val chat = chatDao.getChatBetweenUsers(message.senderId, message.receiverId)
            if (chat != null && chat.lastMessageId == id) {
                // Находим предыдущее сообщение
                val messages = messageDao.getMessagesBetweenUsers(message.senderId, message.receiverId)
                    .map { it.firstOrNull() }
                    .firstOrNull()
                
                if (messages != null) {
                    chatDao.updateLastMessage(chat.id, messages.id, messages.createdAt)
                }
            }
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUnreadCount(userId: String, otherUserId: String): Result<Int> {
        return try {
            val count = messageDao.getUnreadCount(userId, otherUserId)
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTotalUnreadCount(userId: String): Result<Int> {
        return try {
            val count = messageDao.getTotalUnreadCount(userId)
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createOrGetChat(userId1: String, userId2: String): Result<Chat> {
        return try {
            // Проверяем, существуют ли пользователи
            val user1 = userDao.getUserById(userId1)
                ?: return Result.failure(Exception("Пользователь 1 не найден"))

            val user2 = userDao.getUserById(userId2)
                ?: return Result.failure(Exception("Пользователь 2 не найден"))

            // Проверяем, существует ли уже чат между пользователями
            var chatEntity = chatDao.getChatBetweenUsers(userId1, userId2)
            
            if (chatEntity == null) {
                // Создаем новый чат
                val chatId = UUID.randomUUID().toString()
                chatEntity = ChatEntity(
                    id = chatId,
                    user1Id = userId1,
                    user2Id = userId2,
                    lastMessageId = null,
                    unreadCount = 0,
                    updatedAt = System.currentTimeMillis()
                )
                
                chatDao.insert(chatEntity)
            }

            // Получаем последнее сообщение, если оно есть
            val lastMessage = if (chatEntity.lastMessageId != null) {
                val messageEntity = chatDao.getMessageById(chatEntity.lastMessageId)
                if (messageEntity != null) {
                    val sender = messageDao.getUserById(messageEntity.senderId)?.toUser()
                    val receiver = messageDao.getUserById(messageEntity.receiverId)?.toUser()
                    messageEntity.toMessage(sender = sender, receiver = receiver)
                } else {
                    null
                }
            } else {
                null
            }

            val chat = chatEntity.toChat(
                user1 = user1.toUser(),
                user2 = user2.toUser(),
                lastMessage = lastMessage
            )
            
            Result.success(chat)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getChatById(id: String): Result<Chat> {
        return try {
            val chatEntity = chatDao.getChatById(id)
                ?: return Result.failure(Exception("Чат не найден"))

            val user1 = chatDao.getUserById(chatEntity.user1Id)?.toUser()
            val user2 = chatDao.getUserById(chatEntity.user2Id)?.toUser()
            
            val lastMessage = if (chatEntity.lastMessageId != null) {
                val messageEntity = chatDao.getMessageById(chatEntity.lastMessageId)
                if (messageEntity != null) {
                    val sender = messageDao.getUserById(messageEntity.senderId)?.toUser()
                    val receiver = messageDao.getUserById(messageEntity.receiverId)?.toUser()
                    messageEntity.toMessage(sender = sender, receiver = receiver)
                } else {
                    null
                }
            } else {
                null
            }

            val chat = chatEntity.toChat(
                user1 = user1,
                user2 = user2,
                lastMessage = lastMessage
            )
            
            Result.success(chat)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteChatById(id: String): Result<Boolean> {
        return try {
            val chat = chatDao.getChatById(id)
                ?: return Result.failure(Exception("Чат не найден"))

            chatDao.delete(chat)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMessagesBetweenUsers(userId1: String, userId2: String): Flow<List<Message>> {
        return messageDao.getMessagesBetweenUsers(userId1, userId2).map { messageEntities ->
            messageEntities.map { messageEntity ->
                val sender = messageDao.getUserById(messageEntity.senderId)?.toUser()
                val receiver = messageDao.getUserById(messageEntity.receiverId)?.toUser()
                messageEntity.toMessage(sender = sender, receiver = receiver)
            }
        }
    }

    override fun getChatsByUserId(userId: String): Flow<List<Chat>> {
        return chatDao.getChatsByUserId(userId).map { chatEntities ->
            chatEntities.map { chatEntity ->
                val user1 = chatDao.getUserById(chatEntity.user1Id)?.toUser()
                val user2 = chatDao.getUserById(chatEntity.user2Id)?.toUser()
                
                val lastMessage = if (chatEntity.lastMessageId != null) {
                    val messageEntity = chatDao.getMessageById(chatEntity.lastMessageId)
                    if (messageEntity != null) {
                        val sender = messageDao.getUserById(messageEntity.senderId)?.toUser()
                        val receiver = messageDao.getUserById(messageEntity.receiverId)?.toUser()
                        messageEntity.toMessage(sender = sender, receiver = receiver)
                    } else {
                        null
                    }
                } else {
                    null
                }

                chatEntity.toChat(
                    user1 = user1,
                    user2 = user2,
                    lastMessage = lastMessage
                )
            }
        }
    }
}

