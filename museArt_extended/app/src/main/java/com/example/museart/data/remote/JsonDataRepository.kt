package com.example.museart.data.remote

import android.content.Context
import com.example.museart.model.Chat
import com.example.museart.model.Comment
import com.example.museart.model.Message
import com.example.museart.model.Notification
import com.example.museart.model.Post
import com.example.museart.model.User
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

/**
 * Репозиторий для работы с данными в формате JSON.
 * Имитирует работу с бэкендом через локальные JSON-файлы.
 */
class JsonDataRepository(private val context: Context) {
    
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val dataDir: File by lazy {
        File(context.filesDir, "json_data").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    // Файлы для хранения данных
    private val usersFile: File by lazy { File(dataDir, "users.json") }
    private val postsFile: File by lazy { File(dataDir, "posts.json") }
    private val commentsFile: File by lazy { File(dataDir, "comments.json") }
    private val notificationsFile: File by lazy { File(dataDir, "notifications.json") }
    private val messagesFile: File by lazy { File(dataDir, "messages.json") }
    private val chatsFile: File by lazy { File(dataDir, "chats.json") }
    private val likesFile: File by lazy { File(dataDir, "likes.json") }
    private val followsFile: File by lazy { File(dataDir, "follows.json") }

    // Структуры для хранения связей между объектами
    data class Like(val userId: String, val postId: String)
    data class CommentLike(val userId: String, val commentId: String)
    data class Follow(val followerId: String, val followingId: String)

    /**
     * Инициализирует файлы JSON, если они не существуют.
     */
    suspend fun initializeJsonFiles() = withContext(Dispatchers.IO) {
        if (!usersFile.exists()) saveUsers(emptyList())
        if (!postsFile.exists()) savePosts(emptyList())
        if (!commentsFile.exists()) saveComments(emptyList())
        if (!notificationsFile.exists()) saveNotifications(emptyList())
        if (!messagesFile.exists()) saveMessages(emptyList())
        if (!chatsFile.exists()) saveChats(emptyList())
        if (!likesFile.exists()) saveLikes(emptyList())
        if (!followsFile.exists()) saveFollows(emptyList())
    }

    // Методы для работы с пользователями
    suspend fun getUsers(): List<User> = withContext(Dispatchers.IO) {
        if (!usersFile.exists()) return@withContext emptyList()
        try {
            val type = object : TypeToken<List<User>>() {}.type
            FileReader(usersFile).use { reader ->
                gson.fromJson(reader, type) ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveUsers(users: List<User>) = withContext(Dispatchers.IO) {
        try {
            FileWriter(usersFile).use { writer ->
                gson.toJson(users, writer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    suspend fun getUserById(id: String): User? = withContext(Dispatchers.IO) {
        getUsers().find { it.id == id }
    }

    suspend fun addUser(user: User) = withContext(Dispatchers.IO) {
        val users = getUsers().toMutableList()
        users.add(user)
        saveUsers(users)
    }

    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        val users = getUsers().toMutableList()
        val index = users.indexOfFirst { it.id == user.id }
        if (index != -1) {
            users[index] = user
            saveUsers(users)
            true
        } else {
            false
        }
    }

    // Методы для работы с постами
    suspend fun getPosts(): List<Post> = withContext(Dispatchers.IO) {
        if (!postsFile.exists()) return@withContext emptyList()
        try {
            val type = object : TypeToken<List<Post>>() {}.type
            FileReader(postsFile).use { reader ->
                gson.fromJson(reader, type) ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun savePosts(posts: List<Post>) = withContext(Dispatchers.IO) {
        try {
            FileWriter(postsFile).use { writer ->
                gson.toJson(posts, writer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    suspend fun getPostById(id: String): Post? = withContext(Dispatchers.IO) {
        getPosts().find { it.id == id }
    }

    suspend fun addPost(post: Post) = withContext(Dispatchers.IO) {
        val posts = getPosts().toMutableList()
        posts.add(post)
        savePosts(posts)
    }

    suspend fun updatePost(post: Post) = withContext(Dispatchers.IO) {
        val posts = getPosts().toMutableList()
        val index = posts.indexOfFirst { it.id == post.id }
        if (index != -1) {
            posts[index] = post
            savePosts(posts)
            true
        } else {
            false
        }
    }

    suspend fun deletePost(id: String) = withContext(Dispatchers.IO) {
        val posts = getPosts().toMutableList()
        val removed = posts.removeIf { it.id == id }
        if (removed) {
            savePosts(posts)
        }
        removed
    }

    // Методы для работы с комментариями
    suspend fun getComments(): List<Comment> = withContext(Dispatchers.IO) {
        if (!commentsFile.exists()) return@withContext emptyList()
        try {
            val type = object : TypeToken<List<Comment>>() {}.type
            FileReader(commentsFile).use { reader ->
                gson.fromJson(reader, type) ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveComments(comments: List<Comment>) = withContext(Dispatchers.IO) {
        try {
            FileWriter(commentsFile).use { writer ->
                gson.toJson(comments, writer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    suspend fun getCommentById(id: String): Comment? = withContext(Dispatchers.IO) {
        getComments().find { it.id == id }
    }

    suspend fun addComment(comment: Comment) = withContext(Dispatchers.IO) {
        val comments = getComments().toMutableList()
        comments.add(comment)
        saveComments(comments)
    }

    suspend fun updateComment(comment: Comment) = withContext(Dispatchers.IO) {
        val comments = getComments().toMutableList()
        val index = comments.indexOfFirst { it.id == comment.id }
        if (index != -1) {
            comments[index] = comment
            saveComments(comments)
            true
        } else {
            false
        }
    }

    suspend fun deleteComment(id: String) = withContext(Dispatchers.IO) {
        val comments = getComments().toMutableList()
        val removed = comments.removeIf { it.id == id }
        if (removed) {
            saveComments(comments)
        }
        removed
    }

    // Методы для работы с уведомлениями
    suspend fun getNotifications(): List<Notification> = withContext(Dispatchers.IO) {
        if (!notificationsFile.exists()) return@withContext emptyList()
        try {
            val type = object : TypeToken<List<Notification>>() {}.type
            FileReader(notificationsFile).use { reader ->
                gson.fromJson(reader, type) ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveNotifications(notifications: List<Notification>) = withContext(Dispatchers.IO) {
        try {
            FileWriter(notificationsFile).use { writer ->
                gson.toJson(notifications, writer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    suspend fun getNotificationById(id: String): Notification? = withContext(Dispatchers.IO) {
        getNotifications().find { it.id == id }
    }

    suspend fun addNotification(notification: Notification) = withContext(Dispatchers.IO) {
        val notifications = getNotifications().toMutableList()
        notifications.add(notification)
        saveNotifications(notifications)
    }

    suspend fun updateNotification(notification: Notification) = withContext(Dispatchers.IO) {
        val notifications = getNotifications().toMutableList()
        val index = notifications.indexOfFirst { it.id == notification.id }
        if (index != -1) {
            notifications[index] = notification
            saveNotifications(notifications)
            true
        } else {
            false
        }
    }

    suspend fun deleteNotification(id: String) = withContext(Dispatchers.IO) {
        val notifications = getNotifications().toMutableList()
        val removed = notifications.removeIf { it.id == id }
        if (removed) {
            saveNotifications(notifications)
        }
        removed
    }

    // Методы для работы с сообщениями
    suspend fun getMessages(): List<Message> = withContext(Dispatchers.IO) {
        if (!messagesFile.exists()) return@withContext emptyList()
        try {
            val type = object : TypeToken<List<Message>>() {}.type
            FileReader(messagesFile).use { reader ->
                gson.fromJson(reader, type) ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveMessages(messages: List<Message>) = withContext(Dispatchers.IO) {
        try {
            FileWriter(messagesFile).use { writer ->
                gson.toJson(messages, writer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    suspend fun getMessageById(id: String): Message? = withContext(Dispatchers.IO) {
        getMessages().find { it.id == id }
    }

    suspend fun addMessage(message: Message) = withContext(Dispatchers.IO) {
        val messages = getMessages().toMutableList()
        messages.add(message)
        saveMessages(messages)
    }

    suspend fun updateMessage(message: Message) = withContext(Dispatchers.IO) {
        val messages = getMessages().toMutableList()
        val index = messages.indexOfFirst { it.id == message.id }
        if (index != -1) {
            messages[index] = message
            saveMessages(messages)
            true
        } else {
            false
        }
    }

    suspend fun deleteMessage(id: String) = withContext(Dispatchers.IO) {
        val messages = getMessages().toMutableList()
        val removed = messages.removeIf { it.id == id }
        if (removed) {
            saveMessages(messages)
        }
        removed
    }

    // Методы для работы с чатами
    suspend fun getChats(): List<Chat> = withContext(Dispatchers.IO) {
        if (!chatsFile.exists()) return@withContext emptyList()
        try {
            val type = object : TypeToken<List<Chat>>() {}.type
            FileReader(chatsFile).use { reader ->
                gson.fromJson(reader, type) ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveChats(chats: List<Chat>) = withContext(Dispatchers.IO) {
        try {
            FileWriter(chatsFile).use { writer ->
                gson.toJson(chats, writer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    suspend fun getChatById(id: String): Chat? = withContext(Dispatchers.IO) {
        getChats().find { it.id == id }
    }

    suspend fun addChat(chat: Chat) = withContext(Dispatchers.IO) {
        val chats = getChats().toMutableList()
        chats.add(chat)
        saveChats(chats)
    }

    suspend fun updateChat(chat: Chat) = withContext(Dispatchers.IO) {
        val chats = getChats().toMutableList()
        val index = chats.indexOfFirst { it.id == chat.id }
        if (index != -1) {
            chats[index] = chat
            saveChats(chats)
            true
        } else {
            false
        }
    }

    suspend fun deleteChat(id: String) = withContext(Dispatchers.IO) {
        val chats = getChats().toMutableList()
        val removed = chats.removeIf { it.id == id }
        if (removed) {
            saveChats(chats)
        }
        removed
    }

    // Методы для работы с лайками
    suspend fun getLikes(): List<Like> = withContext(Dispatchers.IO) {
        if (!likesFile.exists()) return@withContext emptyList()
        try {
            val type = object : TypeToken<List<Like>>() {}.type
            FileReader(likesFile).use { reader ->
                gson.fromJson(reader, type) ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveLikes(likes: List<Like>) = withContext(Dispatchers.IO) {
        try {
            FileWriter(likesFile).use { writer ->
                gson.toJson(likes, writer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    suspend fun addLike(userId: String, postId: String) = withContext(Dispatchers.IO) {
        val likes = getLikes().toMutableList()
        if (likes.none { it.userId == userId && it.postId == postId }) {
            likes.add(Like(userId, postId))
            saveLikes(likes)
            true
        } else {
            false
        }
    }

    suspend fun removeLike(userId: String, postId: String) = withContext(Dispatchers.IO) {
        val likes = getLikes().toMutableList()
        val removed = likes.removeIf { it.userId == userId && it.postId == postId }
        if (removed) {
            saveLikes(likes)
        }
        removed
    }

    suspend fun isPostLiked(userId: String, postId: String): Boolean = withContext(Dispatchers.IO) {
        getLikes().any { it.userId == userId && it.postId == postId }
    }

    // Методы для работы с подписками
    suspend fun getFollows(): List<Follow> = withContext(Dispatchers.IO) {
        if (!followsFile.exists()) return@withContext emptyList()
        try {
            val type = object : TypeToken<List<Follow>>() {}.type
            FileReader(followsFile).use { reader ->
                gson.fromJson(reader, type) ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveFollows(follows: List<Follow>) = withContext(Dispatchers.IO) {
        try {
            FileWriter(followsFile).use { writer ->
                gson.toJson(follows, writer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    suspend fun addFollow(followerId: String, followingId: String) = withContext(Dispatchers.IO) {
        val follows = getFollows().toMutableList()
        if (follows.none { it.followerId == followerId && it.followingId == followingId }) {
            follows.add(Follow(followerId, followingId))
            saveFollows(follows)
            true
        } else {
            false
        }
    }

    suspend fun removeFollow(followerId: String, followingId: String) = withContext(Dispatchers.IO) {
        val follows = getFollows().toMutableList()
        val removed = follows.removeIf { it.followerId == followerId && it.followingId == followingId }
        if (removed) {
            saveFollows(follows)
        }
        removed
    }

    suspend fun isFollowing(followerId: String, followingId: String): Boolean = withContext(Dispatchers.IO) {
        getFollows().any { it.followerId == followerId && it.followingId == followingId }
    }

    // Методы для синхронизации данных между Room и JSON
    suspend fun syncUsersToJson(users: List<User>) = saveUsers(users)
    suspend fun syncPostsToJson(posts: List<Post>) = savePosts(posts)
    suspend fun syncCommentsToJson(comments: List<Comment>) = saveComments(comments)
    suspend fun syncNotificationsToJson(notifications: List<Notification>) = saveNotifications(notifications)
    suspend fun syncMessagesToJson(messages: List<Message>) = saveMessages(messages)
    suspend fun syncChatsToJson(chats: List<Chat>) = saveChats(chats)
}

