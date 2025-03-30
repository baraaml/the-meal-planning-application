package com.example.mealflow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterInput1()
{
    var text by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    // Input field
    OutlinedTextField(
        value = text,
        onValueChange = { newText -> text = newText },
        label = { Text("Add an Email") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 20.dp, end = 20.dp)
    )

    // حقل كلمة السر
    OutlinedTextField(
        value = password,
        onValueChange = { newPassword -> password = newPassword },
        label = { Text("Enter your paswoed") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 20.dp, end = 20.dp),
        singleLine = true,
        textStyle = TextStyle(color = Color.Black), // تحديد لون النص
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
    )
    // حقل كلمة السر
    OutlinedTextField(
        value = repassword,
        onValueChange = { newPassword -> repassword = newPassword },
        label = { Text("Re-Enter your paswoed") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        singleLine = true,
        textStyle = TextStyle(color = Color.Black), // تحديد لون النص
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
    )
    Row {
        Checkbox(
            checked = passwordVisible,
            onCheckedChange = { passwordVisible = it },
            modifier = Modifier.padding(start = 5.dp)
        )
        //Text(text = "Show Password", modifier = Modifier.padding(start = 8.dp))
        Text(text = "show password", Modifier.padding(top = 15.dp))
    }
}

@Composable
fun Card()
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(20)
            ) // نصف دائري في الأعلى
            .background(Color.Gray)
            .padding(16.dp)
    )
    {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween // لضبط النصوص في الأطراف
        ){
            Text(text = "My Communities", modifier = Modifier
                .padding(start = 10.dp),
                color = Color.White
            )
            Text(text = "See all", modifier = Modifier
                .padding(end = 10.dp),
                color = Color.White
            )
        }
    }
}
// حقل كلمة السر
//OutlinedTextField(
//value = password,
//onValueChange = { newPassword -> password = newPassword },
//label = { Text("Enter your paswoed") },
//modifier = Modifier
//.fillMaxWidth()
//.padding(start = 20.dp, top = 20.dp, end = 20.dp),
//singleLine = true,
//textStyle = TextStyle(color = Color.Black), // تحديد لون النص
//visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
//)


//@Composable
//fun PostCommunity()
//{
//    Box(modifier = Modifier.fillMaxWidth())
//    {
//        Column {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Image(
//                        painter = painterResource(R.drawable.apple_logo_icon),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(40.dp)
//                            .clip(CircleShape)
//                            .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(
//                        text = "Community Name",
//                        textAlign = TextAlign.Center,
//                        style = MaterialTheme.typography.bodyLarge
//                    )
//                }
//                Button(
//                    onClick = { /*TODO*/ },
////                    modifier = Modifier
////                    .size(24.dp)
//                ) {
//                    Text(text = "Join")
//                }
//            }
//            Text(text ="Header")
//            Text(text = "subHeader")
//            Image(
//                painter = painterResource(id = R.drawable.apple_logo_icon),
//                contentDescription = null,
//                Modifier.fillMaxWidth()
//            )
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Button(
//                    onClick = { /*TODO*/ },
//                    modifier = Modifier.padding(8.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.up_arrow_angle_icon),
//                        contentDescription = "Join Icon",
//                        modifier = Modifier.size(20.dp)
//                    )
//                    Spacer(modifier = Modifier.width(8.dp)) // مسافة بين الأيقونة والنص
//                    Text(text = "123")
//                }
//                Button(
//                    onClick = { /*TODO*/ },
//                    modifier = Modifier.padding(8.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.scroll_arrow_to_down),
//                        contentDescription = "Join Icon",
//                        modifier = Modifier.size(20.dp)
//                    )
//                }
//            }
//        }
//    }
//}


//@Composable
//fun CardCommunity(communityName: String, members: Int, recipes: Int) {
////    val painter = painterResource(R.drawable.oip) // الحل النهائي
//    Column(
//        modifier = Modifier.padding(8.dp)
//    ) {
//        Box(
//            modifier = Modifier
//                .height(200.dp)
//                .width(200.dp)
//                .clip(RoundedCornerShape(20))
//                .background(Color.Gray)
//        ) {
//            Image(
//                painter = painterResource(R.drawable.oip),
//                contentDescription = null,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .clip(RoundedCornerShape(10))
//            )
//            DynamicButton(onClick = { /*TODO*/ }, textOnButton = "join", buttonWidthDynamic = 150, modifier = Modifier.align(Alignment.TopEnd))
////            Button(
////                onClick = { /* انضمام للمجتمع */ },
////                modifier = Modifier
////                    .align(Alignment.TopEnd)
////                    .size(100.dp)
////            ) {
////                Text(text = "Join", style = MaterialTheme.typography.labelSmall)
////            }
//        }
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = communityName, style = MaterialTheme.typography.titleMedium)
//        Row {
//            Text(text = "$members Members", style = MaterialTheme.typography.bodySmall)
//            Spacer(modifier = Modifier.width(8.dp))
//            Text(text = "$recipes Recipes", style = MaterialTheme.typography.bodySmall)
//        }
//    }
//}

//@Composable
//fun PostCommunity() {
//    androidx.compose.material3.Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(12.dp),
//        shape = RoundedCornerShape(12.dp),
//        elevation = CardDefaults.cardElevation(6.dp)
//    ) {
//        Column(
//            modifier = Modifier.padding(12.dp)
//        ) {
//            // العنوان + الأيقونة + زر الانضمام
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Image(
//                        painter = painterResource(R.drawable.apple_logo_icon),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(50.dp)
//                            .clip(CircleShape)
//                            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(
//                        text = "Community Name",
//                        textAlign = TextAlign.Center,
//                        style = MaterialTheme.typography.titleMedium
//                    )
//                }
//                Button(
//                    onClick = { /*TODO*/ },
//                    shape = RoundedCornerShape(8.dp)
//                ) {
//                    Text(text = "Join")
//                }
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // العناوين الفرعية
//            Text(
//                text = "Header",
//                style = MaterialTheme.typography.titleLarge,
//                color = MaterialTheme.colorScheme.primary
//            )
//            Text(
//                text = "subHeader",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // صورة المنشور
//            Image(
//                painter = painterResource(id = R.drawable.oip__1_),
//                contentDescription = null,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .aspectRatio(16f / 9f)
//                    .clip(RoundedCornerShape(8.dp))
//                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // أزرار التفاعل
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Button(
//                    onClick = { /*TODO*/ },
//                    shape = RoundedCornerShape(8.dp),
//                    //colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.up_arrow_angle_icon),
//                        contentDescription = "Upvote Icon",
//                        modifier = Modifier.size(20.dp)
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(text = "123")
//                }
//
////                Button(
////                    onClick = { /*TODO*/ },
////                    shape = RoundedCornerShape(8.dp),
////                    //colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
////                ) {
////                    Icon(
////                        painter = painterResource(id = R.drawable.scroll_arrow_to_down),
////                        contentDescription = "Downvote Icon",
////                        modifier = Modifier.size(20.dp)
////                    )
////                }
//                Button(
//                    onClick = { /*TODO*/ },
//                    shape = RoundedCornerShape(8.dp),
//                    modifier = Modifier.size(36.dp), // جعل حجم الزر مساوٍ لحجم الأيقونة تقريبًا
//                    contentPadding = PaddingValues(0.dp), // إزالة الحشو الداخلي
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.scroll_arrow_to_down),
//                        contentDescription = "Downvote Icon",
//                        modifier = Modifier.size(24.dp) // حجم الأيقونة
//                    )
//                }
//
//
//                // زر التعليقات
//                Button(
//                    onClick = { /*TODO: فتح التعليقات */ },
//                    shape = RoundedCornerShape(8.dp),
//                    //colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.bubble_comment_talk_icon), // ضع أيقونة التعليق هنا
//                        contentDescription = "Comment Icon",
//                        modifier = Modifier.size(20.dp)
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(text = "Comment")
//                }
//            }
//        }
//    }
//}