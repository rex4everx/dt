package com.example.museart.data.repository

import android.content.Context
import com.example.museart.data.local.dao.LikeDao
import com.example.museart.data.local.dao.PostDao
import com.example.museart.data.local.dao.UserDao
import com.example.museart.data.local.entity.LikeEntity
import com.example.museart.data.local.entity.PostEntity
import com.example.museart.data.local.entity.UserEntity
import com.example.museart.model.Post
import com.example.museart.model.User
import com.example.museart.util.ImageUploader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import java.io.File
import java.util.Date
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class PostRepositoryTest {

    @Mock
    private lateinit var postDao: PostDao

    @Mock
    private lateinit var userDao: UserDao

    @Mock
    private lateinit var likeDao: LikeDao

    @Mock
    private lateinit var imageUploader: ImageUploader

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var imageFile: File

    private lateinit var postRepository: PostRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        postRepository = PostRepositoryImpl(postDao, userDao, likeDao, imageUploader, context)
    }

    @Test
    fun `createPost should return success when post is created`() = runTest {
        // Arrange
        val userId = UUID.randomUUID().toString()
        val content = "Test post content"
        val imageUrl = "https://example.com/image.jpg"
        val userEntity = UserEntity(
            id = userId,
            username = "testuser",
            displayName = "Test User",
            bio = "",
            profileImageUrl = "",
            followersCount = 0,
            followingCount = 0,
            postsCount = 0,
            isVerified = false,
            email = "test@example.com",
            password = "password123",
            createdAt = System.currentTimeMillis()
        )

        `when`(userDao.getUserById(userId)).thenReturn(userEntity)
        `when`(imageUploader.uploadImage(imageFile)).thenReturn(imageUrl)

        // Act
        val result = postRepository.createPost(userId, content, imageFile)

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals(content, result.getOrNull()?.content)
        assertEquals(imageUrl, result.getOrNull()?.imageUrl)
        verify(postDao).insert(any())
    }

    @Test
    fun `getPostById should return post with user and original post`() = runTest {
        // Arrange
        val postId = UUID.randomUUID().toString()
        val userId = UUID.randomUUID().toString()
        val originalPostId = UUID.randomUUID().toString()
        val originalPostUserId = UUID.randomUUID().toString()
        
        val postEntity = PostEntity(
            id = postId,
            userId = userId,
            content = "Test post content",
            imageUrl = "https://example.com/image.jpg",
            createdAt = System.currentTimeMillis(),
            likesCount = 5,
            commentsCount = 3,
            repostsCount = 2,
            originalPostId = originalPostId
        )
        
        val originalPostEntity = PostEntity(
            id = originalPostId,
            userId = originalPostUserId,
            content = "Original post content",
            imageUrl = "https://example.com/original.jpg",
            createdAt = System.currentTimeMillis() - 86400000, // 1 day ago
            likesCount = 10,
            commentsCount = 5,
            repostsCount = 3,
            originalPostId = null
        )
        
        val userEntity = UserEntity(
            id = userId,
            username = "testuser",
            displayName = "Test User",
            bio = "",
            profileImageUrl = "",
            followersCount = 0,
            followingCount = 0,
            postsCount = 0,
            isVerified = false,
            email = "test@example.com",
            password = "password123",
            createdAt = System.currentTimeMillis()
        )
        
        val originalPostUserEntity = UserEntity(
            id = originalPostUserId,
            username = "originaluser",
            displayName = "Original User",
            bio = "",
            profileImageUrl = "",
            followersCount = 0,
            followingCount = 0,
            postsCount = 0,
            isVerified = false,
            email = "original@example.com",
            password = "password123",
            createdAt = System.currentTimeMillis()
        )

        `when`(postDao.getPostById(postId)).thenReturn(postEntity)
        `when`(postDao.getUserForPost(postId)).thenReturn(userEntity)
        `when`(postDao.getOriginalPost(originalPostId)).thenReturn(originalPostEntity)
        `when`(postDao.getUserForPost(originalPostId)).thenReturn(originalPostUserEntity)

        // Act
        val result = postRepository.getPostById(postId)

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals(postId, result.getOrNull()?.id)
        assertEquals(userId, result.getOrNull()?.userId)
        assertEquals("Test User", result.getOrNull()?.user?.displayName)
        assertNotNull(result.getOrNull()?.originalPost)
        assertEquals(originalPostId, result.getOrNull()?.originalPost?.id)
        assertEquals("Original User", result.getOrNull()?.originalPost?.user?.displayName)
    }

    @Test
    fun `likePost should return success when post is liked`() = runTest {
        // Arrange
        val userId = UUID.randomUUID().toString()
        val postId = UUID.randomUUID().toString()
        val postEntity = PostEntity(
            id = postId,
            userId = UUID.randomUUID().toString(),
            content = "Test post content",
            imageUrl = null,
            createdAt = System.currentTimeMillis(),
            likesCount = 0,
            commentsCount = 0,
            repostsCount = 0,
            originalPostId = null
        )

        `when`(postDao.getPostById(postId)).thenReturn(postEntity)
        `when`(likeDao.exists(userId, postId)).thenReturn(false)

        // Act
        val result = postRepository.likePost(userId, postId)

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
        verify(likeDao).insert(any())
        verify(postDao).incrementLikesCount(postId)
    }

    @Test
    fun `unlikePost should return success when post is unliked`() = runTest {
        // Arrange
        val userId = UUID.randomUUID().toString()
        val postId = UUID.randomUUID().toString()
        val likeEntity = LikeEntity(
            userId = userId,
            postId = postId,
            createdAt = System.currentTimeMillis()
        )

        `when`(likeDao.getLike(userId, postId)).thenReturn(likeEntity)

        // Act
        val result = postRepository.unlikePost(userId, postId)

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
        verify(likeDao).delete(likeEntity)
        verify(postDao).decrementLikesCount(postId)
    }

    @Test
    fun `getFeedPosts should return list of posts`() = runTest {
        // Arrange
        val userId = UUID.randomUUID().toString()
        val postEntities = listOf(
            PostEntity(
                id = UUID.randomUUID().toString(),
                userId = UUID.randomUUID().toString(),
                content = "Post 1",
                imageUrl = null,
                createdAt = System.currentTimeMillis(),
                likesCount = 5,
                commentsCount = 3,
                repostsCount = 1,
                originalPostId = null
            ),
            PostEntity(
                id = UUID.randomUUID().toString(),
                userId = UUID.randomUUID().toString(),
                content = "Post 2",
                imageUrl = "https://example.com/image.jpg",
                createdAt = System.currentTimeMillis() - 3600000, // 1 hour ago
                likesCount = 10,
                commentsCount = 5,
                repostsCount = 2,
                originalPostId = null
            )
        )
        
        val userEntities = listOf(
            UserEntity(
                id = postEntities[0].userId,
                username = "user1",
                displayName = "User 1",
                bio = "",
                profileImageUrl = "",
                followersCount = 0,
                followingCount = 0,
                postsCount = 0,
                isVerified = false,
                email = "user1@example.com",
                password = "password123",
                createdAt = System.currentTimeMillis()
            ),
            UserEntity(
                id = postEntities[1].userId,
                username = "user2",
                displayName = "User 2",
                bio = "",
                profileImageUrl = "",
                followersCount = 0,
                followingCount = 0,
                postsCount = 0,
                isVerified = false,
                email = "user2@example.com",
                password = "password123",
                createdAt = System.currentTimeMillis()
            )
        )

        `when`(postDao.getFeedPosts(userId)).thenReturn(flowOf(postEntities))
        `when`(postDao.getUserForPost(postEntities[0].id)).thenReturn(userEntities[0])
        `when`(postDao.getUserForPost(postEntities[1].id)).thenReturn(userEntities[1])

        // Act
        val result = postRepository.getFeedPosts(userId)

        // Assert
        val posts = result.collect { posts ->
            assertEquals(2, posts.size)
            assertEquals("Post 1", posts[0].content)
            assertEquals("Post 2", posts[1].content)
            assertEquals("User 1", posts[0].user?.displayName)
            assertEquals("User 2", posts[1].user?.displayName)
        }
    }
}

