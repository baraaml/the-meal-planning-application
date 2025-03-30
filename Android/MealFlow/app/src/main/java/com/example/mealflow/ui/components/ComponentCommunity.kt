package com.example.mealflow.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.mealflow.R
import com.example.mealflow.database.token.TokenManager
import com.example.mealflow.network.JoinCommunityRequest
import com.example.mealflow.network.JoinCommunityResponse
import com.example.mealflow.network.joinCommunityApi
import com.example.mealflow.ui.screens.CommunityData
import com.example.mealflow.viewModel.CommunityViewModel
import com.example.mealflow.viewModel.TokenViewModel

@Composable
fun CardCommunity(
    communityName: String,
    imageUrl: String,
    members: Int,
    recipes: Int,
    onJoinClick: () -> Unit,
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
            // صورة المجتمع مع زر الانضمام
            Box(
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth()
            ) {
                // صورة المجتمع
                Image(
                    painter = rememberAsyncImagePainter(
                        model = imageUrl,
                        error = painterResource(R.drawable.placeholder), // صورة افتراضية عند الخطأ
                        placeholder = painterResource(R.drawable.loading_placeholder) // صورة أثناء التحميل
                    ),
                    contentDescription = "صورة مجتمع $communityName",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // طبقة شفافة للتباين
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

                // زر الانضمام
                Button(
                    onClick = onJoinClick,
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

            // معلومات المجتمع
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
                    // أيقونة وعدد الأعضاء
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

                    // أيقونة وعدد الوصفات
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

// ---------------------------------------- Login Page -------------------------------------------
@Composable
fun CommunityList(communities: List<CommunityData>) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val accessToken = tokenManager.getAccessToken()
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "My Communities",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "See all",
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier.clickable { /* Navigate to all communities */ }
            )
        }
        Spacer(modifier = Modifier.height(8.dp)) // Adds spacing before LazyRow
//        LazyRow(
//            modifier = Modifier.fillMaxWidth(),
//            contentPadding = PaddingValues(horizontal = 16.dp),
//            horizontalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            items(communities) { community ->
//                CardCommunity(
//                    communityName = community.first,
//                    members = community.second,
//                    recipes = community.third
//                )
//            }
//        }
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(communities, key = { it.id }) { community -> // ✅ إضافة مفتاح لكل عنصر
                CardCommunity(
                    communityName = community.name,
                    imageUrl = community.imageUrl,
                    members = 34,
                    recipes = 27,
                    {
                        joinCommunityApi(community.id,context,snackbarHostState)
                    },
                )
            }
        }
    }
}

