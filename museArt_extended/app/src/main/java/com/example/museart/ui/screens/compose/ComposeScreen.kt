package com.example.museart.ui.screens.compose

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.museart.R
import com.example.museart.ui.theme.Blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeScreen(
    onPostCreated: () -> Unit
) {
    var postContent by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { 
                Text(
                    text = stringResource(R.string.compose),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { onPostCreated() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            backgroundColor = MaterialTheme.colorScheme.surface,
            elevation = 4.dp
        )
        
        Divider(color = Color.LightGray.copy(alpha = 0.5f))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Аватар пользователя (заглушка)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                // Поле ввода текста поста
                TextField(
                    value = postContent,
                    onValueChange = { postContent = it },
                    placeholder = { Text(stringResource(R.string.post_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Кнопки для добавления медиа и других элементов
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Добавление изображения */ }) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Add Image",
                            tint = Blue
                        )
                    }
                    
                    IconButton(onClick = { /* Добавление опроса */ }) {
                        Icon(
                            imageVector = Icons.Default.Poll,
                            contentDescription = "Add Poll",
                            tint = Blue
                        )
                    }
                    
                    IconButton(onClick = { /* Добавление местоположения */ }) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Add Location",
                            tint = Blue
                        )
                    }
                    
                    IconButton(onClick = { /* Добавление тега */ }) {
                        Icon(
                            imageVector = Icons.Default.Tag,
                            contentDescription = "Add Tag",
                            tint = Blue
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Кнопка публикации
                    Button(
                        onClick = { onPostCreated() },
                        enabled = postContent.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Blue,
                            disabledContainerColor = Blue.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(stringResource(R.string.post))
                    }
                }
            }
        }
    }
}

