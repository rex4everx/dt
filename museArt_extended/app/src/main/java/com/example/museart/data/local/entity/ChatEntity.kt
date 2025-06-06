package com.example.museart.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.museart.model.Chat
import java.util.Date

@Entity(
    tableName = "chats",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user1Id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user2Id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["lastMessageId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("user1Id"),
        Index("user2Id"),
        Index("lastMessageId")
    ]
)
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val user1Id: String,
    val user2Id: String,
    val lastMessageId: String?,
    val unreadCount: Int,
    val updatedAt: Long
) {
    fun toChat(
        user1: com.example.museart.model.User? = null,
        user2: com.example.museart.model.User? = null,
        lastMessage: com.example.museart.model.Message? = null
    ): Chat {
        return Chat(
            id = id,
            user1Id = user1Id,
            user2Id = user2Id,
            user1 = user1,
            user2 = user2,
            lastMessage = lastMessage,
            unreadCount = unreadCount,
            updatedAt = Date(updatedAt)
        )
    }

    companion object {
        fun fromChat(chat: Chat): ChatEntity {
            return ChatEntity(
                id = chat.id,
                user1Id = chat.user1Id,
                user2Id = chat.user2Id,
                lastMessageId = chat.lastMessage?.id,
                unreadCount = chat.unreadCount,
                updatedAt = chat.updatedAt.time
            )
        }
    }
}

