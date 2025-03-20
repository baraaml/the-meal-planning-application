package com.example.mealflow

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.mealflow.data.model.Meal
import com.example.mealflow.ui.navigation.Screen
import com.example.mealflow.ui.screens.ForgetPasswordPage
import com.example.mealflow.ui.screens.HomePage
import com.example.mealflow.ui.screens.LoginPage
import com.example.mealflow.ui.screens.MealDetailScreen
import com.example.mealflow.ui.screens.OtpPage
import com.example.mealflow.ui.screens.RegisterPage
import com.example.mealflow.ui.screens.ResetPasswordPage
import com.example.mealflow.ui.screens.StartPage
import com.example.mealflow.ui.screens.sampleMeals
import com.example.mealflow.ui.theme.MealFlowTheme
import com.example.mealflow.viewModel.RegisterViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MealFlowTheme {
                val navController = rememberNavController()

                // 🔥 التعامل مع Deep Link عند تشغيل التطبيق
                LaunchedEffect(intent?.data) {
                    intent?.data?.getQueryParameter("token")?.let { token ->
                        Log.d("MainActivity", "🔹 Token received: $token")
                        navController.navigate("Reset Password Page?token=$token")
                    }
                }

                AppNavHost(navController)
            }
        }
    }
}

//----------------------------------------------------------------------------
// Function to move between pages
@Composable
fun AppNavHost(navController: NavHostController) {
    val registerViewModel: RegisterViewModel = viewModel()
    NavHost(navController = navController, startDestination = "Start Page") {
        composable("Start Page") {
            StartPage(navController)
        }
        composable("Home Page") {
            HomePage(
                meals = sampleMeals,
                onMealClick = { meal ->
                    // Pass the mealId as a parameter in the navigation route
                    navController.navigate("meal_detail/${meal.mealId}")
                }
            )
        }
        composable(
            route = "meal_detail/{mealId}",
            arguments = listOf(navArgument("mealId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Extract the mealId from the route
            val mealId = backStackEntry.arguments?.getString("mealId")

            // Find the meal by ID
            val meal = sampleMeals.find { it.mealId == mealId }

            // If meal is found, show the detail screen
            if (meal != null) {
                MealDetailScreen(
                    meal = meal,
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                // Handle case where meal is not found
                Text(
                    "Meal not found",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
        composable("Register Page") {
            RegisterPage(navController,registerViewModel)
        }
        composable("Login Page") {
            LoginPage(navController)
        }
        composable("Otp Page") {
            OtpPage(navController,registerViewModel)
        }
        composable("Forget Password Page") {
            ForgetPasswordPage(navController)
        }
        // 🔹 صفحة إعادة تعيين كلمة المرور مع دعم الـ Deep Link
        composable(
            route = "Reset Password Page?token={token}",
            arguments = listOf(navArgument("token") { nullable = true }),
            deepLinks = listOf(navDeepLink { uriPattern = "https://iiacbca.r.bh.d.sendibt3.com/tr/cl?token={token}" })
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")
            ResetPasswordPage(navController, token)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyApp() {
    val navController = rememberNavController()
    AppNavHost(navController = navController)
}
