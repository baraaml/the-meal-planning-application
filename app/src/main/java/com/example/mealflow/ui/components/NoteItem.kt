package com.example.mealflow.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mealflow.R
import com.example.mealflow.data.model.Note

@Composable
fun NoteItem(note: Note) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // User info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (note.user.profilePicture != null) {
                    AsyncImage(
                        model = note.user.profilePicture,
                        contentDescription = "Profile picture of ${note.user.username}",
                        modifier = Modifier
                            .width(32.dp)
                            .height(32.dp),
                        placeholder = painterResource(id = R.drawable.android_robot),
                        error = painterResource(id = R.drawable.android_robot)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = note.user.username,
                    fontWeight = FontWeight.Bold
                )

                if (note.didCook) {
                    Spacer(modifier = Modifier.width(8.dp))
                    SuggestionChip(
                        onClick = { },
                        label = { Text("Cooked this") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Comment
            Text(note.comment)

            // Image if available
            if (note.imageUrl != null) {
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = note.imageUrl,
                    contentDescription = "Image shared by ${note.user.username}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.android_robot),
                    error = painterResource(id = R.drawable.android_robot)
                )
            }

            // Likes/Dislikes
            if (note.likes > 0 || note.dislikes > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        text = "${note.likes} likes",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (note.dislikes > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${note.dislikes} dislikes",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

