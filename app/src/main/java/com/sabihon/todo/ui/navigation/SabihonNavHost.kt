package com.sabihon.todo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sabihon.todo.ui.auth.ForgotPasswordScreen
import com.sabihon.todo.ui.auth.LoginScreen
import com.sabihon.todo.ui.auth.SignUpScreen
import com.sabihon.todo.ui.auth.SplashScreen
import com.sabihon.todo.ui.categories.CategoriesScreen
import com.sabihon.todo.ui.categories.CategoryDetailScreen
import com.sabihon.todo.ui.history.HistoryScreen
import com.sabihon.todo.ui.home.HomeScreen
import com.sabihon.todo.ui.profile.ProfileScreen
import com.sabihon.todo.ui.search.SearchScreen
import com.sabihon.todo.ui.taskdetail.TaskDetailScreen
import com.sabihon.todo.ui.addedit.AddEditTaskScreen

/**
 * Main NavHost – handles auth gating.
 */
@Composable
fun SabihonNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Route = Route.Splash
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Route.Splash> {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Route.Login) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                }
            )
        }
        composable<Route.Login> {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate(Route.SignUp) },
                onNavigateToForgot = { navController.navigate(Route.ForgotPassword) },
                onLoginSuccess = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                }
            )
        }
        composable<Route.SignUp> {
            SignUpScreen(
                onNavigateBack = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.SignUp) { inclusive = true }
                    }
                }
            )
        }
        composable<Route.ForgotPassword> {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Route.Home> {
            HomeScreen(
                onNavigateToAllTasks = { navController.navigate(Route.AllTasks) },
                onNavigateToCategories = { navController.navigate(Route.Categories) },
                onNavigateToTaskDetail = { taskId -> navController.navigate(Route.TaskDetail(taskId)) },
                onNavigateToAddTask = { navController.navigate(Route.AddEditTask()) },
                onNavigateToSearch = { navController.navigate(Route.Search) },
                onNavigateToHistory = { navController.navigate(Route.History) },
                onNavigateToProfile = { navController.navigate(Route.Profile) },
                onNavigateToCategory = { catId -> navController.navigate(Route.CategoryDetail(catId)) }
            )
        }
        composable<Route.Categories> {
            CategoriesScreen(
                onBack = { navController.popBackStack() },
                onCategoryClick = { catId -> navController.navigate(Route.CategoryDetail(catId)) }
            )
        }
        composable<Route.AllTasks> {
            CategoriesScreen(
                onBack = { navController.popBackStack() },
                onCategoryClick = { catId -> navController.navigate(Route.CategoryDetail(catId)) }
            )
        }
        composable<Route.CategoryDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<Route.CategoryDetail>()
            CategoryDetailScreen(
                categoryId = args.categoryId,
                onBack = { navController.popBackStack() },
                onTaskClick = { taskId -> navController.navigate(Route.TaskDetail(taskId)) },
                onAddTask = { navController.navigate(Route.AddEditTask()) }
            )
        }
        composable<Route.TaskDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<Route.TaskDetail>()
            TaskDetailScreen(
                taskId = args.taskId,
                onBack = { navController.popBackStack() },
                onEdit = { taskId -> navController.navigate(Route.AddEditTask(taskId)) }
            )
        }
        composable<Route.AddEditTask> { backStackEntry ->
            val args = backStackEntry.toRoute<Route.AddEditTask>()
            AddEditTaskScreen(
                taskId = args.taskId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
        composable<Route.Search> {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onTaskClick = { taskId -> navController.navigate(Route.TaskDetail(taskId)) }
            )
        }
        composable<Route.History> {
            HistoryScreen(
                onBack = { navController.popBackStack() },
                onTaskClick = { taskId -> navController.navigate(Route.TaskDetail(taskId)) }
            )
        }
        composable<Route.Profile> {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onSignOut = {
                    navController.navigate(Route.Login) {
                        popUpTo(Route.Home) { inclusive = true }
                    }
                }
            )
        }
    }
}
