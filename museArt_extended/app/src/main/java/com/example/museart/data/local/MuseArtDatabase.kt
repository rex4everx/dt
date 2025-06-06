package com.example.museart.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.museart.data.local.dao.ChatDao
import com.example.museart.data.local.dao.CommentDao
import com.example.museart.data.local.dao.CommentLikeDao
import com.example.museart.data.local.dao.FollowDao
import com.example.museart.data.local.dao.LikeDao
import com.example.museart.data.local.dao.MessageDao
import com.example.museart.data.local.dao.NotificationDao
import com.example.museart.data.local.dao.PostDao
import com.example.museart.data.local.dao.UserDao
import com.example.museart.data.local.entity.ChatEntity
import com.example.museart.data.local.entity.CommentEntity
import com.example.museart.data.local.entity.CommentLikeEntity
import com.example.museart.data.local.entity.FollowEntity
import com.example.museart.data.local.entity.LikeEntity
import com.example.museart.data.local.entity.MessageEntity
import com.example.museart.data.local.entity.NotificationEntity
import com.example.museart.data.local.entity.PostEntity
import com.example.museart.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        CommentEntity::class,
        NotificationEntity::class,
        LikeEntity::class,
        CommentLikeEntity::class,
        FollowEntity::class,
        MessageEntity::class,
        ChatEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MuseArtDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun notificationDao(): NotificationDao
    abstract fun likeDao(): LikeDao
    abstract fun commentLikeDao(): CommentLikeDao
    abstract fun followDao(): FollowDao
    abstract fun messageDao(): MessageDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: MuseArtDatabase? = null

        fun getDatabase(context: Context): MuseArtDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MuseArtDatabase::class.java,
                    "museart_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

