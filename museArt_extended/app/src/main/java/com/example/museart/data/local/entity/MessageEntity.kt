package com.example.museart.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.museart.model.Message
import java.util.Date

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["senderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["receiverId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("senderId"),
        Index("receiverId")
    ]
)
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val imageUrl: String?,
    val createdAt: Long,
    val isRead: Boolean
) {
    fun toMessage(sender: com.example.museart.model.User? = null, receiver: com.example.museart.model.User? = null): Message {
        return Message(
            id = id,
            senderId = senderId,
            receiverId = receiverId,
            sender = sender,
            receiver = receiver,
            content = content,
            imageUrl = imageUrl,
            createdAt = Date(createdAt),
            isRead = isRead
        )
    }

    companion object {
        fun fromMessage(message: Message): MessageEntity {
            return MessageEntity(
                id = message.id,
                senderId = message.senderId,
                receiverId = message.receiverId,
                content = message.content,
                imageUrl = message.imageUrl,
                createdAt = message.createdAt.time,
                isRead = message.isRead
            )
        }
    }
}

