package com.example.mealflow.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mealflow.data.model.Meal
import com.example.mealflow.ui.screens.CommunityPage
import com.example.mealflow.ui.screens.GroceriesPage
import com.example.mealflow.ui.screens.MealDetailScreen
import com.example.mealflow.ui.screens.SearchPage
import com.example.mealflow.ui.screens.HomePage
import com.example.mealflow.ui.screens.PlannerPage
import com.example.mealflow.viewModel.MealViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: MealViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {
        composable(Screen.HomeScreen.route) {
            HomePage(
                meals = viewModel.meals.value,
                onMealClick = { meal ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("meal", meal)
                    navController.navigate(Screen.MealDetailScreen.route)
                },
                viewModel = viewModel
            )
        }

        composable(Screen.SearchScreen.route) {
            SearchPage(
                viewModel = viewModel,
                onMealClick = { meal ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("meal", meal)
                    navController.navigate(Screen.MealDetailScreen.route)
                },
                navController = navController,
                meals = viewModel.meals.value
            )
        }

        composable(Screen.PlannerScreen.route) {
            PlannerPage(navController)
        }

        composable(Screen.CommunityScreen.route) {
            CommunityPage(navController)
        }

        composable(Screen.GroceriesScreen.route) {
            GroceriesPage(navController)
        }

        composable(Screen.MealDetailScreen.route) {
            val meal = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Meal>("meal")

            if (meal != null) {
                MealDetailScreen(
                    meal = meal,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

sealed class Screen(val route: String) {
    object HomeScreen : Screen("Home Page")
    object SearchScreen : Screen("Search Page")
    object MealDetailScreen : Screen("Meal Details Page")
    object CommunityScreen : Screen("Community Page")
    object GroceriesScreen : Screen("Groceries Page")
    object PlannerScreen : Screen("Planner Page")
}

class NavigationActions(private val navController: NavHostController) {
    fun navigateToHome() {
        navController.navigate(Screen.HomeScreen.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToSearch() {
        navController.navigate(Screen.SearchScreen.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToCommunity() {
        navController.navigate(Screen.CommunityScreen.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToGroceries() {
        navController.navigate(Screen.GroceriesScreen.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToPlanner() {
        navController.navigate(Screen.PlannerScreen.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToMealDetail(meal: Meal) {
        navController.currentBackStackEntry?.savedStateHandle?.set("meal", meal)
        navController.navigate(Screen.MealDetailScreen.route)
    }
}