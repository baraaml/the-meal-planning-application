package com.example.mealflow

//import com.example.mealflow.viewModel.TokenViewModel
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.mealflow.database.CommunityDatabase
import com.example.mealflow.database.UserPreferencesManager
import com.example.mealflow.database.community.CommunityRepository
import com.example.mealflow.database.token.TokenManager
import com.example.mealflow.network.CommunityApiService
import com.example.mealflow.ui.screens.CommunityPage
import com.example.mealflow.ui.screens.ForgetPasswordPage
import com.example.mealflow.ui.screens.HomePage
import com.example.mealflow.ui.screens.LoginPage
import com.example.mealflow.ui.screens.MainScreen
import com.example.mealflow.ui.screens.MarketPage
import com.example.mealflow.ui.screens.MealDetailScreen
import com.example.mealflow.ui.screens.OtpPage
import com.example.mealflow.ui.screens.PlannerPage
import com.example.mealflow.ui.screens.ProfilePage
import com.example.mealflow.ui.screens.QuickLoginPage
import com.example.mealflow.ui.screens.RegisterPage
import com.example.mealflow.ui.screens.ResetPasswordPage
import com.example.mealflow.ui.screens.SearchPage
import com.example.mealflow.ui.screens.StartPage
import com.example.mealflow.ui.screens.TopBar
import com.example.mealflow.ui.screens.createCommunity.FirstStep
import com.example.mealflow.ui.screens.createCommunity.FourthStep
import com.example.mealflow.ui.screens.createCommunity.SecondStep
import com.example.mealflow.ui.screens.createCommunity.ThirdStep
import com.example.mealflow.ui.theme.MealFlowTheme
import com.example.mealflow.viewModel.CommunityViewModel
import com.example.mealflow.viewModel.GetCommunityViewModel
import com.example.mealflow.viewModel.LoginViewModel
import com.example.mealflow.viewModel.MealViewModel
import com.example.mealflow.viewModel.MealViewModelFactory
import com.example.mealflow.viewModel.RegisterViewModel
import com.example.mealflow.viewModel.ViewModelFactory

// Keep this data class from the old code
data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val nonSelectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null,
    val route: String // Added route property for navigation
)

class MainActivity : ComponentActivity() {
    private lateinit var userPreferencesManager: UserPreferencesManager
    override fun onCreate(savedInstanceState: Bundle?) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        userPreferencesManager = UserPreferencesManager(this)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var isLoading = true

        Handler(Looper.getMainLooper()).postDelayed({
            isLoading = false
        }, 1000) // Delay for 2 seconds


        // Set the uncaught exception handler from the old code
        Thread.setDefaultUncaughtExceptionHandler { _, exception ->
            exception.printStackTrace()
            // We're letting the app continue without crashing
        }

        setContent {
            MealFlowTheme {
                val navController = rememberNavController()

                // Deep Link handling
                LaunchedEffect(intent?.data) {
                    intent?.data?.getQueryParameter("token")?.let { token ->
                        Log.d("MainActivity", "🔹 Token received: $token")
                        navController.navigate("Reset Password Page?token=$token")
                    }
                }

                // Use a MealViewModel with proper initialization
                val context = LocalContext.current
                val viewModel = viewModel<MealViewModel>(
                    factory = MealViewModelFactory(context.applicationContext as Application)
                )

                // Initialize login view model
                val loginViewModel: LoginViewModel = viewModel()

                // Trigger initial data fetch
                LaunchedEffect(Unit) {
                    Log.d("MainActivity", "Triggering initial meal fetch")
                    viewModel.fetchRecommendedMeals()
                }

                // Also fetch meals when login is successful
                val loginSuccessful by loginViewModel.loginSuccessful.observeAsState(false)
                LaunchedEffect(loginSuccessful) {
                    if (loginSuccessful) {
                        Log.d("MainActivity", "Login successful, refreshing meals")
                        viewModel.refreshMeals() // Force a refresh after login
                    }
                }

                AppNavHost(navController, viewModel, loginViewModel,userPreferencesManager)
            }
        }
    }
//----------------------------------------------------------------------------
// Function to move between pages with bottom navigation
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavHost(
    navController: NavHostController,
    mealViewModel: MealViewModel,
    loginViewModel: LoginViewModel,
    userPreferencesManager: UserPreferencesManager
) {
    val registerViewModel: RegisterViewModel = viewModel()
    val communityViewModel: CommunityViewModel = viewModel()
//    val tokenViewModel: TokenViewModel = viewModel()
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    // Get meal data state
    val meals by mealViewModel.meals.collectAsState()
    val isLoading by mealViewModel.isLoading.collectAsState()
    val errorMessage by mealViewModel.errorMessage.collectAsState()

    // Call navigation items
    val navigationItems = getNavigationItems()

    // Get the currentRoute
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check if the bottom bar is visible
    val shouldShowBottomBar = shouldShowBottomBar(currentRoute)

    val db = CommunityDatabase.getDatabase(applicationContext)
    val dao = db.communityDao()
    val apiService = CommunityApiService(context)
    val repository = CommunityRepository(dao, apiService)
    val viewModelGetCommunity = ViewModelProvider(this, ViewModelFactory(repository))[GetCommunityViewModel::class.java]

    val tokenManager = TokenManager(context)
    val accessToken = tokenManager.getAccessToken()
    val refreshToken = tokenManager.getRefreshToken()

    val startDestination =
        if ((accessToken.isNullOrEmpty()) && (refreshToken.isNullOrEmpty()))
        {
            "Start Page"
        }
        else if (accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty())
        {
            "QuickLogin Page"
        }
        else
        {
            "Home Page"
        }
    // Update data when opening the application
//    CoroutineScope(Dispatchers.IO).launch {
//        viewModelGetCommunity.refreshCommunities()
//    }

    // Get selectedIndex
    val currentIndex = navigationItems.indexOfFirst {
        currentRoute == it.route || (it.route == "Home Page" && currentRoute?.startsWith("meal_detail") == true)
    }.let { index -> if (index < 0) 0 else index }

    var selectedItemIndex by rememberSaveable {
        mutableStateOf(currentIndex)
    }

    LaunchedEffect(currentRoute) {
        val index = navigationItems.indexOfFirst {
            currentRoute == it.route || (it.route == "Home Page" && currentRoute?.startsWith("meal_detail") == true)
        }
        if (index >= 0) {
            selectedItemIndex = index
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            if (shouldShowTopBar(currentRoute)) {
                TopBar(navController = navController)
            }
        },
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    navigationItems = navigationItems,
                    selectedItemIndex = selectedItemIndex,
                    onItemSelected = { selectedItemIndex = it },
                    context = context
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("Start Page") {
                StartPage(navController)
            }
            composable("Login Page") {
                LoginPage(navController, loginViewModel)
            }
            composable("Register Page") {
                RegisterPage(navController, registerViewModel)
            }
            composable("Otp Page") {
                OtpPage(navController, registerViewModel)
            }
            composable("Forget Password Page") {
                ForgetPasswordPage(navController)
            }
            composable("Profile Page") {
                ProfilePage(navController)
            }
            composable("QuickLogin Page") {
                QuickLoginPage(context,navController)
            }
            composable(
                route = "Reset Password Page?token={token}",
                arguments = listOf(navArgument("token") { nullable = true }),
                deepLinks = listOf(navDeepLink {
                    uriPattern = "https://iiacbca.r.bh.d.sendibt3.com/tr/cl?token={token}"
                })
            ) { backStackEntry ->
                val token = backStackEntry.arguments?.getString("token")
                ResetPasswordPage(navController, token)
            }
            composable("Community Page") {
                CommunityPage(
                    navController,
                    viewModelGetCommunity
                )
            }
            composable("FirstStep Page") {
                FirstStep(navController,communityViewModel)
            }
            composable("SecondStep Page") {
                SecondStep(navController,communityViewModel)
            }
            composable("ThirdStep Page") {
                ThirdStep(navController,communityViewModel)
            }
            composable("FourthStep Page") {
                FourthStep(navController,communityViewModel)
            }

            // Main screens (with bottom nav)
            composable("Home Page") {
                // Pass the viewModel to enable data fetching and proper loading states
                HomePage(
                    meals = meals.ifEmpty { emptyList() }, // Don't use sample meals
                    onMealClick = { meal ->
                        navController.navigate("meal_detail/${meal.mealId}")
                    },
                    viewModel = mealViewModel // Pass the viewModel
                )
            }

            composable("Search Page") {
                SearchPage(
                    meals = meals.ifEmpty { emptyList() }, // Use fetched meals without fallback
                    onMealClick = { meal ->
                        navController.navigate("meal_detail/${meal.mealId}")
                    },
                    viewModel = mealViewModel,
                    navController = navController
                )
            }

            composable("Planner Page") {
                PlannerPage()
            }
            composable("Market Page") {
                MarketPage(navController)
            }
            composable("main screen")
            {
                MainScreen(viewModelGetCommunity)
            }
            composable(
                route = "meal_detail/{mealId}",
                arguments = listOf(navArgument("mealId") { type = NavType.StringType })
            ) { backStackEntry ->
                val mealId = backStackEntry.arguments?.getString("mealId")

                // First try to find the meal in the fetched data
                val meal = meals.find { it.mealId == mealId }

                if (meal != null) {
                    MealDetailScreen(
                        meal = meal,
                        onNavigateBack = { navController.popBackStack() }
                    )
                } else {
                    // Show loading instead of "Meal not found" if we're still fetching
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        // If not loading and meal not found, show error message
                        Text(
                            "Meal not found",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }
        }
    }
}
}


@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    navigationItems: List<BottomNavigationItem>,
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit,
    context: Context
) {
    NavigationBar {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = {
                    try {
                        onItemSelected(index)
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } catch (e: Exception) {
                        Log.e("Navigation", "Error navigating to screen", e)
                        Toast.makeText(
                            context,
                            "Navigation error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                label = { Text(text = item.title) },
                alwaysShowLabel = false,
                icon = {
                    BadgedBox(badge = {}) {
                        Icon(
                            imageVector = if (index == selectedItemIndex) {
                                item.selectedIcon
                            } else item.nonSelectedIcon,
                            contentDescription = item.title
                        )
                    }
                }
            )
        }
    }
}

// Function to return the main navigation items
@Composable
fun getNavigationItems(): List<BottomNavigationItem> {
    return listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            nonSelectedIcon = Icons.Outlined.Home,
            hasNews = false,
            route = "Home Page"
        ),
        BottomNavigationItem(
            title = "Market",
            selectedIcon = Icons.Filled.ShoppingCart,
            nonSelectedIcon = Icons.Outlined.ShoppingCart,
            hasNews = false,
            route = "Market Page"
        ),
        BottomNavigationItem(
            title = "Search",
            selectedIcon = Icons.Filled.Search,
            nonSelectedIcon = Icons.Outlined.Search,
            hasNews = false,
            route = "Search Page"
        ),
        BottomNavigationItem(
            title = "Community",
            selectedIcon = Icons.Filled.People,
            nonSelectedIcon = Icons.Outlined.PeopleOutline,
            hasNews = false,
            route = "Community Page"
        ),
        BottomNavigationItem(
            title = "Planner",
            selectedIcon = Icons.Filled.Menu,
            nonSelectedIcon = Icons.Outlined.Menu,
            hasNews = false,
            route = "Planner Page"
        )
    )
}
// Function to determine the appearance of the bottom bar
fun shouldShowBottomBar(currentRoute: String?): Boolean {
    val mainScreens = setOf(
        "Home Page",
        "Search Page",
        "Planner Page",
        "Market Page",
        "Community Page",
        "FirstStep Page",
        "SecondStep Page",
        "ThirdStep Page",
        "meal_detail"
    )
    return currentRoute?.let { route ->
        mainScreens.any { screen ->
            route == screen || (screen == "meal_detail" && route.startsWith("meal_detail"))
        }
    } ?: false
}

// Function to determine the appearance of the TopBar
fun shouldShowTopBar(currentRoute: String?): Boolean {
    val screensWithTopBar = setOf(
        "Community Page"
    )
    return currentRoute?.let { route ->
        screensWithTopBar.any { screen ->
            route == screen
        }
    } ?: false
}
