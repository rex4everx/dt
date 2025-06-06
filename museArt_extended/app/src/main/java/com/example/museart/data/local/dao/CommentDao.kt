package com.example.museart.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.museart.data.local.entity.CommentEntity
import com.example.museart.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(comments: List<CommentEntity>)

    @Update
    suspend fun update(comment: CommentEntity)

    @Delete
    suspend fun delete(comment: CommentEntity)

    @Query("SELECT * FROM comments WHERE id = :id")
    suspend fun getCommentById(id: String): CommentEntity?

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt DESC")
    fun getCommentsByPostId(postId: String): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE userId = :userId ORDER BY createdAt DESC")
    fun getCommentsByUserId(userId: String): Flow<List<CommentEntity>>

    @Query("SELECT u.* FROM users u INNER JOIN comments c ON u.id = c.userId WHERE c.id = :commentId")
    suspend fun getUserForComment(commentId: String): UserEntity?

    @Query("SELECT COUNT(*) FROM comment_likes WHERE commentId = :commentId")
    suspend fun getLikesCount(commentId: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM comment_likes WHERE userId = :userId AND commentId = :commentId)")
    suspend fun isLiked(userId: String, commentId: String): Boolean

    @Transaction
    suspend fun incrementLikesCount(commentId: String) {
        val comment = getCommentById(commentId) ?: return
        update(comment.copy(likesCount = comment.likesCount + 1))
    }

    @Transaction
    suspend fun decrementLikesCount(commentId: String) {
        val comment = getCommentById(commentId) ?: return
        update(comment.copy(likesCount = comment.likesCount - 1))
    }
}

