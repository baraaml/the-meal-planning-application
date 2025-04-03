package com.example.mealflow.ui.screens.createCommunity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mealflow.R
import com.example.mealflow.ui.components.CardCommunityTest
import com.example.mealflow.ui.components.ImagePickerSection
import com.example.mealflow.ui.components.TopBarCreateCommunity
import com.example.mealflow.viewModel.CommunityViewModel

@Composable
fun SecondStep(navController: NavController, viewModel: CommunityViewModel = viewModel()) {
    val communityName by viewModel.communityName.observeAsState("")
    val selectedImageUri = viewModel.selectedImageUri

    val isButtonEnabled = selectedImageUri != null

    Scaffold(
        topBar = {
            TopBarCreateCommunity(
                navController = navController,
                textNumber = "2 of 4",
                onClick = { navController.navigate("ThirdStep Page") },
                isButtonEnabled = isButtonEnabled
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.navigationBarsPadding()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.CreateCommunityHeaderSecondPage),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.CreateCommunitySubHeaderSecondPage),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.Preview),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            CardCommunityTest(
                communityName = communityName,
                members = 1000,
                recipes = 50,
                imageUri = selectedImageUri
            )

            Spacer(modifier = Modifier.height(20.dp))

            ImagePickerSection(viewModel)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSecondStep() {
    SecondStep(navController = rememberNavController())
}
