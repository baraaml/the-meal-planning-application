package com.example.mealflow.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mealflow.ui.components.LanguageSwitcher

@Composable
fun MarketPage(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .systemBarsPadding() // ✅ Adds space so that the content doesn't overlap the notification bar
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