package com.example.mealflow.random

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mealflow.R

//--------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RowLoginIcons() {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    ) {
        IconButton(
            onClick = {/*TODO*/ },
            modifier = Modifier
                .padding(10.dp)
                .size(44.dp)  // تحديد الحجم الدائري
                .border(1.dp, Color.Black, CircleShape)// حدود سوداء
        ) {
            Icon(
                painter = painterResource(id = R.drawable.google_icon_icons_com_62736),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(30.dp)
            )
        }
        IconButton(
            onClick = {/*TODO*/ },
            modifier = Modifier
                .padding(10.dp)
                .size(44.dp)  // تحديد الحجم الدائري
                .border(1.dp, Color.Black, CircleShape)// حدود سوداء
        ) {
            Icon(
                painter = painterResource(id = R.drawable.facebook_logo_icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(30.dp)
            )
        }
        IconButton(
            onClick = {/*TODO*/ },
            modifier = Modifier
                .padding(10.dp)
                .size(44.dp)  // تحديد الحجم الدائري
                .border(1.dp, Color.Black, CircleShape)// حدود سوداء
        ) {
            Icon(
                painter = painterResource(id = R.drawable.apple_logo_icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ViewRowLoginIcons() {
    RowLoginIcons()
}