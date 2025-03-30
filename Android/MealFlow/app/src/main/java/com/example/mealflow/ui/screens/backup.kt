package com.example.mealflow.ui.screens

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//@Composable
//fun CardCommunityTest(communityName: String, members: Int, recipes: Int, imageUri: Uri?) {
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
//            if (imageUri != null) {
//                Image(
//                    painter = rememberAsyncImagePainter(imageUri),
//                    contentDescription = null,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .clip(RoundedCornerShape(10))
//                )
//            } else {
//                // صورة افتراضية إذا لم يتم اختيار صورة
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(Color.DarkGray),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text("No Image", color = Color.White)
//                }
//            }
//            DynamicButton(
//                onClick = { /*TODO: انضمام للمجتمع */ },
//                textOnButton = "Join",
//                buttonWidthDynamic = 150,
//                modifier = Modifier.align(Alignment.TopEnd)
//            )
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
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//package com.example.mealflow
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navArgument
//import androidx.navigation.navDeepLink
//import com.example.mealflow.data.model.Ingredient
//import com.example.mealflow.data.model.Interactions
//import com.example.mealflow.data.model.Meal
//import com.example.mealflow.data.model.Note
//import com.example.mealflow.data.model.User
//import com.example.mealflow.ui.screens.CommunityPage
//import com.example.mealflow.ui.screens.HomePage
//import com.example.mealflow.ui.screens.PlannerPage
//import com.example.mealflow.ui.screens.SearchPage
//import com.example.mealflow.ui.theme.MealFlowTheme
//import com.example.mealflow.ui.screens.FirstStep
//import com.example.mealflow.ui.screens.ForgetPasswordPage
//import com.example.mealflow.ui.screens.LoginPage
//import com.example.mealflow.ui.screens.MarketPage
//import com.example.mealflow.ui.screens.OtpPage
//import com.example.mealflow.ui.screens.RegisterPage
//import com.example.mealflow.ui.screens.ResetPasswordPage
//import com.example.mealflow.ui.screens.SecondStep
//import com.example.mealflow.ui.screens.StartPage
//import com.example.mealflow.ui.screens.TestPage
//import com.example.mealflow.viewModel.CommunityViewModel
//import com.example.mealflow.viewModel.MealViewModel
//import com.example.mealflow.viewModel.RegisterViewModel
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        setContent {
//            MealFlowTheme {
//                val navController = rememberNavController()
//
//                // 🔥 التعامل مع Deep Link عند تشغيل التطبيق
//                LaunchedEffect(intent?.data) {
//                    intent?.data?.getQueryParameter("token")?.let { token ->
//                        Log.d("MainActivity", "🔹 Token received: $token")
//                        navController.navigate("Reset Password Page?token=$token")
//                    }
//                }
//                AppNavHost(navController)
//            }
//        }
//    }
//}
//
////----------------------------------------------------------------------------
//// Function to move between pages
//@Composable
//fun AppNavHost(navController: NavHostController) {
//    val registerViewModel: RegisterViewModel = viewModel()
//    val communityViewModel: CommunityViewModel = viewModel()
//    val mealViewModel: MealViewModel = viewModel()
//
//    val sampleMeals = listOf(
//        Meal(
//            mealId = "1",
//            name = "Spaghetti",
//            description = "Delicious spaghetti with tomato sauce",
//            imageUrl = "https://via.placeholder.com/150",
//            tags = listOf("Italian", "Pasta"),
//            ingredients = listOf(
//                Ingredient(name = "Spaghetti", quantity = 200.0, unit = "grams"),
//                Ingredient(name = "Tomato Sauce", quantity = 100.0, unit = "ml")
//            ),
//            instructions = listOf("Boil water", "Add pasta", "Cook sauce", "Mix together"),
//            cookware = listOf("Pot", "Pan"),
//            preparationTime = 10,
//            cookingTime = 15,
//            servings = 2,
//            caloriesPerServing = 350,
//            rating = 4.5,
//            reviewsCount = 100,
//            createdBy = User(userId = "user_123", username = "ChefMaster"),
//            isFavorited = true,
//            isSaved = false,
//            notes = listOf(
//                Note(
//                    noteId = "note_1",
//                    user = User(userId = "user_456", username = "Foodie123"),
//                    comment = "Loved it!",
//                    didCook = true,
//                    likes = 10,
//                    dislikes = 1
//                )
//            ),
//            interactions = Interactions(views = 500, likes = 50, dislikes = 2, shares = 5),
//            createdAt = "2023-01-01",
//            updatedAt = "2023-01-02"
//        )
//    )
//
//    NavHost(navController = navController, startDestination = "Start Page") {
//        composable("Start Page") {
//            StartPage(navController)
//        }
//        composable("Home Page") {
//            //HomePage(navController)
//
//            HomePage(
//                meals = sampleMeals,
//                userName = "Abdelrahman",
//                onMealClick = { /* Do nothing for preview */ }
//            )
//        }
//        composable("Register Page") {
//            RegisterPage(navController,registerViewModel)
//        }
//        composable("Login Page") {
//            LoginPage(navController)
//        }
//        composable("Otp Page") {
//            OtpPage(navController,registerViewModel)
//        }
//        composable("Test Page") {
//            TestPage(navController)
//        }
//        composable("Forget Password Page") {
//            ForgetPasswordPage(navController)
//        }
//        composable("Test Page") {
//            TestPage(navController)
//        }
//
//        composable("Community Page") {
//            CommunityPage(navController,communityViewModel)
//        }
//        composable("Market Page") {
//            MarketPage(navController)
//        }
//        composable("Planner Page") {
//            PlannerPage(navController)
//        }
//        composable("Search Page") {
//            //SearchPage(navController)
//            SearchPage(sampleMeals, {}, mealViewModel, navController)
//        }
//        // 🔹 صفحة إعادة تعيين كلمة المرور مع دعم الـ Deep Link
//        composable(
//            route = "Reset Password Page?token={token}",
//            arguments = listOf(navArgument("token") { nullable = true }),
//            deepLinks = listOf(navDeepLink { uriPattern = "https://iiacbca.r.bh.d.sendibt3.com/tr/cl?token={token}" })
//        ) { backStackEntry ->
//            val token = backStackEntry.arguments?.getString("token")
//            ResetPasswordPage(navController, token)
//        }
//        composable("FirstStep Page") {
//            FirstStep(navController,communityViewModel)
//        }
//        composable("SecondStep Page") {
//            SecondStep(navController,communityViewModel)
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun MyApp() {
//    val navController = rememberNavController()
//    AppNavHost(navController = navController)
//}
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//@Composable
//fun CommunityPage(navController: NavController, viewModel: CommunityViewModel) {
//
//    Scaffold(
//        topBar = { TopBar()},
//        bottomBar = { BottomBar(navController) },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { navController.navigate("FirstStep Page") },
//                modifier = Modifier.padding(16.dp),
//                containerColor = MaterialTheme.colorScheme.primary
//            ) {
//                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة")
//            }
//        },
//        containerColor = Color(0xFFEFEFEF),
//        modifier = Modifier.navigationBarsPadding()
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .systemBarsPadding()
//                .padding(16.dp)
//        ) {
//            SearchBar()
//            LazyColumn(
//                modifier = Modifier.fillMaxWidth(),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                items(1) {
//                    CommunityList(
//                        listOf(
//                            Triple("Soup and Spoon", 108, 25),
//                            Triple("Healthy Eats", 200, 50),
//                            Triple("Fast & Tasty", 150, 30),
//                            Triple("Vegan Delights", 90, 20)
//                        )
//                    )
//                    PostCommunity()
//                    PostUser()
//                    PostUser()
//                    PostUser()
//                    PostUser()
//                    PostCommunity()
//                    PostCommunity()
//                    PostCommunity()
//                    PostCommunity()
//                    PostUser()
//                }
//            }
//        }
//    }
//}
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//package com.example.mealflow.ui.screens
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.navigationBarsPadding
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.systemBarsPadding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material.icons.rounded.Menu
//import androidx.compose.material.icons.rounded.Notifications
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import androidx.navigation.compose.rememberNavController
//import com.example.mealflow.R
//import com.example.mealflow.random.BottomBar
//import com.example.mealflow.ui.screens.CommunityList
//import com.example.mealflow.ui.screens.PostCommunity
//import com.example.mealflow.ui.screens.PostUser
//import com.example.mealflow.viewModel.CommunityViewModel
//
//@Composable
//fun CommunityPage(navController: NavController, viewModel: CommunityViewModel) {
//
//    Scaffold(
//        //topBar = { /* نفس `TopBar` السابق */ },
//        //bottomBar = { BottomBar(navController) },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { navController.navigate("FirstStep Page") },
//                modifier = Modifier.padding(16.dp),
//                containerColor = MaterialTheme.colorScheme.primary
//            ) {
//                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة")
//            }
//        },
//        containerColor = Color(0xFFEFEFEF),
//        //modifier = Modifier.navigationBarsPadding()
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .systemBarsPadding()
//                .padding(16.dp)
//        ) {
//            TopBar()
//            SearchBar()
//            LazyColumn(
//                modifier = Modifier.fillMaxWidth(),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                items(1) {
//                    CommunityList(
//                        listOf(
//                            Triple("Soup and Spoon", 108, 25),
//                            Triple("Healthy Eats", 200, 50),
//                            Triple("Fast & Tasty", 150, 30),
//                            Triple("Vegan Delights", 90, 20)
//                        )
//                    )
//                    PostCommunity()
//                    PostUser()
//                    PostUser()
//                    PostUser()
//                    PostUser()
//                    PostCommunity()
//                    PostCommunity()
//                    PostCommunity()
//                    PostCommunity()
//                    PostUser()
//                }
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewCommunityScreen() {
//    val communityViewModel: CommunityViewModel = viewModel()
//    CommunityPage(navController = rememberNavController(), communityViewModel)
//}
//
//
//@Composable
//fun TopBar() {
//    Surface(
//        modifier = Modifier
//            .fillMaxWidth(),
////        color = MaterialTheme.colorScheme.surface,
//          color = Color.White,
//        tonalElevation = 3.dp
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 12.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(42.dp)
//                        .clip(CircleShape)
//                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
//                        .border(
//                            width = 2.dp,
//                            brush = Brush.linearGradient(
//                                colors = listOf(
//                                    MaterialTheme.colorScheme.secondary,
//                                    MaterialTheme.colorScheme.tertiary
//                                )
//                            ),
//                            shape = CircleShape
//                        )
//                ) {
//                    Image(
//                        painter = painterResource(R.drawable.apple_logo_icon),
//                        contentDescription = "Profile Picture",
//                        modifier = Modifier
//                            .size(28.dp)
//                            .align(Alignment.Center),
//                        contentScale = ContentScale.Fit
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(12.dp))
//
//                Column {
//                    Text(
//                        text = "Hello,",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                    Text(
//                        text = "Abdelrahman",
//                        style = MaterialTheme.typography.titleMedium.copy(
//                            fontWeight = FontWeight.Bold
//                        ),
////                        color = MaterialTheme.colorScheme.onSurface
//                          color = Color.Black
//
//                    )
//                }
//            }
//
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(12.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // زر الإشعارات
//                IconButton(
//                    onClick = { /* Handle click */ },
//                    modifier = Modifier
//                        .size(36.dp)
//                        .clip(CircleShape)
//                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
//                ) {
//                    Icon(
//                        imageVector = Icons.Rounded.Notifications,
//                        contentDescription = "Notifications",
//                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
//                        modifier = Modifier.size(20.dp)
//                    )
//                }
//
//                // زر القائمة
//                IconButton(
//                    onClick = { /* Handle click */ },
//                    modifier = Modifier
//                        .size(36.dp)
//                        .clip(CircleShape)
//                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
//                ) {
//                    Icon(
//                        imageVector = Icons.Rounded.Menu,
//                        contentDescription = "Menu",
//                        tint = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.size(20.dp)
//                    )
//                }
//            }
//        }
//    }
//}
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SearchBar() {
//    var searchQuery by remember { mutableStateOf("") }
//
//    OutlinedTextField(
//        value = searchQuery,
//        onValueChange = { searchQuery = it },
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp)
//            .background(Color.White, shape = RoundedCornerShape(16.dp))
//            .shadow(4.dp, shape = RoundedCornerShape(16.dp)),
//        placeholder = { Text("Search", style = TextStyle(color = Color.Gray)) },
//        leadingIcon = {
//            Icon(Icons.Default.Search, contentDescription = "بحث", tint = Color.Gray)
//        },
//        shape = RoundedCornerShape(16.dp),
//        colors = TextFieldDefaults.outlinedTextFieldColors(
//            focusedBorderColor = Color.Transparent,
//            unfocusedBorderColor = Color.Transparent,
//            containerColor = Color.White
//        )
//    )
//}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewPostCommunity() {
//    val communityViewModel: CommunityViewModel = viewModel()
//    PostCommunity()
//}
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//@Composable
//fun TopBarCreateCommunity(navController: NavController,textnumber:String,onClick: () -> Unit) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(2.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.Center // يجعل العناصر في منتصف الصف أفقيًا
//    ) {
//        IconButton(
//            onClick = { navController.popBackStack() },
//            modifier = Modifier
//                .padding(20.dp)
//                .size(56.dp)  // تحديد الحجم الدائري
//            //.background(Color(0xFF009951), CircleShape) // اللون الأخضر والشكل الدائري
//            //.border(1.dp, Color.Black, CircleShape)// حدود سوداء
//        ) {
//            Icon(
//                imageVector = Icons.Default.ArrowBack,
//                contentDescription = "رجوع",
//                tint = Color.Black,
//                modifier = Modifier.fillMaxSize()
//            )
//        }
//        //BackButton(onClick = { navController.popBackStack() })
//
//        Spacer(modifier = Modifier.width(8.dp)) // مسافة صغيرة بين الزر والنص
//
//        Text(
//            text = textnumber,
//            modifier = Modifier
//                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
//                .padding(8.dp),
//            textAlign = TextAlign.Center
//        )
//
//        Spacer(modifier = Modifier.weight(1f)) // يدفع الزر التالي إلى اليمين
//
//        Button(
//            onClick = onClick,
//            modifier = Modifier
//                .wrapContentWidth() // العرض يتغير حسب النص
//                .wrapContentHeight() // الارتفاع يتغير حسب النص
//                //.width(100.dp)
//                .padding(20.dp)
//                //.height(50.dp)
//                .border(2.dp, Color.Black, RoundedCornerShape(50.dp))// حدود سوداء
//            , shape = RoundedCornerShape(50.dp), // تقليل انحناء الحواف هنا أيضًا
//            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009951))
//        ) {
//            Text(
//                text = "Next",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black // لون النص الأسود
//            )
//        }
//    }
//}
//@Composable
//fun TopBarCreateCommunity(navController: NavController, textnumber: String, onClick: () -> Unit, isButtonEnabled: Boolean) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(2.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.Center
//    ) {
//        IconButton(
//            onClick = { navController.popBackStack() },
//            modifier = Modifier
//                .padding(20.dp)
//                .size(56.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Default.ArrowBack,
//                contentDescription = "رجوع",
//                tint = Color.Black,
//                modifier = Modifier.fillMaxSize()
//            )
//        }
//
//        Spacer(modifier = Modifier.width(8.dp))
//
//        Text(
//            text = textnumber,
//            modifier = Modifier
//                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
//                .padding(8.dp),
//            textAlign = TextAlign.Center
//        )
//
//        Spacer(modifier = Modifier.weight(1f))
//
//        Button(
//            onClick = onClick,
//            enabled = isButtonEnabled, // ✅ تعطيل الزر إذا لم تكن البيانات مكتملة
//            modifier = Modifier
//                .wrapContentWidth()
//                .wrapContentHeight()
//                .padding(20.dp)
//                .border(2.dp, Color.Black, RoundedCornerShape(50.dp)),
//            shape = RoundedCornerShape(50.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = if (isButtonEnabled) Color(0xFF009951) else Color.Gray // ✅ تغيير اللون حسب حالة الإدخال
//            )
//        ) {
//            Text(
//                text = "Next",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black
//            )
//        }
//    }
//}
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------

//@Composable
//fun FirstStep(navController: NavController,viewModel: CommunityViewModel = viewModel())
//{
//    val communityName by viewModel.communityname.observeAsState("")
//    var description by remember { mutableStateOf("") }
//    Scaffold(
//        topBar = { /* نفس `TopBar` السابق */ },
//        bottomBar = { BottomBar(navController) },
//        containerColor = Color(0xFFEFEFEF), // ✅ تغيير لون الخلفية إلى لون رمادي فاتح (يمكن تغييره لأي لون آخر)
//        modifier = Modifier.navigationBarsPadding() // ✅ يمنع التداخل مع أزرار النظام
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .systemBarsPadding() // ✅ يضيف مسافة بحيث لا يتداخل المحتوى مع شريط الإشعارات
//                .padding(16.dp)
//        ) {
//            TopBarCreateCommunity(
//                navController = navController,
//                "1 of 4",
//                {navController.navigate("SecondStep Page")}
//            )
//            Spacer(modifier = Modifier.height(8.dp)) // مسافة صغيرة بين الزر والنص
//            Text(
//                text = "Tell us about you \nCommunity",
//                modifier = Modifier
//                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
//                    .padding(8.dp),
//                textAlign = TextAlign.Center,
//                fontSize = 35.sp
//            )
//            Text(
//                text = "A name and description help people understand\n what your community is all about",
//                modifier = Modifier
//                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
//                    .padding(8.dp),
//                textAlign = TextAlign.Center,
//                fontSize = 15.sp
//            )
////            OutlinedTextField(
////                value = email,
////                onValueChange = { viewModel.updateEmail(it) },
////                label = { Text(stringResource(id = R.string.EnterEmail)) },
////                singleLine = true,
////                textStyle = TextStyle(color = Color.Black),
//////                colors = TextFieldDefaults.outlinedTextFieldColors(
//////                    focusedBorderColor = if (passwordError != null) Color.Red else Color.Blue,  // لون الحدود عند التركيز
//////                ),
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .padding(start = 20.dp, top = 20.dp, end = 20.dp)
////                    //.onFocusChanged { isFocusedEmail = it.isFocused }
////            )
//
//            Text("Community Name *", color = Color.Gray)
//            OutlinedTextField(
//                value = communityName,
//                onValueChange = { viewModel.updateEmail(it) },
//                modifier = Modifier.fillMaxWidth(),
//                textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
//                singleLine = true,
//                label = { Text("r/Community") },
//                trailingIcon = {
//                    Text(text = communityName.length.toString(), color = Color.Gray)
//                }
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // حقل الوصف
//            Text("Description *", color = Color.Gray)
//            OutlinedTextField(
//                value = description,
//                onValueChange = { description = it },
//                modifier = Modifier.fillMaxWidth(),
//                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
//                label = { Text("Enter description") },
//                isError = description.length in 1..14,
//                trailingIcon = {
//                    Text(text = (500 - description.length).toString(), color = Color.Gray)
//                }
//            )
//
//            if (description.length in 1..14) {
//                Text(
//                    text = "Description must be at least 15 characters",
//                    color = Color.Red,
//                    fontSize = 14.sp
//                )
//            }
//        }
//    }
//}

//@Composable
//fun FirstStep(navController: NavController, viewModel: CommunityViewModel = viewModel()) {
//    val communityName by viewModel.communityname.observeAsState("")
//    var description by remember { mutableStateOf("") }
//
//    // ✅ التحقق من صحة الإدخال
//    val isButtonEnabled = communityName.isNotBlank() && description.length >= 15
//
//    Scaffold(
//        topBar = {},
//        bottomBar = { BottomBar(navController) },
//        containerColor = Color(0xFFEFEFEF),
//        modifier = Modifier.navigationBarsPadding()
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .systemBarsPadding()
//                .padding(16.dp)
//        ) {
//            TopBarCreateCommunity(
//                navController = navController,
//                textnumber = "1 of 4",
//                onClick = { if (isButtonEnabled) navController.navigate("SecondStep Page") },
//                isButtonEnabled = isButtonEnabled // ✅ تمرير الحالة إلى `TopBarCreateCommunity`
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = "Tell us about your Community",
//                modifier = Modifier
////                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
//                    .padding(8.dp),
//                textAlign = TextAlign.Center,
//                fontSize = 35.sp
//            )
//
//            Text(
//                text = "A name and description help people understand\n what your community is all about",
//                modifier = Modifier
////                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
//                    .padding(8.dp),
//                textAlign = TextAlign.Center,
//                fontSize = 15.sp
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // حقل اسم المجتمع
//            Text("Community Name *", color = Color.Gray)
//            OutlinedTextField(
//                value = communityName,
//                onValueChange = { viewModel.updateEmail(it) },
//                modifier = Modifier.fillMaxWidth(),
//                textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
//                singleLine = true,
//                label = { Text("r/Community") },
//                trailingIcon = {
//                    Text(text = communityName.length.toString(), color = Color.Gray)
//                }
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // حقل الوصف
//            Text("Description *", color = Color.Gray)
//            OutlinedTextField(
//                value = description,
//                onValueChange = { description = it },
//                modifier = Modifier.fillMaxWidth(),
//                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
//                label = { Text("Enter description") },
//                isError = description.length in 1..14,
//                trailingIcon = {
//                    Text(text = (500 - description.length).toString(), color = Color.Gray)
//                }
//            )
//
//            if (description.length in 1..14) {
//                Text(
//                    text = "Description must be at least 15 characters",
//                    color = Color.Red,
//                    fontSize = 14.sp
//                )
//            }
//        }
//    }
//}
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//package com.example.mealflow.ui.screens
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.WindowInsets
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.ime
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.windowInsetsPadding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.OutlinedTextFieldDefaults
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.SnackbarHost
//import androidx.compose.material3.SnackbarHostState
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.focus.onFocusChanged
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.text.input.VisualTransformation
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import androidx.navigation.compose.rememberNavController
//import com.example.mealflow.R
//import com.example.mealflow.buttons.BackButton
//import com.example.mealflow.network.resetPasswordApi
//import com.example.mealflow.utils.Validator
//import com.example.mealflow.viewModel.ForgetPasswordViewModel
//import kotlinx.coroutines.launch
//
//
//// ----------------------- Login Page ---------------------------
//@Composable
//fun ResetPasswordPage(navController: NavController, token: String?,viewModel: ForgetPasswordViewModel = viewModel())
//{
//    //Log.d("ResetPasswordPage", "Received token: $token")
//    // 🔥Store `token` in the ViewModel when the page is running
//    LaunchedEffect(token) {
//        token?.let { viewModel.updateToken(it) }
//    }
//    // ----------------------- Variables ---------------------------
//    val password by viewModel.password.observeAsState("")
//    val passwordVisible by viewModel.passwordVisible.observeAsState(false)
//    val passwordError = Validator.validatePassword(password)
////    var isMatchPassword by remember { mutableStateOf(false) }
//    var isFocusedPassword by remember { mutableStateOf(false) }
//    val repassword by viewModel.repassword.observeAsState("")
//    val tokenValue by viewModel.token.observeAsState("")
//    val isLoading by viewModel.isLoading.observeAsState(false)
//
//    // Determine if passwords match for validation
//    val passwordsMatch = password == repassword && password.isNotEmpty()
//
//    // Function to validate all inputs before API call
//    val isFormValid = password.isNotEmpty() && passwordError == null && passwordsMatch
//
//    // snackbarHostState and CoroutineScope
//    val snackbarHostState = remember { SnackbarHostState() }
//    val coroutineScope = rememberCoroutineScope()
//
//    Scaffold(
//        snackbarHost = {
//            SnackbarHost(
//                hostState = snackbarHostState,
//                modifier = Modifier
//                    .windowInsetsPadding(WindowInsets.ime) // Adjusts the position based on the keyboard height.
//            )
//        },
//        containerColor = Color.White
//    ) { paddingValues ->
//        Box{
//            Column(modifier = Modifier
//                .padding(paddingValues)) {
//                // ----------------------- Back Button ---------------------------
//                Row(
//                    Modifier
//                        .fillMaxWidth()
//                        .padding(top = 20.dp), horizontalArrangement = Arrangement.Start) {
//                    BackButton(onClick = {
//                        navController.popBackStack()
//                    })
//                }
//                // ----------------------- Join Text -----------------------------
//                Text(
//                    text = stringResource(id = R.string.ForgottenPassword),
//                    Modifier.padding(start = 20.dp, top = 80.dp, end = 20.dp),
//                    fontSize = 25.sp,
//                    fontWeight = FontWeight.Bold
//                )
//                // Input field ------------------ Password -----------------------
//                OutlinedTextField(
//                    value = password,
//                    onValueChange = { viewModel.updatePassword(it) },
//                    label = { Text(stringResource(id = R.string.EnterPassword)) },
//                    trailingIcon = {
//                        Icon(
//                            painter = painterResource(
//                                id = if (passwordVisible) {
//                                    R.drawable.eye_view_icon
//                                } else {
//                                    R.drawable.eye_closed_icon
//                                }
//                            ),
//                            contentDescription = null,
//                            tint = Color.Gray,
//                            modifier = Modifier
//                                .size(28.dp)
//                                .clickable { viewModel.togglePasswordVisibility() }
//                        )
//                    },
//                    colors = OutlinedTextFieldDefaults.colors(
//                        focusedBorderColor = if (passwordError != null) Color.Red else Color.Blue,
//                    ),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(start = 20.dp, top = 20.dp, end = 20.dp)
//                        .onFocusChanged { isFocusedPassword = it.isFocused },
//                    singleLine = true,
//                    textStyle = TextStyle(color = Color.Black),
//                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
//                )
//                // Text ------------------ Password Error -----------------------
//                if (isFocusedPassword && passwordError != null)
//                {
//                    Text(
//                        text = passwordError,
//                        modifier = Modifier
//                            .padding(start = 24.dp, end = 24.dp, top = 5.dp),
//                        color = Color.Red,
//                        fontSize = 12.sp
//                    )
//                }
//
//                //Input field ------------------ Re_password -----------------------
//                OutlinedTextField(
//                    value = repassword,
//                    onValueChange = { viewModel.updaterepassword(it) },
//                    label = { Text("Re-Enter your password") },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(start = 20.dp, top = 20.dp, end = 20.dp),
//                    singleLine = true,
//                    textStyle = TextStyle(color = Color.Black),
//                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
//                )
//
//                // Text ------------------ Password Error -----------------------
//                if (repassword.isNotEmpty() && password != repassword)
//                {
//                    Text(
//                        text = "Passwords do not match",
//                        modifier = Modifier
//                            .padding(start = 24.dp, end = 24.dp, top = 5.dp),
//                        color = Color.Red,
//                        fontSize = 12.sp
//                    )
//                }
//                //-----------------------------------------------------------------------------------------------------
//                //-----------------------------------------------------------------------------------------------------
//                // Button ------------------ Log in -------------------------------------------------------------------
//                Button(
//                    onClick = {
//                        if (isFormValid) {
//                            coroutineScope.launch {
//                                resetPasswordApi(
//                                    tokenValue ?: "",
//                                    password,
//                                    navController,
//                                    viewModel,
//                                    snackbarHostState
//                                )
//                            }
//                        }
//                    },
//                    Modifier
//                        .padding(start = 20.dp, end = 20.dp, top = 100.dp)
//                        .align(alignment = Alignment.CenterHorizontally)
//                        .fillMaxWidth(),
//                    shape = RoundedCornerShape(8.dp),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
//                    enabled = isFormValid && !isLoading
//                ) {
//                    if (isLoading) {
//                        // Show loading indicator
//                        androidx.compose.material3.CircularProgressIndicator(
//                            modifier = Modifier.size(24.dp),
//                            color = Color.White,
//                            strokeWidth = 2.dp
//                        )
//                    } else {
//                        Text(
//                            text = stringResource(id = R.string.Submit),
//                            color = Color.White
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
////------------------------------------------------------------------------------
////------------------------------------------------------------------------------
//@Preview(showSystemUi = true, showBackground = true)
//@Composable
//fun ResetPasswordPagePreview()
//{
//    ResetPasswordPage(navController = rememberNavController(),"456456")
//}
////------------------------------------------------------------------------------
////------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
