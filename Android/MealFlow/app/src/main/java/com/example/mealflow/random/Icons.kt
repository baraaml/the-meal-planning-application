package com.example.mealflow.random

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
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
@Composable
fun IconWithClickable(painter: Painter, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Icon(
        painter = painter,
        contentDescription = "أيقونة مخصصة",
        tint = Color.Black, // لإبقاء ألوان الصورة الأصلية
        modifier = modifier
            .size(40.dp)
            .clickable { onClick() }
            .padding(10.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ViewLoginIcons() {
    IconWithClickable(
        painter = painterResource(id = R.drawable.community_icon), // استبدل باسم الأيقونة الفعلي
        onClick = { println("Icon Clicked!") },
        modifier = Modifier.padding(20.dp)
    )
}

@Composable
fun BottomBar(modifier: Modifier = Modifier)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .drawBehind {
                drawLine(
                    color = Color.Black, // لون الحد
                    start = Offset(0f, 0f), // بداية الخط (أعلى يسار)
                    end = Offset(size.width, 0f), // نهاية الخط (أعلى يمين)
                    strokeWidth = 4.dp.toPx() // سمك الحد
                )
            }, // إضافة لون خلفية لاختبار التصميم
        horizontalArrangement = Arrangement.SpaceEvenly // توزيع الأيقونات بالتساوي
    ) {
        IconWithClickable(painter = painterResource(id = R.drawable.home_icon), onClick = {},
            modifier = Modifier.weight(1f))
        IconWithClickable(painter = painterResource(id = R.drawable.market_icon), onClick = {},
            modifier = Modifier.weight(1f))
        IconWithClickable(painter = painterResource(id = R.drawable.search_zoom), onClick = {},
            modifier = Modifier.weight(1f))
        IconWithClickable(painter = painterResource(id = R.drawable.community_icon), onClick = {},
            modifier = Modifier.weight(1f))
        IconWithClickable(painter = painterResource(id = R.drawable.planner_icon), onClick = {},
            modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BottomBarPreview() {
    Box{
        Row(verticalAlignment = Alignment.Bottom)
        {
            BottomBar()
        }
    }
}