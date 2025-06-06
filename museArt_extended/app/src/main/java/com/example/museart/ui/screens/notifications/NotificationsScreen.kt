package com.example.museart.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.museart.R
import com.example.museart.model.Notification
import com.example.museart.model.NotificationType
import com.example.museart.model.User
import com.example.museart.ui.theme.Blue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Все", "Упоминания")
    
    // Демо-данные для уведомлений
    val notifications = remember {
        generateDemoNotifications()
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { 
                Text(
                    text = stringResource(R.string.notifications),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            },
            backgroundColor = MaterialTheme.colorScheme.surface,
            elevation = 4.dp
        )
        
        // Вкладки
        TabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = Blue
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }
        
        // Список уведомлений
        LazyColumn {
            items(
                if (selectedTabIndex == 0) notifications
                else notifications.filter { it.type == NotificationType.MENTION }
            ) { notification ->
                NotificationItem(notification = notification)
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Действие при нажатии на уведомление */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Иконка типа уведомления
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(getNotificationColor(notification.type)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getNotificationIcon(notification.type),
                contentDescription = null,
                tint = Color.White
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            // Текст уведомления
            Text(
                text = getNotificationText(notification),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp
            )
            
            // Время уведомления
            Text(
                text = formatDate(notification.createdAt),
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        
        // Аватар пользователя, который вызвал уведомление (заглушка)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
    }
}

// Получение иконки для типа уведомления
@Composable
private fun getNotificationIcon(type: NotificationType): ImageVector {
    return when (type) {
        NotificationType.LIKE -> Icons.Default.Favorite
        NotificationType.COMMENT -> Icons.Default.Star
        NotificationType.REPOST -> Icons.Default.Repeat
        NotificationType.FOLLOW -> Icons.Default.Person
        NotificationType.MENTION -> Icons.Default.Star
    }
}

// Получение цвета для типа уведомления
@Composable
private fun getNotificationColor(type: NotificationType): Color {
    return when (type) {
        NotificationType.LIKE -> Color.Red
        NotificationType.COMMENT -> Blue
        NotificationType.REPOST -> Color.Green
        NotificationType.FOLLOW -> Color.Magenta
        NotificationType.MENTION -> Blue
    }
}

// Получение текста уведомления
@Composable
private fun getNotificationText(notification: Notification): String {
    val username = notification.triggerUser?.displayName ?: "Пользователь"
    
    return when (notification.type) {
        NotificationType.LIKE -> "$username лайкнул ваш пост"
        NotificationType.COMMENT -> "$username прокомментировал ваш пост"
        NotificationType.REPOST -> "$username сделал репост вашего поста"
        NotificationType.FOLLOW -> "$username подписался на вас"
        NotificationType.MENTION -> "$username упомянул вас в посте"
    }
}

// Форматирование даты
private fun formatDate(date: Date): String {
    val now = Date()
    val diffInMillis = now.time - date.time
    val diffInHours = diffInMillis / (60 * 60 * 1000)
    
    return when {
        diffInHours < 1 -> "менее часа назад"
        diffInHours < 24 -> "$diffInHours ч назад"
        else -> {
            val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
            formatter.format(date)
        }
    }
}

// Генерация демо-данных для уведомлений
private fun generateDemoNotifications(): List<Notification> {
    val user1 = User(
        id = "2",
        username = "janedoe",
        displayName = "Jane Doe",
        bio = "UI/UX Designer"
    )
    
    val user2 = User(
        id = "3",
        username = "marksmith",
        displayName = "Mark Smith",
        bio = "iOS Developer"
    )
    
    val user3 = User(
        id = "4",
        username = "sarahj",
        displayName = "Sarah Johnson",
        bio = "Product Manager"
    )
    
    return listOf(
        Notification(
            id = "1",
            userId = "1",
            triggerUserId = "2",
            triggerUser = user1,
            type = NotificationType.LIKE,
            postId = "1",
            createdAt = Date(System.currentTimeMillis() - 30 * 60 * 1000) // 30 минут назад
        ),
        Notification(
            id = "2",
            userId = "1",
            triggerUserId = "3",
            triggerUser = user2,
            type = NotificationType.COMMENT,
            postId = "1",
            commentId = "1",
            createdAt = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000) // 2 часа назад
        ),
        Notification(
            id = "3",
            userId = "1",
            triggerUserId = "4",
            triggerUser = user3,
            type = NotificationType.FOLLOW,
            createdAt = Date(System.currentTimeMillis() - 5 * 60 * 60 * 1000) // 5 часов назад
        ),
        Notification(
            id = "4",
            userId = "1",
            triggerUserId = "2",
            triggerUser = user1,
            type = NotificationType.REPOST,
            postId = "2",
            createdAt = Date(System.currentTimeMillis() - 8 * 60 * 60 * 1000) // 8 часов назад
        ),
        Notification(
            id = "5",
            userId = "1",
            triggerUserId = "3",
            triggerUser = user2,
            type = NotificationType.MENTION,
            postId = "3",
            createdAt = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000) // 1 день назад
        )
    )
}

