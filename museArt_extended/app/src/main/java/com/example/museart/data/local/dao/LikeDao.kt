package com.example.museart.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.museart.data.local.entity.LikeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LikeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(like: LikeEntity)

    @Delete
    suspend fun delete(like: LikeEntity)

    @Query("SELECT * FROM likes WHERE userId = :userId AND postId = :postId")
    suspend fun getLike(userId: String, postId: String): LikeEntity?

    @Query("SELECT * FROM likes WHERE postId = :postId")
    fun getLikesByPostId(postId: String): Flow<List<LikeEntity>>

    @Query("SELECT * FROM likes WHERE userId = :userId")
    fun getLikesByUserId(userId: String): Flow<List<LikeEntity>>

    @Query("DELETE FROM likes WHERE userId = :userId AND postId = :postId")
    suspend fun deleteLike(userId: String, postId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM likes WHERE userId = :userId AND postId = :postId)")
    suspend fun exists(userId: String, postId: String): Boolean
}

