package com.example.museart.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.museart.model.Post
import java.util.Date

@Entity(
    tableName = "posts",
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
            childColumns = ["originalPostId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("userId"),
        Index("originalPostId")
    ]
)
data class PostEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val content: String,
    val imageUrl: String?,
    val createdAt: Long,
    val likesCount: Int,
    val commentsCount: Int,
    val repostsCount: Int,
    val originalPostId: String?
) {
    fun toPost(user: com.example.museart.model.User? = null, originalPost: Post? = null): Post {
        return Post(
            id = id,
            userId = userId,
            user = user,
            content = content,
            imageUrl = imageUrl,
            createdAt = Date(createdAt),
            likesCount = likesCount,
            commentsCount = commentsCount,
            repostsCount = repostsCount,
            originalPost = originalPost
        )
    }

    companion object {
        fun fromPost(post: Post): PostEntity {
            return PostEntity(
                id = post.id,
                userId = post.userId,
                content = post.content,
                imageUrl = post.imageUrl,
                createdAt = post.createdAt.time,
                likesCount = post.likesCount,
                commentsCount = post.commentsCount,
                repostsCount = post.repostsCount,
                originalPostId = post.originalPost?.id
            )
        }
    }
}

