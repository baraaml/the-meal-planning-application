package com.example.mealflow.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mealflow.ui.components.CommunityList
import com.example.mealflow.viewModel.CommunityViewModel
import com.example.mealflow.viewModel.GetCommunityViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.accompanist.swiperefresh.*

data class CommunityData(
    val id: String,
    val name: String,
    val imageUrl: String,
    val memberCount: Int,
    val postCount: Int
)

@Composable
fun CommunityPage(
    navController: NavController,
    viewModelGet: GetCommunityViewModel
) {
    val communities by viewModelGet.communities.collectAsState()
    val formattedCommunities = communities.map { community ->
        CommunityData(community.id, community.name, community.image.toString(), 76, 65)
    }
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Log.d("CommunityPage", "Calling fetchAndStoreCommunities()")
        viewModelGet.fetchAndStoreCommunities()
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            coroutineScope.launch {
                isRefreshing = true
                viewModelGet.fetchAndStoreCommunities()
                delay(2000) // Simulate data loading
                isRefreshing = false
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEFEFEF))
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(1) {
                        SearchBar()
                        CommunityList(communities = formattedCommunities)
                        PostCommunity()
                        PostUser()
                        PostUser()
                        PostUser()
                        PostUser()
                        PostCommunity()
                        PostCommunity()
                        PostCommunity()
                        PostCommunity()
                        PostUser()
                    }
                }

                FloatingActionButton(
                    onClick = { navController.navigate("FirstStep Page") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCommunityScreen() {
    val communityViewModel: CommunityViewModel = viewModel()
    val getCommunityViewModel: GetCommunityViewModel = viewModel()
    CommunityPage(navController = rememberNavController(), getCommunityViewModel)
}
