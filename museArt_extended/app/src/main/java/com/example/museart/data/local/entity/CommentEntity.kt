package com.example.museart.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.museart.model.Comment
import java.util.Date

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId"),
        Index("postId")
    ]
)
data class CommentEntity(
    @PrimaryKey
    val id: String,
    val postId: String,
    val userId: String,
    val content: String,
    val createdAt: Long,
    val likesCount: Int
) {
    fun toComment(user: com.example.museart.model.User? = null): Comment {
        return Comment(
            id = id,
            postId = postId,
            userId = userId,
            user = user,
            content = content,
            createdAt = Date(createdAt),
            likesCount = likesCount
        )
    }

    companion object {
        fun fromComment(comment: Comment): CommentEntity {
            return CommentEntity(
                id = comment.id,
                postId = comment.postId,
                userId = comment.userId,
                content = comment.content,
                createdAt = comment.createdAt.time,
                likesCount = comment.likesCount
            )
        }
    }
}

