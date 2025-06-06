package com.example.museart.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.museart.data.local.dao.FollowDao
import com.example.museart.data.local.dao.UserDao
import com.example.museart.data.local.entity.UserEntity
import com.example.museart.model.User
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
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class UserRepositoryTest {

    @Mock
    private lateinit var userDao: UserDao

    @Mock
    private lateinit var followDao: FollowDao

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private lateinit var userRepository: UserRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(context.getSharedPreferences(any(), any())).thenReturn(sharedPreferences)
        `when`(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor)
        `when`(sharedPreferencesEditor.putString(any(), any())).thenReturn(sharedPreferencesEditor)
        `when`(sharedPreferencesEditor.remove(any())).thenReturn(sharedPreferencesEditor)

        userRepository = UserRepositoryImpl(userDao, followDao, context)
    }

    @Test
    fun `registerUser should return success when user is registered`() = runTest {
        // Arrange
        val username = "testuser"
        val email = "test@example.com"
        val password = "password123"
        val displayName = "Test User"

        `when`(userDao.getUserByEmail(email)).thenReturn(null)
        `when`(userDao.getUserByUsername(username)).thenReturn(null)

        // Act
        val result = userRepository.registerUser(username, email, password, displayName)

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        verify(userDao).insert(any())
        verify(sharedPreferencesEditor).putString(eq("current_user_id"), any())
        verify(sharedPreferencesEditor).apply()
    }

    @Test
    fun `loginUser should return success when credentials are valid`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val userId = UUID.randomUUID().toString()
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
            email = email,
            password = password,
            createdAt = System.currentTimeMillis()
        )

        `when`(userDao.getUserByEmailAndPassword(email, password)).thenReturn(userEntity)

        // Act
        val result = userRepository.loginUser(email, password)

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals(userId, result.getOrNull()?.id)
        verify(sharedPreferencesEditor).putString(eq("current_user_id"), eq(userId))
        verify(sharedPreferencesEditor).apply()
    }

    @Test
    fun `getUserById should return user with correct counts`() = runTest {
        // Arrange
        val userId = UUID.randomUUID().toString()
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
        `when`(userDao.getFollowersCount(userId)).thenReturn(5)
        `when`(userDao.getFollowingCount(userId)).thenReturn(10)
        `when`(userDao.getPostsCount(userId)).thenReturn(15)
        `when`(sharedPreferences.getString(eq("current_user_id"), any())).thenReturn("current_user_id")
        `when`(userDao.isFollowing("current_user_id", userId)).thenReturn(true)

        // Act
        val result = userRepository.getUserById(userId)

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals(userId, result.getOrNull()?.id)
        assertEquals(5, result.getOrNull()?.followersCount)
        assertEquals(10, result.getOrNull()?.followingCount)
        assertEquals(15, result.getOrNull()?.postsCount)
        assertEquals(true, result.getOrNull()?.isFollowing)
    }

    @Test
    fun `searchUsers should return list of users`() = runTest {
        // Arrange
        val query = "test"
        val userEntities = listOf(
            UserEntity(
                id = UUID.randomUUID().toString(),
                username = "testuser1",
                displayName = "Test User 1",
                bio = "",
                profileImageUrl = "",
                followersCount = 0,
                followingCount = 0,
                postsCount = 0,
                isVerified = false,
                email = "test1@example.com",
                password = "password123",
                createdAt = System.currentTimeMillis()
            ),
            UserEntity(
                id = UUID.randomUUID().toString(),
                username = "testuser2",
                displayName = "Test User 2",
                bio = "",
                profileImageUrl = "",
                followersCount = 0,
                followingCount = 0,
                postsCount = 0,
                isVerified = false,
                email = "test2@example.com",
                password = "password123",
                createdAt = System.currentTimeMillis()
            )
        )

        `when`(userDao.searchUsers(query)).thenReturn(flowOf(userEntities))

        // Act
        val result = userRepository.searchUsers(query)

        // Assert
        val users = result.collect { users ->
            assertEquals(2, users.size)
            assertEquals("testuser1", users[0].username)
            assertEquals("testuser2", users[1].username)
        }
    }

    @Test
    fun `logout should return success`() = runTest {
        // Arrange
        `when`(sharedPreferencesEditor.remove("current_user_id")).thenReturn(sharedPreferencesEditor)

        // Act
        val result = userRepository.logout()

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
        verify(sharedPreferencesEditor).remove("current_user_id")
        verify(sharedPreferencesEditor).apply()
    }
}

