package com.example.museart.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "likes",
    primaryKeys = ["userId", "postId"],
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
data class LikeEntity(
    val userId: String,
    val postId: String,
    val createdAt: Long
)

