package com.example.museart.ui.screens.profile

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
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.museart.R
import com.example.museart.model.Post
import com.example.museart.model.User
import com.example.museart.ui.theme.Blue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    // Демо-данные для профиля
    val user = remember {
        User(
            id = "1",
            username = "johndoe",
            displayName = "John Doe",
            bio = "Android Developer | Kotlin Enthusiast | Coffee Lover",
            followersCount = 1250,
            followingCount = 420,
            postsCount = 86
        )
    }
    
    val posts = remember {
        generateUserPosts(user)
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { 
                Text(
                    text = user.displayName,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { /* Навигация назад */ }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* Меню опций */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            backgroundColor = MaterialTheme.colorScheme.surface,
            elevation = 4.dp
        )
        
        LazyColumn {
            item {
                ProfileHeader(user = user)
            }
            
            items(posts) { post ->
                PostItem(post = post)
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun ProfileHeader(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Аватар и кнопка редактирования
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Аватар пользователя (заглушка)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Кнопка редактирования профиля или подписки
            if (user.id == "1") { // Если это текущий пользователь
                OutlinedButton(
                    onClick = { /* Редактирование профиля */ },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(stringResource(R.string.edit_profile))
                }
            } else {
                Button(
                    onClick = { /* Подписка/отписка */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = if (user.isFollowing) stringResource(R.string.unfollow) else stringResource(R.string.follow),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Имя пользователя и username
        Text(
            text = user.displayName,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "@${user.username}",
            color = Color.Gray,
            fontSize = 14.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Био
        Text(
            text = user.bio,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Статистика
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "${user.followingCount} ${stringResource(R.string.following)}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${user.followersCount} ${stringResource(R.string.followers)}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Вкладки
        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf(stringResource(R.string.posts), stringResource(R.string.likes))
        
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

// Генерация демо-постов для пользователя
private fun generateUserPosts(user: User): List<Post> {
    return listOf(
        Post(
            id = "1",
            userId = user.id,
            user = user,
            content = "Работаю над новым проектом с использованием Jetpack Compose. Это действительно меняет подход к разработке UI для Android!",
            createdAt = Date(),
            likesCount = 42,
            commentsCount = 7,
            repostsCount = 5
        ),
        Post(
            id = "2",
            userId = user.id,
            user = user,
            content = "Kotlin - лучший язык для Android-разработки. Изменил мой подход к программированию.",
            createdAt = Date(System.currentTimeMillis() - 86400000), // 1 день назад
            likesCount = 67,
            commentsCount = 12,
            repostsCount = 8
        ),
        Post(
            id = "3",
            userId = user.id,
            user = user,
            content = "Изучаю новые возможности Material Design 3. Много интересных компонентов и анимаций!",
            createdAt = Date(System.currentTimeMillis() - 172800000), // 2 дня назад
            likesCount = 28,
            commentsCount = 5,
            repostsCount = 3
        )
    )
}

