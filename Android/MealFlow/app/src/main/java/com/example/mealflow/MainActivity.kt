package com.example.mealflow

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.mealflow.screens.ForgetPasswordPage
import com.example.mealflow.screens.HomePage
import com.example.mealflow.screens.LoginPage
import com.example.mealflow.screens.OtpPage
import com.example.mealflow.screens.RegisterPage
import com.example.mealflow.screens.ResetPasswordPage
import com.example.mealflow.screens.StartPage
import com.example.mealflow.screens.TestPage
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
            HomePage(navController)
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
        composable("Test Page") {
            TestPage(navController)
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

