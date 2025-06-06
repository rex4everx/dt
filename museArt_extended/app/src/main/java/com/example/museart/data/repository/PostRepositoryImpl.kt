package com.example.museart.data.repository

import android.content.Context
import com.example.museart.data.local.dao.LikeDao
import com.example.museart.data.local.dao.PostDao
import com.example.museart.data.local.dao.UserDao
import com.example.museart.data.local.entity.LikeEntity
import com.example.museart.data.local.entity.PostEntity
import com.example.museart.model.Post
import com.example.museart.util.ImageUploader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.util.Date
import java.util.UUID

class PostRepositoryImpl(
    private val postDao: PostDao,
    private val userDao: UserDao,
    private val likeDao: LikeDao,
    private val imageUploader: ImageUploader,
    private val context: Context
) : PostRepository {

    override suspend fun createPost(userId: String, content: String, imageFile: File?): Result<Post> {
        return try {
            // Проверяем, существует ли пользователь
            val user = userDao.getUserById(userId)
                ?: return Result.failure(Exception("Пользователь не найден"))

            // Загружаем изображение, если оно есть
            var imageUrl: String? = null
            if (imageFile != null) {
                imageUrl = imageUploader.uploadImage(imageFile)
            }

            // Создаем пост
            val postId = UUID.randomUUID().toString()
            val postEntity = PostEntity(
                id = postId,
                userId = userId,
                content = content,
                imageUrl = imageUrl,
                createdAt = System.currentTimeMillis(),
                likesCount = 0,
                commentsCount = 0,
                repostsCount = 0,
                originalPostId = null
            )

            postDao.insert(postEntity)

            val post = postEntity.toPost(user = user.toUser())
            Result.success(post)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPostById(id: String): Result<Post> {
        return try {
            val postEntity = postDao.getPostById(id)
                ?: return Result.failure(Exception("Пост не найден"))

            val user = postDao.getUserForPost(id)?.toUser()
            
            var originalPost: Post? = null
            if (postEntity.originalPostId != null) {
                val originalPostEntity = postDao.getOriginalPost(postEntity.originalPostId)
                if (originalPostEntity != null) {
                    val originalPostUser = postDao.getUserForPost(originalPostEntity.id)?.toUser()
                    originalPost = originalPostEntity.toPost(user = originalPostUser)
                }
            }

            val post = postEntity.toPost(user = user, originalPost = originalPost)
            Result.success(post)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePost(id: String): Result<Boolean> {
        return try {
            val postEntity = postDao.getPostById(id)
                ?: return Result.failure(Exception("Пост не найден"))

            postDao.delete(postEntity)

            // Если у поста было изображение, удаляем его
            if (postEntity.imageUrl != null) {
                imageUploader.deleteImage(postEntity.imageUrl)
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun likePost(userId: String, postId: String): Result<Boolean> {
        return try {
            // Проверяем, существует ли пост
            val post = postDao.getPostById(postId)
                ?: return Result.failure(Exception("Пост не найден"))

            // Проверяем, не лайкнул ли уже пользователь этот пост
            if (likeDao.exists(userId, postId)) {
                return Result.failure(Exception("Вы уже лайкнули этот пост"))
            }

            // Создаем лайк
            val likeEntity = LikeEntity(
                userId = userId,
                postId = postId,
                createdAt = System.currentTimeMillis()
            )

            likeDao.insert(likeEntity)
            postDao.incrementLikesCount(postId)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unlikePost(userId: String, postId: String): Result<Boolean> {
        return try {
            // Проверяем, существует ли лайк
            val like = likeDao.getLike(userId, postId)
                ?: return Result.failure(Exception("Вы не лайкали этот пост"))

            likeDao.delete(like)
            postDao.decrementLikesCount(postId)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun repostPost(userId: String, originalPostId: String, content: String): Result<Post> {
        return try {
            // Проверяем, существует ли оригинальный пост
            val originalPost = postDao.getPostById(originalPostId)
                ?: return Result.failure(Exception("Оригинальный пост не найден"))

            // Проверяем, существует ли пользователь
            val user = userDao.getUserById(userId)
                ?: return Result.failure(Exception("Пользователь не найден"))

            // Создаем репост
            val postId = UUID.randomUUID().toString()
            val postEntity = PostEntity(
                id = postId,
                userId = userId,
                content = content,
                imageUrl = null,
                createdAt = System.currentTimeMillis(),
                likesCount = 0,
                commentsCount = 0,
                repostsCount = 0,
                originalPostId = originalPostId
            )

            postDao.insert(postEntity)
            postDao.incrementRepostsCount(originalPostId)

            val originalPostUser = postDao.getUserForPost(originalPostId)?.toUser()
            val originalPostModel = originalPost.toPost(user = originalPostUser)
            val post = postEntity.toPost(user = user.toUser(), originalPost = originalPostModel)

            Result.success(post)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isLiked(userId: String, postId: String): Result<Boolean> {
        return try {
            val isLiked = likeDao.exists(userId, postId)
            Result.success(isLiked)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isReposted(userId: String, postId: String): Result<Boolean> {
        return try {
            val isReposted = postDao.isReposted(userId, postId)
            Result.success(isReposted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getFeedPosts(userId: String): Flow<List<Post>> {
        return postDao.getFeedPosts(userId).map { postEntities ->
            postEntities.map { postEntity ->
                val user = postDao.getUserForPost(postEntity.id)?.toUser()
                
                var originalPost: Post? = null
                if (postEntity.originalPostId != null) {
                    val originalPostEntity = postDao.getPostById(postEntity.originalPostId)
                    if (originalPostEntity != null) {
                        val originalPostUser = postDao.getUserForPost(originalPostEntity.id)?.toUser()
                        originalPost = originalPostEntity.toPost(user = originalPostUser)
                    }
                }
                
                postEntity.toPost(user = user, originalPost = originalPost)
            }
        }
    }

    override fun getUserPosts(userId: String): Flow<List<Post>> {
        return postDao.getPostsByUserId(userId).map { postEntities ->
            postEntities.map { postEntity ->
                val user = postDao.getUserForPost(postEntity.id)?.toUser()
                
                var originalPost: Post? = null
                if (postEntity.originalPostId != null) {
                    val originalPostEntity = postDao.getPostById(postEntity.originalPostId)
                    if (originalPostEntity != null) {
                        val originalPostUser = postDao.getUserForPost(originalPostEntity.id)?.toUser()
                        originalPost = originalPostEntity.toPost(user = originalPostUser)
                    }
                }
                
                postEntity.toPost(user = user, originalPost = originalPost)
            }
        }
    }

    override fun searchPosts(query: String): Flow<List<Post>> {
        return postDao.searchPosts(query).map { postEntities ->
            postEntities.map { postEntity ->
                val user = postDao.getUserForPost(postEntity.id)?.toUser()
                
                var originalPost: Post? = null
                if (postEntity.originalPostId != null) {
                    val originalPostEntity = postDao.getPostById(postEntity.originalPostId)
                    if (originalPostEntity != null) {
                        val originalPostUser = postDao.getUserForPost(originalPostEntity.id)?.toUser()
                        originalPost = originalPostEntity.toPost(user = originalPostUser)
                    }
                }
                
                postEntity.toPost(user = user, originalPost = originalPost)
            }
        }
    }
}

