package com.example.museart.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "follows",
    primaryKeys = ["followerId", "followingId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["followerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["followingId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("followerId"),
        Index("followingId")
    ]
)
data class FollowEntity(
    val followerId: String, // ID пользователя, который подписывается
    val followingId: String, // ID пользователя, на которого подписываются
    val createdAt: Long
)

