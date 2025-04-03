//package com.example.mealflow.ui.screens
//
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Timer
//import androidx.compose.material3.Button
//import androidx.compose.material3.Card
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.Tab
//import androidx.compose.material3.TabRow
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import coil.compose.rememberAsyncImagePainter
//import com.example.mealflow.database.community.GetCommunityEntity
//import com.example.mealflow.viewModel.GetCommunityViewModel
//
//@Composable
//fun MainScreen(communityId: String?) {
//    var selectedTabIndex by remember { mutableStateOf(0) }
//    val communityViewModel: GetCommunityViewModel = viewModel()
//    // استدعاء دالة ViewModel لتحميل بيانات المجتمع
//    communityId?.let {
//        communityViewModel.getCommunityById(it)
//    }
//    // عرض البيانات عندما يتم تحميلها
//    val communityData = communityViewModel.communityData
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        // قسم الهيدر مع صورة وتدرج شفاف
//        HeaderSection()
//
//        // العنوان وزر الانضمام
//        TitleSection()
//
//        // وصف قصير
//        DescriptionSection()
//
//        // معلومات الأعضاء والوصفات
//        MembersAndRecipesCount()
//
//        // خيارات إضافية (كالأزرار أو الشيبس)
//        OptionsRow()
//
//        // تب بار
//        TabRow(
//            selectedTabIndex = selectedTabIndex,
////            backgroundColor = Color.White
//        ) {
//            listOf("Recipes", "Conversations").forEachIndexed { index, title ->
//                Tab(
//                    selected = selectedTabIndex == index,
//                    onClick = { selectedTabIndex = index }
//                ) {
//                    Text(
//                        text = title,
//                        modifier = Modifier.padding(16.dp),
//                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
//                    )
//                }
//            }
//        }
//
//        // عرض المحتوى الخاص بكل تب
//        when (selectedTabIndex) {
//            0 -> RecipesList()
//            1 -> ConversationsList()
//        }
//    }
//}
//
//@Composable
//fun HeaderSection() {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(220.dp)
//    ) {
//        // صورة الخلفية
//        Image(
//            painter = rememberAsyncImagePainter(
//                model = "https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg"
//            ),
//            contentDescription = "Header Image",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.fillMaxSize()
//        )
//        // تدرج شفاف يعطي تأثيراً لطيفاً
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                    Brush.verticalGradient(
//                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
//                        startY = 100f,
//                        endY = 600f
//                    )
//                )
//        )
//        // نص توضيحي على صورة الهيدر (اختياري)
//        Text(
//            text = "Delicious & Fast Meals",
//            style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
//            modifier = Modifier
//                .align(Alignment.BottomStart)
//                .padding(16.dp)
//        )
//    }
//}
//
//@Composable
//fun TitleSection() {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 12.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = "30 min meals",
//            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
//        )
//        Button(
//            onClick = { /* عملية الانضمام */ },
//            shape = RoundedCornerShape(20.dp),
////            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
//        ) {
//            Text(text = "Join", color = Color.White)
//        }
//    }
//}
//
//@Composable
//fun DescriptionSection() {
//    Text(
//        text = "Quick meals that are on the table before everyone gets hangry. " +
//                "30 minutes or less total time (prep & cook time combined).",
//        style = MaterialTheme.typography.bodySmall,
//        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
//        fontSize = 14.sp,
//        color = Color.Gray
//    )
//}
//
//@Composable
//fun MembersAndRecipesCount() {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp),
//        horizontalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        Text(
//            text = "990k members",
//            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
//        )
//        Text(
//            text = "7.3k recipes",
//            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
//        )
//    }
//}
//
//@Composable
//fun OptionsRow() {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 8.dp, vertical = 4.dp),
//        horizontalArrangement = Arrangement.SpaceEvenly
//    ) {
//        val options = listOf("Ingredients", "Meal Type", "Diet", "Cook time")
//        options.forEach { option ->
//            OutlinedButton(
//                onClick = { /* إجراء الاختيار */ },
//                shape = RoundedCornerShape(50),
//                modifier = Modifier.height(36.dp)
//            ) {
//                Text(text = option, fontSize = 12.sp)
//            }
//        }
//    }
//}
//
//@Composable
//fun RecipesList() {
//    val recipes = listOf(
//        RecipeItem("Beef and Broccoli Stir-Fry", "https://via.placeholder.com/400"),
//        RecipeItem("Creamy Pasta", "https://via.placeholder.com/300"),
//        RecipeItem("Grilled Chicken Salad", "https://via.placeholder.com/200")
//    )
//
//    LazyColumn(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        items(recipes) { recipe ->
//            RecipeCard(recipe)
//        }
//    }
//}
//
//@Composable
//fun ConversationsList() {
//    val conversations = listOf(
//        "Any quick meal ideas for busy weeknights?",
//        "Best way to store leftovers?",
//        "How to meal prep for the entire week?"
//    )
//
//    LazyColumn(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        items(conversations) { conv ->
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//            ) {
//                Text(text = conv, style = MaterialTheme.typography.bodySmall)
//                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
//            }
//        }
//    }
//}
//
//data class RecipeItem(val title: String, val imageUrl: String)
//
//@Composable
//fun RecipeCard(recipe: RecipeItem) {
//    Card(
//        shape = RoundedCornerShape(16.dp),
////        elevation = 8.dp,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp)
//            .clickable { /* عملية التعامل مع النقر */ }
//    ) {
//        Row(
//            modifier = Modifier.padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // صورة الوصفة مع زوايا مستديرة
//            Image(
//                painter = rememberAsyncImagePainter(model = recipe.imageUrl),
//                contentDescription = "Recipe Image",
//                modifier = Modifier
//                    .size(100.dp)
//                    .clip(RoundedCornerShape(12.dp)),
//                contentScale = ContentScale.Crop
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//            // تفاصيل الوصفة
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = recipe.title,
//                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    text = "Delicious and quick to make!",
//                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
//                    fontSize = 14.sp
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Timer,
//                        contentDescription = "Cooking Time",
//                        tint = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "30 min",
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Preview
//@Composable
//fun r()
//{
//    MainScreen("A")
//}
package com.example.mealflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.mealflow.database.UserPreferencesManager
import com.example.mealflow.database.community.GetCommunityEntity
import com.example.mealflow.viewModel.GetCommunityViewModel

@Composable
fun MainScreen(communityViewModel: GetCommunityViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val userPreferencesManager = remember { UserPreferencesManager(context) }
    val communityId by userPreferencesManager.getCommunityId().collectAsState(initial = "Loading...")

//    val communityData = communityViewModel.getCommunityById(communityId)
    val communityData by communityViewModel.communityData.collectAsState()
    LaunchedEffect(communityId) {
        communityViewModel.getCommunityById(communityId)
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // قسم الهيدر مع صورة وتدرج شفاف
        HeaderSection(communityData)

        // العنوان وزر الانضمام
        TitleSection()

        // وصف قصير
        DescriptionSection(communityData)

        // معلومات الأعضاء والوصفات
        MembersAndRecipesCount(communityData)

        // خيارات إضافية (كالأزرار أو الشيبس)
        OptionsRow()

        // تب بار
        TabRow(
            selectedTabIndex = selectedTabIndex,
        ) {
            listOf("Recipes", "Conversations").forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }
        }

        // عرض المحتوى الخاص بكل تب
        when (selectedTabIndex) {
            0 -> RecipesList()
            1 -> ConversationsList()
        }
    }
}

@Composable
fun HeaderSection(communityData: GetCommunityEntity?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        // صورة الخلفية
        val imageUrl = communityData?.image ?: "https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg"
        Image(
            painter = rememberAsyncImagePainter(
                model = imageUrl
            ),
            contentDescription = "Header Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // تدرج شفاف
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                        startY = 100f,
                        endY = 600f
                    )
                )
        )
        // نص توضيحي على صورة الهيدر
        Text(
            text = communityData?.name ?: "Delicious & Fast Meals",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}

@Composable
fun TitleSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "30 min meals",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
        )
        Button(
            onClick = { /* عملية الانضمام */ },
            shape = RoundedCornerShape(20.dp),
        ) {
            Text(text = "Join", color = Color.White)
        }
    }
}

@Composable
fun DescriptionSection(communityData: GetCommunityEntity?) {
    Text(
        text = communityData?.description
            ?: ("Quick meals that are on the table before everyone gets hangry. " +
                    "30 minutes or less total time (prep & cook time combined)."),
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        fontSize = 14.sp,
        color = Color.Gray
    )
}

@Composable
fun MembersAndRecipesCount(communityData: GetCommunityEntity?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "${communityData?.members?.size ?: 0} members",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(
            text = "${communityData?.categories?.size ?: 0} recipes",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
fun OptionsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val options = listOf("Ingredients", "Meal Type", "Diet", "Cook time")
        options.forEach { option ->
            OutlinedButton(
                onClick = { /* إجراء الاختيار */ },
                shape = RoundedCornerShape(50),
                modifier = Modifier.height(36.dp)
            ) {
                Text(text = option, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun RecipesList() {
    val recipes = listOf(
        RecipeItem("Beef and Broccoli Stir-Fry", "https://via.placeholder.com/400"),
        RecipeItem("Creamy Pasta", "https://via.placeholder.com/300"),
        RecipeItem("Grilled Chicken Salad", "https://via.placeholder.com/200")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(recipes) { recipe ->
            RecipeCard(recipe)
        }
    }
}

@Composable
fun ConversationsList() {
    val conversations = listOf(
        "Any quick meal ideas for busy weeknights?",
        "Best way to store leftovers?",
        "How to meal prep for the entire week?"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(conversations) { conv ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = conv, style = MaterialTheme.typography.bodySmall)
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

data class RecipeItem(val title: String, val imageUrl: String)

@Composable
fun RecipeCard(recipe: RecipeItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { /* عملية التعامل مع النقر */ }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // صورة الوصفة مع زوايا مستديرة
            Image(
                painter = rememberAsyncImagePainter(model = recipe.imageUrl),
                contentDescription = "Recipe Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            // تفاصيل الوصفة
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Delicious and quick to make!",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Cooking Time",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "30 min",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun r() {
//    MainScreen()
}
