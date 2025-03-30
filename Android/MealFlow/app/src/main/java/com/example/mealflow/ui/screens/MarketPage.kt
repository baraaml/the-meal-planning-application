package com.example.mealflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mealflow.random.BottomBar
import com.example.mealflow.ui.components.LanguageSwitcher
import com.example.mealflow.viewModel.HomeViewModel

@Composable
fun MarketPage(navController: NavController, viewModel: HomeViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .systemBarsPadding() // ✅ يضيف مسافة بحيث لا يتداخل المحتوى مع شريط الإشعارات
            .padding(16.dp)
    ) {
        SectionTitle(title = "Market Page")
        LanguageSwitcher()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMarketScreen() {
    MarketPage(navController = rememberNavController())
}