package com.example.mealflow.ui.components

import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mealflow.R
import java.util.Locale

@Composable
fun LanguageSwitcher() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    var isArabic by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isArabic) "مرحبا بك!" else "Welcome!",
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = {
            isArabic = !isArabic
            updateLocale(context.resources, if (isArabic) "ar" else "en")
        }) {
            Text(text = context.getString(R.string.change_language))
        }
    }
}

fun updateLocale(resources: Resources, languageCode: String) {
    val locale = Locale(languageCode)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
}

@Preview
@Composable
fun View()
{
    LanguageSwitcher()
}