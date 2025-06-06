package com.example.museart.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.museart.data.local.dao.FollowDao
import com.example.museart.data.local.dao.UserDao
import com.example.museart.data.local.entity.FollowEntity
import com.example.museart.data.local.entity.UserEntity
import com.example.museart.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val followDao: FollowDao,
    context: Context
) : UserRepository {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "museart_prefs", Context.MODE_PRIVATE
    )

    override suspend fun registerUser(
        username: String,
        email: String,
        password: String,
        displayName: String
    ): Result<User> {
        return try {
            // Проверяем, существует ли пользователь с таким email или username
            val existingUserByEmail = userDao.getUserByEmail(email)
            if (existingUserByEmail != null) {
                return Result.failure(Exception("Пользователь с таким email уже существует"))
            }

            val existingUserByUsername = userDao.getUserByUsername(username)
            if (existingUserByUsername != null) {
                return Result.failure(Exception("Пользователь с таким username уже существует"))
            }

            // Создаем нового пользователя
            val userId = UUID.randomUUID().toString()
            val userEntity = UserEntity(
                id = userId,
                username = username,
                displayName = displayName,
                bio = "",
                profileImageUrl = "",
                followersCount = 0,
                followingCount = 0,
                postsCount = 0,
                isVerified = false,
                email = email,
                password = password, // В реальном приложении пароль должен быть зашифрован
                createdAt = System.currentTimeMillis()
            )

            userDao.insert(userEntity)

            // Сохраняем ID текущего пользователя
            sharedPreferences.edit().putString("current_user_id", userId).apply()

            Result.success(userEntity.toUser())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val userEntity = userDao.getUserByEmailAndPassword(email, password)
                ?: return Result.failure(Exception("Неверный email или пароль"))

            // Сохраняем ID текущего пользователя
            sharedPreferences.edit().putString("current_user_id", userEntity.id).apply()

            Result.success(userEntity.toUser())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserById(id: String): Result<User> {
        return try {
            val userEntity = userDao.getUserById(id)
                ?: return Result.failure(Exception("Пользователь не найден"))

            val followersCount = userDao.getFollowersCount(id)
            val followingCount = userDao.getFollowingCount(id)
            val postsCount = userDao.getPostsCount(id)

            val currentUserId = sharedPreferences.getString("current_user_id", null)
            val isFollowing = if (currentUserId != null) {
                userDao.isFollowing(currentUserId, id)
            } else {
                false
            }

            val user = userEntity.toUser().copy(
                followersCount = followersCount,
                followingCount = followingCount,
                postsCount = postsCount,
                isFollowing = isFollowing
            )

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserByUsername(username: String): Result<User> {
        return try {
            val userEntity = userDao.getUserByUsername(username)
                ?: return Result.failure(Exception("Пользователь не найден"))

            val followersCount = userDao.getFollowersCount(userEntity.id)
            val followingCount = userDao.getFollowingCount(userEntity.id)
            val postsCount = userDao.getPostsCount(userEntity.id)

            val currentUserId = sharedPreferences.getString("current_user_id", null)
            val isFollowing = if (currentUserId != null) {
                userDao.isFollowing(currentUserId, userEntity.id)
            } else {
                false
            }

            val user = userEntity.toUser().copy(
                followersCount = followersCount,
                followingCount = followingCount,
                postsCount = postsCount,
                isFollowing = isFollowing
            )

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(user: User): Result<User> {
        return try {
            val userEntity = userDao.getUserById(user.id)
                ?: return Result.failure(Exception("Пользователь не найден"))

            val updatedUserEntity = userEntity.copy(
                displayName = user.displayName,
                bio = user.bio,
                profileImageUrl = user.profileImageUrl
            )

            userDao.update(updatedUserEntity)

            Result.success(updatedUserEntity.toUser())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun followUser(followerId: String, followingId: String): Result<Boolean> {
        return try {
            // Проверяем, существуют ли пользователи
            val follower = userDao.getUserById(followerId)
                ?: return Result.failure(Exception("Подписчик не найден"))

            val following = userDao.getUserById(followingId)
                ?: return Result.failure(Exception("Пользователь для подписки не найден"))

            // Проверяем, не подписан ли уже пользователь
            if (followDao.exists(followerId, followingId)) {
                return Result.failure(Exception("Вы уже подписаны на этого пользователя"))
            }

            // Создаем подписку
            val followEntity = FollowEntity(
                followerId = followerId,
                followingId = followingId,
                createdAt = System.currentTimeMillis()
            )

            followDao.insert(followEntity)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unfollowUser(followerId: String, followingId: String): Result<Boolean> {
        return try {
            // Проверяем, существует ли подписка
            val follow = followDao.getFollow(followerId, followingId)
                ?: return Result.failure(Exception("Вы не подписаны на этого пользователя"))

            followDao.delete(follow)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isFollowing(followerId: String, followingId: String): Result<Boolean> {
        return try {
            val isFollowing = followDao.exists(followerId, followingId)
            Result.success(isFollowing)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getFollowers(userId: String): Flow<List<User>> {
        return followDao.getFollowers(userId).map { userEntities ->
            userEntities.map { it.toUser() }
        }
    }

    override fun getFollowing(userId: String): Flow<List<User>> {
        return followDao.getFollowing(userId).map { userEntities ->
            userEntities.map { it.toUser() }
        }
    }

    override fun searchUsers(query: String): Flow<List<User>> {
        return userDao.searchUsers(query).map { userEntities ->
            userEntities.map { it.toUser() }
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        val currentUserId = sharedPreferences.getString("current_user_id", null)
            ?: return Result.failure(Exception("Пользователь не авторизован"))

        return getUserById(currentUserId)
    }

    override suspend fun logout(): Result<Boolean> {
        return try {
            sharedPreferences.edit().remove("current_user_id").apply()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

