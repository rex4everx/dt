package com.example.museart.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.museart.data.local.entity.CommentLikeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentLikeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(commentLike: CommentLikeEntity)

    @Delete
    suspend fun delete(commentLike: CommentLikeEntity)

    @Query("SELECT * FROM comment_likes WHERE userId = :userId AND commentId = :commentId")
    suspend fun getCommentLike(userId: String, commentId: String): CommentLikeEntity?

    @Query("SELECT * FROM comment_likes WHERE commentId = :commentId")
    fun getCommentLikesByCommentId(commentId: String): Flow<List<CommentLikeEntity>>

    @Query("SELECT * FROM comment_likes WHERE userId = :userId")
    fun getCommentLikesByUserId(userId: String): Flow<List<CommentLikeEntity>>

    @Query("DELETE FROM comment_likes WHERE userId = :userId AND commentId = :commentId")
    suspend fun deleteCommentLike(userId: String, commentId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM comment_likes WHERE userId = :userId AND commentId = :commentId)")
    suspend fun exists(userId: String, commentId: String): Boolean
}

