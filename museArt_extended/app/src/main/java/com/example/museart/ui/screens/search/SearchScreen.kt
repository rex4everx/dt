package com.example.museart.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.example.museart.model.User
import com.example.museart.ui.theme.Blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Топ", "Последние", "Люди", "Медиа")
    
    // Демо-данные для поиска
    val users = remember {
        listOf(
            User(
                id = "1",
                username = "johndoe",
                displayName = "John Doe",
                bio = "Android Developer",
                followersCount = 120,
                followingCount = 80,
                postsCount = 45
            ),
            User(
                id = "2",
                username = "janedoe",
                displayName = "Jane Doe",
                bio = "UI/UX Designer",
                followersCount = 350,
                followingCount = 120,
                postsCount = 78
            ),
            User(
                id = "3",
                username = "marksmith",
                displayName = "Mark Smith",
                bio = "iOS Developer",
                followersCount = 210,
                followingCount = 150,
                postsCount = 62
            ),
            User(
                id = "4",
                username = "sarahj",
                displayName = "Sarah Johnson",
                bio = "Product Manager",
                followersCount = 420,
                followingCount = 180,
                postsCount = 95
            )
        )
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { 
                Text(
                    text = stringResource(R.string.search),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            },
            backgroundColor = MaterialTheme.colorScheme.surface,
            elevation = 4.dp
        )
        
        // Поле поиска
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Поиск в MuseArt") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50)),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.LightGray.copy(alpha = 0.2f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
        }
        
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
        
        // Результаты поиска
        when (selectedTabIndex) {
            0, 1 -> {
                // Топ и Последние - показываем заглушку
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Введите поисковый запрос",
                        color = Color.Gray
                    )
                }
            }
            2 -> {
                // Люди - показываем список пользователей
                LazyColumn {
                    items(users.filter { 
                        searchQuery.isEmpty() || 
                        it.displayName.contains(searchQuery, ignoreCase = true) || 
                        it.username.contains(searchQuery, ignoreCase = true) 
                    }) { user ->
                        UserItem(user = user)
                        Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }
            }
            3 -> {
                // Медиа - показываем заглушку
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Введите поисковый запрос для поиска медиа",
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun UserItem(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Переход на профиль пользователя */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Аватар пользователя (заглушка)
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = user.displayName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "@${user.username}",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = user.bio,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp
            )
        }
    }
}

