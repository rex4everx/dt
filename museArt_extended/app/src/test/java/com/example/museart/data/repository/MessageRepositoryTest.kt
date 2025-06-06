package com.example.museart.data.repository

import com.example.museart.data.local.dao.ChatDao
import com.example.museart.data.local.dao.MessageDao
import com.example.museart.data.local.dao.UserDao
import com.example.museart.data.local.entity.ChatEntity
import com.example.museart.data.local.entity.MessageEntity
import com.example.museart.data.local.entity.UserEntity
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
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class MessageRepositoryTest {

    @Mock
    private lateinit var messageDao: MessageDao

    @Mock
    private lateinit var chatDao: ChatDao

    @Mock
    private lateinit var userDao: UserDao

    @Mock
    private lateinit var imageUploader: ImageUploader

    @Mock
    private lateinit var imageFile: File

    private lateinit var messageRepository: MessageRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        messageRepository = MessageRepositoryImpl(messageDao, chatDao, userDao, imageUploader)
    }

    @Test
    fun `sendMessage should return success when message is sent`() = runTest {
        // Arrange
        val senderId = UUID.randomUUID().toString()
        val receiverId = UUID.randomUUID().toString()
        val content = "Test message content"
        val imageUrl = "https://example.com/image.jpg"
        val chatId = UUID.randomUUID().toString()
        
        val senderEntity = UserEntity(
            id = senderId,
            username = "sender",
            displayName = "Sender User",
            bio = "",
            profileImageUrl = "",
            followersCount = 0,
            followingCount = 0,
            postsCount = 0,
            isVerified = false,
            email = "sender@example.com",
            password = "password123",
            createdAt = System.currentTimeMillis()
        )
        
        val receiverEntity = UserEntity(
            id = receiverId,
            username = "receiver",
            displayName = "Receiver User",
            bio = "",
            profileImageUrl = "",
            followersCount = 0,
            followingCount = 0,
            postsCount = 0,
            isVerified = false,
            email = "receiver@example.com",
            password = "password123",
            createdAt = System.currentTimeMillis()
        )
        
        val chatEntity = ChatEntity(
            id = chatId,
            user1Id = senderId,
            user2Id = receiverId,
            lastMessageId = null,
            unreadCount = 0,
            updatedAt = System.currentTimeMillis()
        )

        `when`(userDao.getUserById(senderId)).thenReturn(senderEntity)
        `when`(userDao.getUserById(receiverId)).thenReturn(receiverEntity)
        `when`(imageUploader.uploadImage(imageFile)).thenReturn(imageUrl)
        `when`(chatDao.getChatBetweenUsers(senderId, receiverId)).thenReturn(chatEntity)

        // Act
        val result = messageRepository.sendMessage(senderId, receiverId, content, imageFile)

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals(content, result.getOrNull()?.content)
        assertEquals(imageUrl, result.getOrNull()?.imageUrl)
        assertEquals(senderId, result.getOrNull()?.senderId)
        assertEquals(receiverId, result.getOrNull()?.receiverId)
        verify(messageDao).insert(any())
        verify(chatDao).updateLastMessage(eq(chatId), any(), any())
        verify(chatDao).incrementUnreadCount(chatId)
    }

    @Test
    fun `getMessageById should return message with sender and receiver`() = runTest {
        // Arrange
        val messageId = UUID.randomUUID().toString()
        val senderId = UUID.randomUUID().toString()
        val receiverId = UUID.randomUUID().toString()
        
        val messageEntity = MessageEntity(
            id = messageId,
            senderId = senderId,
            receiverId = receiverId,
            content = "Test message content",
            imageUrl = "https://example.com/image.jpg",
            createdAt = System.currentTimeMillis(),
            isRead = false
        )
        
        val senderEntity = UserEntity(
            id = senderId,
            username = "sender",
            displayName = "Sender User",
            bio = "",
            profileImageUrl = "",
            followersCount = 0,
            followingCount = 0,
            postsCount = 0,
            isVerified = false,
            email = "sender@example.com",
            password = "password123",
            createdAt = System.currentTimeMillis()
        )
        
        val receiverEntity = UserEntity(
            id = receiverId,
            username = "receiver",
            displayName = "Receiver User",
            bio = "",
            profileImageUrl = "",
            followersCount = 0,
            followingCount = 0,
            postsCount = 0,
            isVerified = false,
            email = "receiver@example.com",
            password = "password123",
            createdAt = System.currentTimeMillis()
        )

        `when`(messageDao.getMessageById(messageId)).thenReturn(messageEntity)
        `when`(messageDao.getUserById(senderId)).thenReturn(senderEntity)
        `when`(messageDao.getUserById(receiverId)).thenReturn(receiverEntity)

        // Act
        val result = messageRepository.getMessageById(messageId)

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals(messageId, result.getOrNull()?.id)
        assertEquals("Test message content", result.getOrNull()?.content)
        assertEquals("https://example.com/image.jpg", result.getOrNull()?.imageUrl)
        assertEquals("Sender User", result.getOrNull()?.sender?.displayName)
        assertEquals("Receiver User", result.getOrNull()?.receiver?.displayName)
    }

    @Test
    fun `markAsRead should return success when message is marked as read`() = runTest {
        // Arrange
        val messageId = UUID.randomUUID().toString()
        val senderId = UUID.randomUUID().toString()
        val receiverId = UUID.randomUUID().toString()
        val chatId = UUID.randomUUID().toString()
        
        val messageEntity = MessageEntity(
            id = messageId,
            senderId = senderId,
            receiverId = receiverId,
            content = "Test message content",
            imageUrl = null,
            createdAt = System.currentTimeMillis(),
            isRead = false
        )
        
        val chatEntity = ChatEntity(
            id = chatId,
            user1Id = senderId,
            user2Id = receiverId,
            lastMessageId = messageId,
            unreadCount = 1,
            updatedAt = System.currentTimeMillis()
        )

        `when`(messageDao.getMessageById(messageId)).thenReturn(messageEntity)
        `when`(chatDao.getChatBetweenUsers(senderId, receiverId)).thenReturn(chatEntity)
        `when`(messageDao.getUnreadCount(receiverId, senderId)).thenReturn(0)

        // Act
        val result = messageRepository.markAsRead(messageId)

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() == true)
        verify(messageDao).markAsRead(messageId)
        verify(chatDao).updateUnreadCount(chatId, 0)
    }

    @Test
    fun `createOrGetChat should return existing chat if it exists`() = runTest {
        // Arrange
        val userId1 = UUID.randomUUID().toString()
        val userId2 = UUID.randomUUID().toString()
        val chatId = UUID.randomUUID().toString()
        
        val user1Entity = UserEntity(
            id = userId1,
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
        )
        
        val user2Entity = UserEntity(
            id = userId2,
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
        
        val chatEntity = ChatEntity(
            id = chatId,
            user1Id = userId1,
            user2Id = userId2,
            lastMessageId = null,
            unreadCount = 0,
            updatedAt = System.currentTimeMillis()
        )

        `when`(userDao.getUserById(userId1)).thenReturn(user1Entity)
        `when`(userDao.getUserById(userId2)).thenReturn(user2Entity)
        `when`(chatDao.getChatBetweenUsers(userId1, userId2)).thenReturn(chatEntity)

        // Act
        val result = messageRepository.createOrGetChat(userId1, userId2)

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
        assertEquals(chatId, result.getOrNull()?.id)
        assertEquals(userId1, result.getOrNull()?.user1Id)
        assertEquals(userId2, result.getOrNull()?.user2Id)
        assertEquals("User 1", result.getOrNull()?.user1?.displayName)
        assertEquals("User 2", result.getOrNull()?.user2?.displayName)
    }

    @Test
    fun `getMessagesBetweenUsers should return list of messages`() = runTest {
        // Arrange
        val userId1 = UUID.randomUUID().toString()
        val userId2 = UUID.randomUUID().toString()
        
        val messageEntities = listOf(
            MessageEntity(
                id = UUID.randomUUID().toString(),
                senderId = userId1,
                receiverId = userId2,
                content = "Message 1",
                imageUrl = null,
                createdAt = System.currentTimeMillis() - 3600000, // 1 hour ago
                isRead = true
            ),
            MessageEntity(
                id = UUID.randomUUID().toString(),
                senderId = userId2,
                receiverId = userId1,
                content = "Message 2",
                imageUrl = "https://example.com/image.jpg",
                createdAt = System.currentTimeMillis(),
                isRead = false
            )
        )
        
        val user1Entity = UserEntity(
            id = userId1,
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
        )
        
        val user2Entity = UserEntity(
            id = userId2,
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

        `when`(messageDao.getMessagesBetweenUsers(userId1, userId2)).thenReturn(flowOf(messageEntities))
        `when`(messageDao.getUserById(userId1)).thenReturn(user1Entity)
        `when`(messageDao.getUserById(userId2)).thenReturn(user2Entity)

        // Act
        val result = messageRepository.getMessagesBetweenUsers(userId1, userId2)

        // Assert
        val messages = result.collect { messages ->
            assertEquals(2, messages.size)
            assertEquals("Message 1", messages[0].content)
            assertEquals("Message 2", messages[1].content)
            assertEquals("User 1", messages[0].sender?.displayName)
            assertEquals("User 2", messages[1].sender?.displayName)
        }
    }
}

