package com.example.museart.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.museart.data.local.entity.FollowEntity
import com.example.museart.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FollowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(follow: FollowEntity)

    @Delete
    suspend fun delete(follow: FollowEntity)

    @Query("SELECT * FROM follows WHERE followerId = :followerId AND followingId = :followingId")
    suspend fun getFollow(followerId: String, followingId: String): FollowEntity?

    @Query("SELECT u.* FROM users u INNER JOIN follows f ON u.id = f.followingId WHERE f.followerId = :userId")
    fun getFollowing(userId: String): Flow<List<UserEntity>>

    @Query("SELECT u.* FROM users u INNER JOIN follows f ON u.id = f.followerId WHERE f.followingId = :userId")
    fun getFollowers(userId: String): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM follows WHERE followingId = :userId")
    suspend fun getFollowersCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM follows WHERE followerId = :userId")
    suspend fun getFollowingCount(userId: String): Int

    @Query("DELETE FROM follows WHERE followerId = :followerId AND followingId = :followingId")
    suspend fun deleteFollow(followerId: String, followingId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM follows WHERE followerId = :followerId AND followingId = :followingId)")
    suspend fun exists(followerId: String, followingId: String): Boolean
}

