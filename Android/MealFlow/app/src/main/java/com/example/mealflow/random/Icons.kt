package com.example.mealflow.random

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mealflow.R

//----------------------------------------------------------------------------------------
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
                .size(44.dp)  // Specify the circular size
                .border(1.dp, Color.Black, CircleShape)
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
                .size(44.dp)  // Specify the circular size
                .border(1.dp, Color.Black, CircleShape)
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
                .size(44.dp) // Specify the circular size
                .border(1.dp, Color.Black, CircleShape)
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
        contentDescription = "Custom icon",
        tint = Color.Black, // To keep the original image colors
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
        painter = painterResource(id = R.drawable.community_icon), // Replace with the actual icon name
        onClick = { println("Icon Clicked!") },
        modifier = Modifier.padding(20.dp)
    )
}

@Composable
fun BottomBar(navController: NavController, modifier: Modifier = Modifier) {
    NavigationBar(
        modifier = modifier
            .height(56.dp)
            .shadow(6.dp, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
        tonalElevation = 6.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        val items = listOf(
            "Home Page" to Icons.Outlined.Home,
            "Market Page" to Icons.Outlined.AddShoppingCart,
            "Search Page" to Icons.Outlined.Search,
            "Community Page" to Icons.Outlined.Groups,
            "Planner Page" to Icons.Outlined.CalendarMonth
        )

        val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { (route, icon) ->
            val isSelected = currentDestination == route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = icon,  // Changed from painterResource to imageVector
                        contentDescription = route,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer {
                                scaleX = if (isSelected) 1.1f else 1f
                                scaleY = if (isSelected) 1.1f else 1f
                            }
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                },
                alwaysShowLabel = false
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BottomBarPreview() {
    BottomBar(navController = rememberNavController())
}
