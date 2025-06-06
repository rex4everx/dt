package com.example.museart.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.museart.model.Post
import com.example.museart.model.User
import com.example.museart.ui.theme.Blue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { 
                Text(
                    text = "MuseArt",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            },
            backgroundColor = MaterialTheme.colorScheme.surface,
            elevation = 4.dp
        )
        
        // Демо-данные для ленты
        val posts = remember {
            generateDemoPosts()
        }
        
        LazyColumn {
            items(posts) { post ->
                PostItem(post = post)
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun PostItem(post: Post) {
    var isLiked by remember { mutableStateOf(post.isLiked) }
    var likesCount by remember { mutableStateOf(post.likesCount) }
    
    Column(modifier = Modifier.padding(16.dp)) {
        // Информация о пользователе
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Аватар пользователя (заглушка)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column {
                Text(
                    text = post.user?.displayName ?: "User",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "@${post.user?.username ?: "username"}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
        
        // Содержимое поста
        Text(
            text = post.content,
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        
        // Время публикации
        Text(
            text = formatDate(post.createdAt),
            color = Color.Gray,
            fontSize = 12.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Кнопки взаимодействия
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Комментарии
            IconButton(onClick = { /* Действие при нажатии */ }) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "Comment",
                    tint = Color.Gray
                )
            }
            Text(
                text = post.commentsCount.toString(),
                color = Color.Gray,
                fontSize = 12.sp
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Репосты
            IconButton(onClick = { /* Действие при нажатии */ }) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Repost",
                    tint = if (post.isReposted) Blue else Color.Gray
                )
            }
            Text(
                text = post.repostsCount.toString(),
                color = Color.Gray,
                fontSize = 12.sp
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Лайки
            IconButton(onClick = { 
                isLiked = !isLiked
                likesCount = if (isLiked) likesCount + 1 else likesCount - 1
            }) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) Blue else Color.Gray
                )
            }
            Text(
                text = likesCount.toString(),
                color = Color.Gray,
                fontSize = 12.sp
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Поделиться
            IconButton(onClick = { /* Действие при нажатии */ }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.Gray
                )
            }
        }
    }
}

// Форматирование даты
private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("HH:mm · dd MMM yyyy", Locale.getDefault())
    return formatter.format(date)
}

// Генерация демо-данных
private fun generateDemoPosts(): List<Post> {
    val user1 = User(
        id = "1",
        username = "johndoe",
        displayName = "John Doe",
        bio = "Android Developer",
        followersCount = 120,
        followingCount = 80,
        postsCount = 45
    )
    
    val user2 = User(
        id = "2",
        username = "janedoe",
        displayName = "Jane Doe",
        bio = "UI/UX Designer",
        followersCount = 350,
        followingCount = 120,
        postsCount = 78
    )
    
    return listOf(
        Post(
            id = "1",
            userId = "1",
            user = user1,
            content = "Привет, MuseArt! Это мой первый пост в этом приложении. Очень рад быть здесь!",
            createdAt = Date(),
            likesCount = 15,
            commentsCount = 3,
            repostsCount = 2
        ),
        Post(
            id = "2",
            userId = "2",
            user = user2,
            content = "Работаю над новым дизайном для мобильного приложения. Скоро поделюсь результатами!",
            createdAt = Date(System.currentTimeMillis() - 3600000), // 1 час назад
            likesCount = 42,
            commentsCount = 7,
            repostsCount = 5
        ),
        Post(
            id = "3",
            userId = "1",
            user = user1,
            content = "Изучаю Jetpack Compose. Это действительно меняет подход к разработке UI для Android!",
            createdAt = Date(System.currentTimeMillis() - 7200000), // 2 часа назад
            likesCount = 28,
            commentsCount = 5,
            repostsCount = 3
        ),
        Post(
            id = "4",
            userId = "2",
            user = user2,
            content = "Минимализм в дизайне - это не просто тренд, это философия. Меньше значит больше.",
            createdAt = Date(System.currentTimeMillis() - 10800000), // 3 часа назад
            likesCount = 53,
            commentsCount = 8,
            repostsCount = 12
        ),
        Post(
            id = "5",
            userId = "1",
            user = user1,
            content = "Kotlin - лучший язык для Android-разработки. Изменил мой подход к программированию.",
            createdAt = Date(System.currentTimeMillis() - 14400000), // 4 часа назад
            likesCount = 67,
            commentsCount = 12,
            repostsCount = 8
        )
    )
}

