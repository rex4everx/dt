package com.example.museart.data.remote

import android.content.Context
import android.util.Log
import com.example.museart.data.local.MuseArtDatabase
import com.example.museart.data.local.entity.ChatEntity
import com.example.museart.data.local.entity.CommentEntity
import com.example.museart.data.local.entity.CommentLikeEntity
import com.example.museart.data.local.entity.FollowEntity
import com.example.museart.data.local.entity.LikeEntity
import com.example.museart.data.local.entity.MessageEntity
import com.example.museart.data.local.entity.NotificationEntity
import com.example.museart.data.local.entity.PostEntity
import com.example.museart.data.local.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Класс для синхронизации данных между локальной базой данных Room и JSON-файлами.
 */
class DataSynchronizer(
    private val context: Context,
    private val database: MuseArtDatabase,
    private val jsonDataRepository: JsonDataRepository
) {
    private val TAG = "DataSynchronizer"

    /**
     * Синхронизирует данные из локальной базы данных в JSON-файлы.
     */
    suspend fun syncToJson() = withContext(Dispatchers.IO) {
        try {
            // Синхронизация пользователей
            val users = database.userDao().getAllUsers().first().map { it.toUser() }
            jsonDataRepository.syncUsersToJson(users)

            // Синхронизация постов
            val posts = database.postDao().getAllPosts().first().map { postEntity ->
                val user = database.userDao().getUserById(postEntity.userId)?.toUser()
                var originalPost = null
                if (postEntity.originalPostId != null) {
                    val originalPostEntity = database.postDao().getPostById(postEntity.originalPostId)
                    if (originalPostEntity != null) {
                        val originalPostUser = database.userDao().getUserById(originalPostEntity.userId)?.toUser()
                        originalPost = originalPostEntity.toPost(user = originalPostUser)
                    }
                }
                postEntity.toPost(user = user, originalPost = originalPost)
            }
            jsonDataRepository.syncPostsToJson(posts)

            // Синхронизация комментариев
            val comments = database.commentDao().getCommentsByPostId("all").first().map { commentEntity ->
                val user = database.userDao().getUserById(commentEntity.userId)?.toUser()
                commentEntity.toComment(user = user)
            }
            jsonDataRepository.syncCommentsToJson(comments)

            // Синхронизация уведомлений
            val notifications = database.notificationDao().getNotificationsByUserId("all").first().map { notificationEntity ->
                val triggerUser = database.userDao().getUserById(notificationEntity.triggerUserId)?.toUser()
                notificationEntity.toNotification(triggerUser = triggerUser)
            }
            jsonDataRepository.syncNotificationsToJson(notifications)

            // Синхронизация сообщений
            val messages = database.messageDao().getMessagesBetweenUsers("all", "all").first().map { messageEntity ->
                val sender = database.userDao().getUserById(messageEntity.senderId)?.toUser()
                val receiver = database.userDao().getUserById(messageEntity.receiverId)?.toUser()
                messageEntity.toMessage(sender = sender, receiver = receiver)
            }
            jsonDataRepository.syncMessagesToJson(messages)

            // Синхронизация чатов
            val chats = database.chatDao().getChatsByUserId("all").first().map { chatEntity ->
                val user1 = database.userDao().getUserById(chatEntity.user1Id)?.toUser()
                val user2 = database.userDao().getUserById(chatEntity.user2Id)?.toUser()
                
                val lastMessage = if (chatEntity.lastMessageId != null) {
                    val messageEntity = database.messageDao().getMessageById(chatEntity.lastMessageId)
                    if (messageEntity != null) {
                        val sender = database.userDao().getUserById(messageEntity.senderId)?.toUser()
                        val receiver = database.userDao().getUserById(messageEntity.receiverId)?.toUser()
                        messageEntity.toMessage(sender = sender, receiver = receiver)
                    } else {
                        null
                    }
                } else {
                    null
                }

                chatEntity.toChat(user1 = user1, user2 = user2, lastMessage = lastMessage)
            }
            jsonDataRepository.syncChatsToJson(chats)

            Log.d(TAG, "Синхронизация с JSON успешно завершена")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при синхронизации с JSON", e)
        }
    }

    /**
     * Синхронизирует данные из JSON-файлов в локальную базу данных.
     */
    suspend fun syncFromJson() = withContext(Dispatchers.IO) {
        try {
            // Инициализация JSON-файлов, если они не существуют
            jsonDataRepository.initializeJsonFiles()

            // Синхронизация пользователей
            val users = jsonDataRepository.getUsers()
            users.forEach { user ->
                val userEntity = UserEntity.fromUser(
                    user = user,
                    email = "${user.username}@example.com", // Временное решение для демо
                    password = "password123" // Временное решение для демо
                )
                database.userDao().insert(userEntity)
            }

            // Синхронизация постов
            val posts = jsonDataRepository.getPosts()
            posts.forEach { post ->
                val postEntity = PostEntity.fromPost(post)
                database.postDao().insert(postEntity)
            }

            // Синхронизация комментариев
            val comments = jsonDataRepository.getComments()
            comments.forEach { comment ->
                val commentEntity = CommentEntity.fromComment(comment)
                database.commentDao().insert(commentEntity)
            }

            // Синхронизация уведомлений
            val notifications = jsonDataRepository.getNotifications()
            notifications.forEach { notification ->
                val notificationEntity = NotificationEntity.fromNotification(notification)
                database.notificationDao().insert(notificationEntity)
            }

            // Синхронизация сообщений
            val messages = jsonDataRepository.getMessages()
            messages.forEach { message ->
                val messageEntity = MessageEntity.fromMessage(message)
                database.messageDao().insert(messageEntity)
            }

            // Синхронизация чатов
            val chats = jsonDataRepository.getChats()
            chats.forEach { chat ->
                val chatEntity = ChatEntity.fromChat(chat)
                database.chatDao().insert(chatEntity)
            }

            // Синхронизация лайков
            val likes = jsonDataRepository.getLikes()
            likes.forEach { like ->
                val likeEntity = LikeEntity(
                    userId = like.userId,
                    postId = like.postId,
                    createdAt = System.currentTimeMillis()
                )
                database.likeDao().insert(likeEntity)
            }

            // Синхронизация подписок
            val follows = jsonDataRepository.getFollows()
            follows.forEach { follow ->
                val followEntity = FollowEntity(
                    followerId = follow.followerId,
                    followingId = follow.followingId,
                    createdAt = System.currentTimeMillis()
                )
                database.followDao().insert(followEntity)
            }

            Log.d(TAG, "Синхронизация из JSON успешно завершена")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при синхронизации из JSON", e)
        }
    }
}

