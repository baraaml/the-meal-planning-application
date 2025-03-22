package com.example.mealflow.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF7BC24D),          // Soft Green
    onPrimary = Color.White,
    secondary = Color(0xFFF2EDE4),        // Light Tan/Beige
    onSecondary = Color.Black,
    tertiary = Color(0xFFFF9D42),         // Warm Orange
    background = Color.White,
    surface = Color(0xFFF9F9F9),
    error = Color(0xFFFF5A5A)            // Soft Pink/Coral for errors too
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF71EB37),          // Keep primary the same for brand recognition
    onPrimary = Color.Black,              // Invert text color for dark theme
    secondary = Color(0xFF3E3830),        // Darker version of tan
    onSecondary = Color.White,
    tertiary = Color(0xFFFFB266),         // Lighter orange for dark theme
    background = Color(0xFF121212),
    surface = Color(0xFF242424),
    error = Color(0xFFFF7A7A)
)
@Composable
fun MealFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}



@Composable
fun ThemedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    text: String
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        ),
        enabled = enabled && !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
