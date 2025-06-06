package com.example.museart.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.museart.data.local.entity.PostEntity
import com.example.museart.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<PostEntity>)

    @Update
    suspend fun update(post: PostEntity)

    @Delete
    suspend fun delete(post: PostEntity)

    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getPostById(id: String): PostEntity?

    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY createdAt DESC")
    fun getPostsByUserId(userId: String): Flow<List<PostEntity>>

    @Query("""
        SELECT p.* FROM posts p
        INNER JOIN follows f ON p.userId = f.followingId
        WHERE f.followerId = :userId
        ORDER BY p.createdAt DESC
    """)
    fun getFeedPosts(userId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE content LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchPosts(query: String): Flow<List<PostEntity>>

    @Query("SELECT u.* FROM users u INNER JOIN posts p ON u.id = p.userId WHERE p.id = :postId")
    suspend fun getUserForPost(postId: String): UserEntity?

    @Query("SELECT * FROM posts WHERE id = :originalPostId")
    suspend fun getOriginalPost(originalPostId: String): PostEntity?

    @Query("SELECT COUNT(*) FROM likes WHERE postId = :postId")
    suspend fun getLikesCount(postId: String): Int

    @Query("SELECT COUNT(*) FROM comments WHERE postId = :postId")
    suspend fun getCommentsCount(postId: String): Int

    @Query("SELECT COUNT(*) FROM posts WHERE originalPostId = :postId")
    suspend fun getRepostsCount(postId: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM likes WHERE userId = :userId AND postId = :postId)")
    suspend fun isLiked(userId: String, postId: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM posts WHERE userId = :userId AND originalPostId = :postId)")
    suspend fun isReposted(userId: String, postId: String): Boolean

    @Transaction
    suspend fun incrementLikesCount(postId: String) {
        val post = getPostById(postId) ?: return
        update(post.copy(likesCount = post.likesCount + 1))
    }

    @Transaction
    suspend fun decrementLikesCount(postId: String) {
        val post = getPostById(postId) ?: return
        update(post.copy(likesCount = post.likesCount - 1))
    }

    @Transaction
    suspend fun incrementCommentsCount(postId: String) {
        val post = getPostById(postId) ?: return
        update(post.copy(commentsCount = post.commentsCount + 1))
    }

    @Transaction
    suspend fun decrementCommentsCount(postId: String) {
        val post = getPostById(postId) ?: return
        update(post.copy(commentsCount = post.commentsCount - 1))
    }

    @Transaction
    suspend fun incrementRepostsCount(postId: String) {
        val post = getPostById(postId) ?: return
        update(post.copy(repostsCount = post.repostsCount + 1))
    }

    @Transaction
    suspend fun decrementRepostsCount(postId: String) {
        val post = getPostById(postId) ?: return
        update(post.copy(repostsCount = post.repostsCount - 1))
    }
}

