package com.example.mealflow.network

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewScreen() {
    //CommunityPage(navController = rememberNavController())
    //Card()
    //CardCommunity()
}

//@Composable
//fun CardCommunity()
//{
//    Column {
//        Box(
//            modifier = Modifier
//                .height(200.dp)
//                .width(200.dp)
//                .clip(
//                    RoundedCornerShape(20)
//                ) // نصف دائري في الأعلى
//                .background(Color.Gray)
//        )
//        {
//            Image(
//                painter = painterResource(R.drawable.apple_logo_icon),
//                contentDescription = null,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .clip(RoundedCornerShape(10))
//                //.border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape),
//            )
//            Button(
//                onClick = { /*TODO*/ },
//                Modifier.align(Alignment.TopEnd)
//                    .size(100.dp)
//            ) {
//                Text(text = "Join")
//            }
//        }
//        Text(text = "Soup and Spoon")
//        Row {
//            Text(text = "108 Members")
//            Text(text = "108 recipes")
//        }
//    }
//}

//@Composable
//fun CommunityList(list: listOf) {
//    val communities =
//    )
//
//    LazyRow(
//        modifier = Modifier.fillMaxWidth(),
//        contentPadding = PaddingValues(horizontal = 16.dp),
//        horizontalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        items(communities) { community ->
//            CardCommunity(
//                communityName = community.first,
//                members = community.second,
//                recipes = community.third
//            )
//        }
//    }
//}

//@Composable
//fun TopBar()
//{
//    Row {
//        Image(
//            painter = painterResource(R.drawable.apple_logo_icon),
//            contentDescription = null,
//            modifier = Modifier
//                .size(40.dp)
//                .clip(CircleShape)
//                .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
//        )
//        Text(
//            text = "Hello, Abdelrahman",
//            textAlign = TextAlign.Center
//        )
//    }
//}
