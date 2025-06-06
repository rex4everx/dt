package com.example.museart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.museart.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val displayName: String,
    val bio: String,
    val profileImageUrl: String,
    val followersCount: Int,
    val followingCount: Int,
    val postsCount: Int,
    val isVerified: Boolean,
    val email: String,
    val password: String, // В реальном приложении пароль должен храниться в зашифрованном виде
    val createdAt: Long
) {
    fun toUser(): User {
        return User(
            id = id,
            username = username,
            displayName = displayName,
            bio = bio,
            profileImageUrl = profileImageUrl,
            followersCount = followersCount,
            followingCount = followingCount,
            postsCount = postsCount,
            isVerified = isVerified
        )
    }

    companion object {
        fun fromUser(user: User, email: String = "", password: String = ""): UserEntity {
            return UserEntity(
                id = user.id,
                username = user.username,
                displayName = user.displayName,
                bio = user.bio,
                profileImageUrl = user.profileImageUrl,
                followersCount = user.followersCount,
                followingCount = user.followingCount,
                postsCount = user.postsCount,
                isVerified = user.isVerified,
                email = email,
                password = password,
                createdAt = System.currentTimeMillis()
            )
        }
    }
}

