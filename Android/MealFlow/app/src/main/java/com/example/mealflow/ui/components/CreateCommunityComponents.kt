package com.example.mealflow.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mealflow.random.BottomBar
import com.example.mealflow.viewModel.CommunityViewModel
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*


@Composable
fun TopBarCreateCommunity(navController: NavController, textnumber: String, onClick: () -> Unit, isButtonEnabled: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .size(40.dp)
                .padding(end = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Text(
            text = textnumber,
            modifier = Modifier
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onClick,
            enabled = isButtonEnabled,
            modifier = Modifier
                .padding(8.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(50.dp)),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isButtonEnabled) Color(0xFF009951) else Color.Gray
            )
        ) {
            Text(
                text = "Next",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


@Composable
fun ImagePickerSection(viewModel: CommunityViewModel) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.setImageUri(uri) // تخزين الصورة في الـ ViewModel
    }

    Column(modifier = Modifier.padding(16.dp)) {
        ImagePickerItem(
            title = "Community Image",
            subtitle = "Displays at 10:3",
            onPickImage = { imagePickerLauncher.launch("image/*") },
            selectedImageUri = viewModel.selectedImageUri // تمرير الصورة المختارة
        )
    }
}

@Composable
fun ImagePickerItem(title: String, subtitle: String? = null, onPickImage: () -> Unit, selectedImageUri: Uri?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            subtitle?.let {
                Text(text = it, color = Color.Gray, fontSize = 14.sp)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // عرض الصورة المختارة إذا تم تحديد صورة
            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // زر اختيار الصورة
            Button(
                onClick = { onPickImage() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Icon(imageVector = Icons.Outlined.AddPhotoAlternate, contentDescription = "Add Image", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Add", color = Color.White)
            }
        }
    }
}


@Composable
fun PrivacySettingsScreen(viewModel: CommunityViewModel = viewModel()) {
    var selectedOption by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PrivacyOption(
            title = "Public",
            description = "Anyone can search for, view, and contribute",
            icon = Icons.Default.Public,
            selected = selectedOption == "Public",
            onSelect = {
                selectedOption = "Public"
                viewModel.updateRecipeCreationPermission("ANY_MEMBER")
            }
        )

//        PrivacyOption(
//            title = "Restricted",
//            description = "Anyone can view, but restrict who can contribute",
//            icon = Icons.Default.Visibility,
//            selected = selectedOption == "Restricted",
//            onSelect = { selectedOption = "Restricted" }
//        )

        PrivacyOption(
            title = "Private",
            description = "Only approved members can view and contribute",
            icon = Icons.Default.Lock,
            selected = selectedOption == "Private",
            onSelect = {
                selectedOption = "Private"
                viewModel.updateRecipeCreationPermission("ANY_MEMBER")
            }
        )
    }
}

@Composable
fun PrivacyOption(
    title: String,
    description: String,
    icon: ImageVector,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold)
            Text(text = description, fontSize = 12.sp, color = Color.Gray)
        }
        RadioButton(
            selected = selected,
            onClick = onSelect
        )
    }
}

@Composable
fun CardCommunityTest(
    communityName: String,
    members: Int,
    recipes: Int,
    imageUri: Uri?,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        modifier = modifier
            .width(220.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth()
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "صورة مجتمع $communityName",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Image", color = Color.White)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f)
                                )
                            )
                        )
                )

                Button(
                    onClick = {/*ToDo*/},
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Join",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = communityName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$members عضو",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.Rounded.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$recipes وصفة",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}


@Composable
fun CommunityTopicsScreen() {
    val categories = mapOf(
        "Anime & Cosplay" to listOf("Anime & Manga", "Cosplay"),
        "Art" to listOf("Performing Arts", "Architecture", "Design", "Art", "Filmmaking", "Digital Art", "Photography"),
        "Business & Finance" to listOf("Personal Finance", "Crypto", "Economics", "Startup", "Business News & Discussion", "Deals & Marketplace"),
        "Collectibles & Other Hobbies" to listOf("Model Building", "Collectibles", "Other Hobbies", "Toys")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Choose community topics",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        for ((category, topics) in categories) {
            Text(
                text = category,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(((topics.size / 2 + 1) * 48).dp)
            ) {
                items(topics.size) { index ->
                    TopicCard(topic = topics[index])
                }
            }
        }
    }
}

@Composable
fun TopicCard(topic: String) {
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = topic)
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewFourthStep1() {
    val communityViewModel: CommunityViewModel = viewModel()
    CommunityTopicsScreen()
}

@Composable
fun ThirdStep1(navController: NavController,viewModel: CommunityViewModel = viewModel())
{
//    val communityName by viewModel.communityName.observeAsState("")
//    var description by remember { mutableStateOf("test") }
    val isButtonEnabled = true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .systemBarsPadding() // ✅ Adds space so that the content doesn't overlap the notification bar
            .padding(16.dp)
    ) {
        TopBarCreateCommunity(navController = navController,"3 of 4",{/*ToDo*/},isButtonEnabled)
        Spacer(modifier = Modifier.height(8.dp)) // Small space between the button and the text
        Text(
            text = "Select community type",
            modifier = Modifier
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .padding(8.dp),
            textAlign = TextAlign.Center,
            fontSize = 35.sp
        )

    }
}

@Composable
fun SelectableTopicCard(topic: String, isSelected: Boolean, onSelected: (String) -> Unit) {
    Card(
        onClick = { onSelected(topic) },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFB3E5FC) else Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = topic,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}