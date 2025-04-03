package com.example.mealflow.ui.screens.createCommunity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mealflow.R
import com.example.mealflow.ui.components.TopBarCreateCommunity
import com.example.mealflow.viewModel.CommunityViewModel

//@Composable
//fun FirstStep(navController: NavController, viewModel: CommunityViewModel = viewModel()) {
//    val communityName by viewModel.communityName.observeAsState("")
//    val description by viewModel.communityDescription.observeAsState("")
//
//    val isButtonEnabled = communityName.isNotBlank() && description.length >= 15
//
//    Scaffold(
//        topBar = {
//            TopBarCreateCommunity(
//                navController = navController,
//                textNumber = "1 of 4",
//                onClick = { if (isButtonEnabled) navController.navigate("SecondStep Page") },
//                isButtonEnabled = isButtonEnabled
//            )
//        },
//        bottomBar = {
////            BottomBar(navController)
//        },
//        containerColor = Color(0xFFEFEFEF),
//        modifier = Modifier.navigationBarsPadding()
//    )
//    { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .navigationBarsPadding()
//                .systemBarsPadding()
//                .padding(paddingValues)
//        ) {
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = stringResource(id = R.string.CreateCommunityHeaderFirstPage),
//                modifier = Modifier.padding(8.dp),
//                textAlign = TextAlign.Center,
//                fontSize = 35.sp,
//                fontWeight = FontWeight.Bold
//            )
//
//            Text(
//                text = "Community Name *",
//                color = Color.Gray,
//                fontWeight = FontWeight.SemiBold
//            )
//
//            OutlinedTextField(
//                value = communityName,
//                onValueChange = { viewModel.updateCommunityName(it) },
//                modifier = Modifier.fillMaxWidth(),
//                textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
//                label = { Text("r/Community") }
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text("Description *", color = Color.Gray, fontWeight = FontWeight.SemiBold)
//            OutlinedTextField(
//                value = description,
//                onValueChange = { viewModel.updateCommunityDescription(it) },
//                modifier = Modifier.fillMaxWidth(),
//                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
//                label = { Text("Enter description") }
//            )
//        }
//    }
//}
@Composable
fun FirstStep(navController: NavController, viewModel: CommunityViewModel = viewModel()) {
    val communityName by viewModel.communityName.observeAsState("")
    val description by viewModel.communityDescription.observeAsState("")

    var isFirstAttempt by remember { mutableStateOf(true) }

    val isButtonEnabled = communityName.isNotBlank() && description.length >= 15
    val isDescriptionValid = description.length >= 15 || isFirstAttempt

    Scaffold(
        topBar = {
            TopBarCreateCommunity(
                navController = navController,
                textNumber = "1 of 4",
                onClick = { if (isButtonEnabled) navController.navigate("SecondStep Page") },
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
            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = stringResource(id = R.string.CreateCommunityHeaderFirstPage),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(70.dp))

            // Community Name Input
            Text(
                text = "Community Name *",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )

            OutlinedTextField(
                value = communityName,
                onValueChange = { viewModel.updateCommunityName(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp),
                label = { Text("r/Community", color = MaterialTheme.colorScheme.secondary) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Description Input
            Text(
                text = "Description *",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )

            OutlinedTextField(
                value = description,
                onValueChange = {
                    viewModel.updateCommunityDescription(it)
                    isFirstAttempt = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
                label = { Text("Enter description", color = MaterialTheme.colorScheme.secondary) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isDescriptionValid) MaterialTheme.colorScheme.primary else Color.Red,
                    unfocusedBorderColor = if (isDescriptionValid) MaterialTheme.colorScheme.onSurfaceVariant else Color.Red
                )
            )

            if (!isDescriptionValid) {
                Text(
                    text = "Description must be at least 15 characters.",
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewFirstStepCreateCommunity() {
    FirstStep(navController = rememberNavController())
}