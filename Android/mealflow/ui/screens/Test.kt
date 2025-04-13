package com.example.mealflow.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mealflow.R


//@Composable
//fun ImageWithThinkingBubble() {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black)
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.mealflow), // استبدل your_image بالصورة الفعلية
//            contentDescription = "Background Image",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.fillMaxSize()
//        )
//
//        Box(
//            modifier = Modifier
//                .align(Alignment.Center)
//                .padding(16.dp)
//        ) {
//            ThinkingBubble(
//                text = "هل يمكن للذكاء الاصطناعي أن يفكر؟",
//                bubbleWidth = 300.dp, // تحكم في عرض الفقاعة
//                bubbleHeight = 150.dp, // تحكم في ارتفاع الفقاعة
//                textSize = 20.sp, // تحكم في حجم النص
//                borderWidth = 5.dp, // تحكم في سماكة الحد
//                tailSize = 15.dp // تحكم في حجم الذيل
//            )
//        }
//    }
//}
//
//@Composable
//fun ThinkingBubble(
//    text: String,
//    bubbleWidth: Dp = 250.dp,
//    bubbleHeight: Dp = 120.dp,
//    bubbleRadius: Dp = 25.dp,
//    borderWidth: Dp = 5.dp,  // زودنا قيمة الحد ليكون أوضح
//    tailSize: Dp = 15.dp,
//    textSize: TextUnit = 18.sp
//) {
//    Canvas(modifier = Modifier.size(bubbleWidth, bubbleHeight + 30.dp)) { // 30.dp إضافية للذيل
//        val bubbleColor = Color.White
//        val borderColor = Color.Black
//
//        val path = Path().apply {
//            // رسم الفقاعة الرئيسية
//            addRoundRect(
//                RoundRect(
//                    rect = Rect(Offset(borderWidth.toPx() / 2, borderWidth.toPx() / 2), // تحريك المسار لتجنب القطع
//                        size.copy(
//                            height = size.height - 30.dp.toPx() - borderWidth.toPx(),
//                            width = size.width - borderWidth.toPx()
//                        )
//                    ),
//                    cornerRadius = CornerRadius(bubbleRadius.toPx(), bubbleRadius.toPx())
//                )
//            )
//
//            // رسم الذيل باستخدام 3 دوائر متدرجة
//            addOval(Rect(Offset(size.width * 0.7f, size.height - 28.dp.toPx()), Size(tailSize.toPx(), tailSize.toPx())))
//            addOval(Rect(Offset(size.width * 0.8f, size.height - 18.dp.toPx()), Size((tailSize * 0.75f).toPx(), (tailSize * 0.75f).toPx())))
//            addOval(Rect(Offset(size.width * 0.85f, size.height - 10.dp.toPx()), Size((tailSize * 0.5f).toPx(), (tailSize * 0.5f).toPx())))
//        }
//
//        // رسم الفقاعة البيضاء أولاً
//        drawPath(path, color = bubbleColor, style = Fill)
//
//        // رسم الحد الأسود حول الفقاعة بالكامل
//        drawPath(
//            path,
//            color = borderColor,
//            style = androidx.compose.ui.graphics.drawscope.Stroke(
//                width = borderWidth.toPx(),
//                join = StrokeJoin.Round // يجعل الزوايا ناعمة
//            )
//        )
//    }
//
//    Box(
//        modifier = Modifier
//            .size(bubbleWidth, bubbleHeight)
//            .padding(16.dp)
//            .background(Color.Transparent),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = text,
//            fontSize = textSize,
//            fontWeight = FontWeight.Bold,
//            textAlign = TextAlign.Center,
//            color = Color.Black
//        )
//    }
//}
@Composable
fun ThinkingBubbleWithImage() {
    Box(
        modifier = Modifier
            .size(150.dp) // حجم المربع الكلي
            .background(Color.White) // خلفية بيضاء
            //.border(2.dp, Color.Black) // حدود للمربع
    ) {
        // تقسيم المربع إلى 2x2 (باستخدام Column و Row)
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.weight(1f)) {
                // فقاعة التفكير (في الأعلى يسار)
                Box(
                    modifier = Modifier
                        .weight(1f) // تأخذ نصف العرض
                        .fillMaxHeight() // تأخذ كل الارتفاع المتاح
                        .padding(8.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    RoundedCornerBox()
                }

                Spacer(modifier = Modifier.weight(1f)) // المساحة الفارغة يمين الفقاعة
            }

            Row(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.weight(1f)) // المساحة الفارغة يسار الصورة

                // الصورة في الأسفل يمين
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(8.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.android_robot), // استبدل بالصورة الفعلية
                        contentDescription = "Thinking Object",
                        modifier = Modifier.fillMaxSize() // حجم الصورة
                    )
                }
            }
        }
    }
}

// دالة لإنشاء فقاعة التفكير
@Composable
fun ThoughtBubble() {
    Box(
        modifier = Modifier
            .size(150.dp, 100.dp)
            .fillMaxSize()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val borderWidth = 4.dp.toPx()
            drawOval(
                color = Color.Black, // الحد الأسود
                topLeft = Offset(borderWidth / 2, borderWidth / 2),
                size = Size(size.width - borderWidth, size.height - borderWidth),
                style = Stroke(width = borderWidth)
            )
            drawOval(
                color = Color.White, // الفقاعة البيضاء
                topLeft = Offset(borderWidth, borderWidth),
                size = Size(size.width - borderWidth * 2, size.height - borderWidth * 2)
            )
        }

        // نص داخل الفقاعة
        Text(
            text = "Text dddddddddddddddd",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )

        // رسم الذيل (دوائر صغيرة)
        Canvas(
            modifier = Modifier
                .size(50.dp, 30.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = 30.dp) // وضع الذيل تحت الفقاعة
        ) {
            drawCircle(color = Color.Black, radius = 6.dp.toPx(), center = Offset(0f, 25.dp.toPx()))
            drawCircle(color = Color.Black, radius = 4.dp.toPx(), center = Offset(15.dp.toPx(), 40.dp.toPx()))
            drawCircle(color = Color.Black, radius = 3.dp.toPx(), center = Offset(30.dp.toPx(), 50.dp.toPx()))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun s()
{
    ThinkingBubbleWithImage()
}
@Composable
fun RoundedCornerBox() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp)) // زوايا دائرية
            .background(Color.White)
            .padding(16.dp) // هامش داخلي
            .border(2.dp, Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "نص داخل المربع",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Preview
@Composable
fun PreviewRoundedCornerBox() {
    RoundedCornerBox()
}
//@Composable
//fun TutorialScreen() {
//    var textIndex by remember { mutableStateOf(0) }
//    val tutorialTexts = listOf(
//        "مرحبًا! أنا روبوت مساعدك الشخصي 🤖",
//        "اضغط على الشاشة لتغيير النص!",
//        "هكذا يمكنك التنقل بين الشروحات بسهولة!",
//        "جرب إضافتي إلى تطبيقك!"
//    )
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//            .clickable {
//                textIndex = (textIndex + 1) % tutorialTexts.size
//            },
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = tutorialTexts[textIndex],
//            fontSize = 20.sp,
//            fontWeight = FontWeight.Bold,
//            color = Color.Black,
//            textAlign = TextAlign.Center,
//            modifier = Modifier.padding(16.dp)
//        )
//
//        Image(
//            painter = painterResource(id = R.drawable.batman), // ضع صورة الروبوت هنا
//            contentDescription = "Robot",
//            modifier = Modifier.size(150.dp)
//        )
//    }
//}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun se()
{
    TutorialScreen()
}
@Composable
fun TutorialScreen(
    tutorialSize: Dp = 100.dp,  // حجم الـ Tutorial كمدخل
    tutorialTexts: List<String> = listOf(
        "مرحبًا! أنا روبوت مساعدك الشخصي 🤖",
        "اضغط على الشاشة لتغيير النص!",
        "هكذا يمكنك التنقل بين الشروحات بسهولة!",
        "جرب إضافتي إلى تطبيقك!"
    ),
    fontSizeRatio: Float = 0.1f, // نسبة حجم النص مقارنة بحجم الـ tutorial
    imageSizeRatio: Float = 0.5f // نسبة حجم الصورة مقارنة بحجم الـ tutorial
) {
    var textIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .size(tutorialSize) // حجم الـ tutorial يتغير بناءً على المدخل
            .background(Color.White)
            .clickable {
                textIndex = (textIndex + 1) % tutorialTexts.size
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = tutorialTexts[textIndex],
            fontSize = (tutorialSize.value * fontSizeRatio).sp, // حجم النص يتناسب مع حجم الـ tutorial
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.batman), // ضع صورة الروبوت هنا
            contentDescription = "Robot",
            modifier = Modifier.size(tutorialSize * imageSizeRatio) // حجم الصورة يتناسب مع حجم الـ tutorial
        )
    }
}
