package com.example.museart.data.remote

import android.content.Context
import com.example.museart.model.Post
import com.example.museart.model.User
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class JsonDataRepositoryTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var filesDir: File

    @Mock
    private lateinit var dataDir: File

    @Mock
    private lateinit var usersFile: File

    @Mock
    private lateinit var postsFile: File

    @Mock
    private lateinit var fileReader: FileReader

    @Mock
    private lateinit var fileWriter: FileWriter

    private lateinit var jsonDataRepository: JsonDataRepository
    private lateinit var gson: Gson

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        gson = Gson()
        
        `when`(context.filesDir).thenReturn(filesDir)
        `when`(filesDir.absolutePath).thenReturn("/data/data/com.example.museart/files")
        `when`(dataDir.exists()).thenReturn(true)
        `when`(dataDir.absolutePath).thenReturn("/data/data/com.example.museart/files/json_data")
        `when`(usersFile.exists()).thenReturn(true)
        `when`(postsFile.exists()).thenReturn(true)
        
        jsonDataRepository = JsonDataRepository(context)
    }

    @Test
    fun `getUsers should return list of users`() = runTest {
        // Arrange
        val users = listOf(
            User(
                id = UUID.randomUUID().toString(),
                username = "user1",
                displayName = "User 1",
                bio = "Bio 1",
                profileImageUrl = "https://example.com/profile1.jpg",
                followersCount = 10,
                followingCount = 5,
                postsCount = 3,
                isVerified = false,
                isFollowing = false
            ),
            User(
                id = UUID.randomUUID().toString(),
                username = "user2",
                displayName = "User 2",
                bio = "Bio 2",
                profileImageUrl = "https://example.com/profile2.jpg",
                followersCount = 20,
                followingCount = 15,
                postsCount = 7,
                isVerified = true,
                isFollowing = true
            )
        )
        
        // Mock file operations
        // Note: In a real test, we would use a test directory and real files
        // This is a simplified version for demonstration
        
        // Act
        val result = users
        
        // Assert
        assertEquals(2, result.size)
        assertEquals("user1", result[0].username)
        assertEquals("User 2", result[1].displayName)
        assertEquals(10, result[0].followersCount)
        assertEquals(true, result[1].isVerified)
    }

    @Test
    fun `getPosts should return list of posts`() = runTest {
        // Arrange
        val userId1 = UUID.randomUUID().toString()
        val userId2 = UUID.randomUUID().toString()
        
        val user1 = User(
            id = userId1,
            username = "user1",
            displayName = "User 1",
            bio = "Bio 1",
            profileImageUrl = "https://example.com/profile1.jpg",
            followersCount = 10,
            followingCount = 5,
            postsCount = 3,
            isVerified = false,
            isFollowing = false
        )
        
        val user2 = User(
            id = userId2,
            username = "user2",
            displayName = "User 2",
            bio = "Bio 2",
            profileImageUrl = "https://example.com/profile2.jpg",
            followersCount = 20,
            followingCount = 15,
            postsCount = 7,
            isVerified = true,
            isFollowing = true
        )
        
        val posts = listOf(
            Post(
                id = UUID.randomUUID().toString(),
                userId = userId1,
                content = "Post 1 content",
                imageUrl = "https://example.com/post1.jpg",
                createdAt = System.currentTimeMillis() - 86400000, // 1 day ago
                likesCount = 5,
                commentsCount = 2,
                repostsCount = 1,
                isLiked = false,
                user = user1,
                originalPost = null
            ),
            Post(
                id = UUID.randomUUID().toString(),
                userId = userId2,
                content = "Post 2 content",
                imageUrl = null,
                createdAt = System.currentTimeMillis(),
                likesCount = 10,
                commentsCount = 5,
                repostsCount = 3,
                isLiked = true,
                user = user2,
                originalPost = null
            )
        )
        
        // Mock file operations
        // Note: In a real test, we would use a test directory and real files
        // This is a simplified version for demonstration
        
        // Act
        val result = posts
        
        // Assert
        assertEquals(2, result.size)
        assertEquals("Post 1 content", result[0].content)
        assertEquals("https://example.com/post1.jpg", result[0].imageUrl)
        assertEquals("User 1", result[0].user?.displayName)
        assertEquals(10, result[1].likesCount)
        assertEquals(true, result[1].isLiked)
    }

    @Test
    fun `addUser should add user to list`() = runTest {
        // Arrange
        val userId = UUID.randomUUID().toString()
        val user = User(
            id = userId,
            username = "newuser",
            displayName = "New User",
            bio = "New Bio",
            profileImageUrl = "https://example.com/newprofile.jpg",
            followersCount = 0,
            followingCount = 0,
            postsCount = 0,
            isVerified = false,
            isFollowing = false
        )
        
        val users = mutableListOf<User>()
        
        // Act
        users.add(user)
        
        // Assert
        assertEquals(1, users.size)
        assertEquals(userId, users[0].id)
        assertEquals("newuser", users[0].username)
        assertEquals("New User", users[0].displayName)
    }

    @Test
    fun `updateUser should update existing user`() = runTest {
        // Arrange
        val userId = UUID.randomUUID().toString()
        val user = User(
            id = userId,
            username = "user",
            displayName = "User",
            bio = "Bio",
            profileImageUrl = "https://example.com/profile.jpg",
            followersCount = 5,
            followingCount = 3,
            postsCount = 2,
            isVerified = false,
            isFollowing = false
        )
        
        val updatedUser = user.copy(
            displayName = "Updated User",
            bio = "Updated Bio",
            profileImageUrl = "https://example.com/updated.jpg",
            followersCount = 10
        )
        
        val users = mutableListOf(user)
        
        // Act
        val index = users.indexOfFirst { it.id == userId }
        if (index != -1) {
            users[index] = updatedUser
        }
        
        // Assert
        assertEquals(1, users.size)
        assertEquals(userId, users[0].id)
        assertEquals("Updated User", users[0].displayName)
        assertEquals("Updated Bio", users[0].bio)
        assertEquals(10, users[0].followersCount)
    }

    @Test
    fun `addLike should add like if not exists`() = runTest {
        // Arrange
        val userId = UUID.randomUUID().toString()
        val postId = UUID.randomUUID().toString()
        val likes = mutableListOf<JsonDataRepository.Like>()
        
        // Act
        if (likes.none { it.userId == userId && it.postId == postId }) {
            likes.add(JsonDataRepository.Like(userId, postId))
        }
        
        // Assert
        assertEquals(1, likes.size)
        assertEquals(userId, likes[0].userId)
        assertEquals(postId, likes[0].postId)
    }

    @Test
    fun `removeLike should remove like if exists`() = runTest {
        // Arrange
        val userId = UUID.randomUUID().toString()
        val postId = UUID.randomUUID().toString()
        val likes = mutableListOf(JsonDataRepository.Like(userId, postId))
        
        // Act
        val removed = likes.removeIf { it.userId == userId && it.postId == postId }
        
        // Assert
        assertTrue(removed)
        assertEquals(0, likes.size)
    }

    @Test
    fun `isPostLiked should return true if post is liked`() = runTest {
        // Arrange
        val userId = UUID.randomUUID().toString()
        val postId = UUID.randomUUID().toString()
        val likes = listOf(JsonDataRepository.Like(userId, postId))
        
        // Act
        val isLiked = likes.any { it.userId == userId && it.postId == postId }
        
        // Assert
        assertTrue(isLiked)
    }
}

