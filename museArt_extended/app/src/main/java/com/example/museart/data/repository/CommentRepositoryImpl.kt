package com.example.museart.data.repository

import com.example.museart.data.local.dao.CommentDao
import com.example.museart.data.local.dao.CommentLikeDao
import com.example.museart.data.local.dao.PostDao
import com.example.museart.data.local.dao.UserDao
import com.example.museart.data.local.entity.CommentEntity
import com.example.museart.data.local.entity.CommentLikeEntity
import com.example.museart.model.Comment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class CommentRepositoryImpl(
    private val commentDao: CommentDao,
    private val userDao: UserDao,
    private val postDao: PostDao,
    private val commentLikeDao: CommentLikeDao
) : CommentRepository {

    override suspend fun createComment(userId: String, postId: String, content: String): Result<Comment> {
        return try {
            // Проверяем, существует ли пользователь
            val user = userDao.getUserById(userId)
                ?: return Result.failure(Exception("Пользователь не найден"))

            // Проверяем, существует ли пост
            val post = postDao.getPostById(postId)
                ?: return Result.failure(Exception("Пост не найден"))

            // Создаем комментарий
            val commentId = UUID.randomUUID().toString()
            val commentEntity = CommentEntity(
                id = commentId,
                postId = postId,
                userId = userId,
                content = content,
                createdAt = System.currentTimeMillis(),
                likesCount = 0
            )

            commentDao.insert(commentEntity)
            postDao.incrementCommentsCount(postId)

            val comment = commentEntity.toComment(user = user.toUser())
            Result.success(comment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCommentById(id: String): Result<Comment> {
        return try {
            val commentEntity = commentDao.getCommentById(id)
                ?: return Result.failure(Exception("Комментарий не найден"))

            val user = commentDao.getUserForComment(id)?.toUser()
            val comment = commentEntity.toComment(user = user)

            Result.success(comment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteComment(id: String): Result<Boolean> {
        return try {
            val commentEntity = commentDao.getCommentById(id)
                ?: return Result.failure(Exception("Комментарий не найден"))

            commentDao.delete(commentEntity)
            postDao.decrementCommentsCount(commentEntity.postId)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun likeComment(userId: String, commentId: String): Result<Boolean> {
        return try {
            // Проверяем, существует ли комментарий
            val comment = commentDao.getCommentById(commentId)
                ?: return Result.failure(Exception("Комментарий не найден"))

            // Проверяем, не лайкнул ли уже пользователь этот комментарий
            if (commentLikeDao.exists(userId, commentId)) {
                return Result.failure(Exception("Вы уже лайкнули этот комментарий"))
            }

            // Создаем лайк
            val commentLikeEntity = CommentLikeEntity(
                userId = userId,
                commentId = commentId,
                createdAt = System.currentTimeMillis()
            )

            commentLikeDao.insert(commentLikeEntity)
            commentDao.incrementLikesCount(commentId)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unlikeComment(userId: String, commentId: String): Result<Boolean> {
        return try {
            // Проверяем, существует ли лайк
            val commentLike = commentLikeDao.getCommentLike(userId, commentId)
                ?: return Result.failure(Exception("Вы не лайкали этот комментарий"))

            commentLikeDao.delete(commentLike)
            commentDao.decrementLikesCount(commentId)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isLiked(userId: String, commentId: String): Result<Boolean> {
        return try {
            val isLiked = commentLikeDao.exists(userId, commentId)
            Result.success(isLiked)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCommentsByPostId(postId: String): Flow<List<Comment>> {
        return commentDao.getCommentsByPostId(postId).map { commentEntities ->
            commentEntities.map { commentEntity ->
                val user = commentDao.getUserForComment(commentEntity.id)?.toUser()
                commentEntity.toComment(user = user)
            }
        }
    }

    override fun getCommentsByUserId(userId: String): Flow<List<Comment>> {
        return commentDao.getCommentsByUserId(userId).map { commentEntities ->
            commentEntities.map { commentEntity ->
                val user = commentDao.getUserForComment(commentEntity.id)?.toUser()
                commentEntity.toComment(user = user)
            }
        }
    }
}

