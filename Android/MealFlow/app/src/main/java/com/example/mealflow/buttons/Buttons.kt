package com.example.mealflow.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//-----------------------------------------
//-----------------------------------------
//Button
@Composable
fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(20.dp)
            .size(56.dp)  // تحديد الحجم الدائري
            .background(Color(0xFF009951), CircleShape) // اللون الأخضر والشكل الدائري
            .border(1.dp, Color.Black, CircleShape)// حدود سوداء
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "رجوع",
            tint = Color.White
        )
    }
}

@Composable
fun FixedButton(onClick: () -> Unit, textOnButton: String, modifier: Modifier = Modifier) {
    Button(
        onClick = { /*TODO*/ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .height(50.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(50.dp))// حدود سوداء
        , shape = RoundedCornerShape(50.dp), // تقليل انحناء الحواف هنا أيضًا
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009951))
    ) {
        Text(
            text = textOnButton,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White // لون النص الأسود
        )
    }
}

@Composable
fun DynamicButton(
    onClick: () -> Unit, textOnButton: String,
    modifier: Modifier = Modifier,
    buttonWidthDynamic: Int
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(buttonWidthDynamic.dp)
            .padding(20.dp)
            .height(50.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(50.dp))// حدود سوداء
        , shape = RoundedCornerShape(50.dp), // تقليل انحناء الحواف هنا أيضًا
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009951))
    ) {
        Text(
            text = textOnButton,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White // لون النص الأسود
        )
    }
}
// In a file like components/Buttons.kt
@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    text: String
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
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
            Text(text = text)
        }
    }
}

@Composable
fun SecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    text: String
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f)
        ),
        enabled = enabled && !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onSecondary,
                strokeWidth = 2.dp
            )
        } else {
            Text(text = text)
        }
    }
}

//-------------------------------------------------------
//-------------------------------------------------------
//----------------------------------------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewButton() {
    Column {
        DynamicButton(onClick = { /*TODO*/ }, textOnButton = "Sign in", buttonWidthDynamic = 200)
        FixedButton(onClick = { /*TODO*/ }, "Log in")
    }
}