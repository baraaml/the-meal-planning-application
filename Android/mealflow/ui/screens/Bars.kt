package com.example.mealflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mealflow.R


@Composable
fun TopBar(navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(), // Add this to handle notification bar
//        color = MaterialTheme.colorScheme.surface,
        color = Color.White,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            ),
                            shape = CircleShape
                        )
                        .clickable {
                            navController.navigate("Profile Page")
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.apple_logo_icon),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.Center),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Hello,",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Abdelrahman",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
//                        color = MaterialTheme.colorScheme.onSurface
                        color = Color.Black

                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Notification button
                IconButton(
                    onClick = { /* Handle click */ },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
//                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                // Menu button
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
//                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
@Preview
@Composable
fun PreviewTopBar()
{
    TopBar(navController = rememberNavController())
}

@Composable
fun SearchBar() {
    var searchQuery by remember { mutableStateOf("") }

    OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(5.dp)
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .shadow(4.dp, shape = RoundedCornerShape(16.dp)),
        placeholder = { Text("Search", style = TextStyle(color = Color.Gray)) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "بحث", tint = Color.Gray)
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}
