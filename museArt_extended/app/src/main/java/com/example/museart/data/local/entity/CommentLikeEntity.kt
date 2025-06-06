package com.example.museart.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "comment_likes",
    primaryKeys = ["userId", "commentId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CommentEntity::class,
            parentColumns = ["id"],
            childColumns = ["commentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId"),
        Index("commentId")
    ]
)
data class CommentLikeEntity(
    val userId: String,
    val commentId: String,
    val createdAt: Long
)

