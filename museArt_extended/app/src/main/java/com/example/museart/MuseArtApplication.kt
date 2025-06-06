package com.example.museart

import android.app.Application
import com.example.museart.data.local.MuseArtDatabase
import com.example.museart.data.remote.DataSynchronizer
import com.example.museart.data.remote.JsonDataRepository
import com.example.museart.data.repository.CommentRepository
import com.example.museart.data.repository.CommentRepositoryImpl
import com.example.museart.data.repository.MessageRepository
import com.example.museart.data.repository.MessageRepositoryImpl
import com.example.museart.data.repository.NotificationRepository
import com.example.museart.data.repository.NotificationRepositoryImpl
import com.example.museart.data.repository.PostRepository
import com.example.museart.data.repository.PostRepositoryImpl
import com.example.museart.data.repository.UserRepository
import com.example.museart.data.repository.UserRepositoryImpl
import com.example.museart.util.ImageUploader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MuseArtApplication : Application() {
    // База данных
    private val database by lazy { MuseArtDatabase.getDatabase(this) }
    
    // Утилиты
    private val imageUploader by lazy { ImageUploader(this) }
    
    // JSON репозиторий
    private val jsonDataRepository by lazy { JsonDataRepository(this) }
    
    // Синхронизатор данных
    private val dataSynchronizer by lazy { DataSynchronizer(this, database, jsonDataRepository) }
    
    // Репозитории
    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(database.userDao(), database.followDao(), this)
    }
    
    val postRepository: PostRepository by lazy {
        PostRepositoryImpl(database.postDao(), database.userDao(), database.likeDao(), imageUploader, this)
    }
    
    val commentRepository: CommentRepository by lazy {
        CommentRepositoryImpl(database.commentDao(), database.userDao(), database.postDao(), database.commentLikeDao())
    }
    
    val notificationRepository: NotificationRepository by lazy {
        NotificationRepositoryImpl(database.notificationDao(), database.userDao())
    }
    
    val messageRepository: MessageRepository by lazy {
        MessageRepositoryImpl(database.messageDao(), database.chatDao(), database.userDao(), imageUploader)
    }
    
    // Область видимости корутин для приложения
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        
        // Инициализация приложения
        initializeData()
    }
    
    private fun initializeData() {
        applicationScope.launch {
            try {
                // Синхронизация данных из JSON в базу данных при первом запуске
                dataSynchronizer.syncFromJson()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    // Метод для синхронизации данных из базы данных в JSON
    fun syncDataToJson() {
        applicationScope.launch {
            try {
                dataSynchronizer.syncToJson()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

