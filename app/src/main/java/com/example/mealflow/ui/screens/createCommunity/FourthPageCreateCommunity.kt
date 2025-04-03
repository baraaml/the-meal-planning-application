package com.example.mealflow.ui.screens.createCommunity

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mealflow.R
import com.example.mealflow.database.token.TokenManager
import com.example.mealflow.network.createCommunityApi
import com.example.mealflow.ui.components.PrivacySettingsScreen
import com.example.mealflow.ui.components.TopBarCreateCommunity
import com.example.mealflow.viewModel.CommunityViewModel

@Composable
fun FourthStep(
    navController: NavController,
    viewModel: CommunityViewModel = viewModel(),
) {
    // Existing state observations
    val communityName = viewModel.communityName.observeAsState("").value
    val communityDescription = viewModel.communityDescription.observeAsState("").value
    val recipeCreationPermission = viewModel.recipeCreationPermission.observeAsState("").value
    val categories = viewModel.categories.observeAsState(emptyList()).value
    val selectedImageUri = viewModel.selectedImageUri

    // Create SnackbarHostState
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val accessToken = tokenManager.getAccessToken()

    val isButtonEnabled = communityName.isNotBlank() &&
            communityDescription.isNotBlank()

    // Wrap the entire content in a Scaffold to ensure SnackbarHost is visible
    Scaffold(
        topBar = {
            TopBarCreateCommunity(
                navController = navController,
                textNumber = "4 of 4",
                onClick = {
                    Log.d("FourthStep", "Creating Community")
                    Log.d("FourthStep", "Name: $communityName")
                    Log.d("FourthStep", "Description: $communityDescription")
                    Log.d("FourthStep", "Token: $accessToken")
                    Log.d("FourthStep", "Image URI: $selectedImageUri")

                    accessToken?.let {
                        createCommunityApi(
                            context,
                            communityName,
                            communityDescription,
                            recipeCreationPermission,
                            it,
                            categories,
                            selectedImageUri,
                            navController = navController,
                            snackbarHostState = snackbarHostState
                        )
                    }
                },
                isButtonEnabled = isButtonEnabled
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    modifier = Modifier.padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .systemBarsPadding()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Select community type",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Decide who can view and contribute in your community. Only public communities show up in search. Important: Once set, you can only change your community type with Reddit's approval.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(40.dp))

            PrivacySettingsScreen(viewModel)
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewFourthStep() {
    FourthStep(navController = rememberNavController())
}