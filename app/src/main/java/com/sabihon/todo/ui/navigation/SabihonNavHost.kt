package com.sabihon.todo.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.CubicOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sabihon.todo.core.ui.extensions.useReducedMotion
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
    val isReducedMotion = useReducedMotion()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Route.Splash>(
            enterTransition = {
                fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 200))
            }
        ) {
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
        composable<Route.Login>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            }
        ) {
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
        composable<Route.SignUp>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            }
        ) {
            SignUpScreen(
                onNavigateBack = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.SignUp) { inclusive = true }
                    }
                }
            )
        }
        composable<Route.ForgotPassword>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            }
        ) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Route.Home>(
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 200))
            }
        ) {
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
        composable<Route.Categories>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            }
        ) {
            CategoriesScreen(
                onBack = { navController.popBackStack() },
                onCategoryClick = { catId -> navController.navigate(Route.CategoryDetail(catId)) }
            )
        }
        composable<Route.AllTasks>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            }
        ) {
            CategoriesScreen(
                onBack = { navController.popBackStack() },
                onCategoryClick = { catId -> navController.navigate(Route.CategoryDetail(catId)) }
            )
        }
        composable<Route.CategoryDetail>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            }
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<Route.CategoryDetail>()
            CategoryDetailScreen(
                categoryId = args.categoryId,
                onBack = { navController.popBackStack() },
                onTaskClick = { taskId -> navController.navigate(Route.TaskDetail(taskId)) },
                onAddTask = { navController.navigate(Route.AddEditTask()) }
            )
        }
        composable<Route.TaskDetail>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            }
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<Route.TaskDetail>()
            TaskDetailScreen(
                taskId = args.taskId,
                onBack = { navController.popBackStack() },
                onEdit = { taskId -> navController.navigate(Route.AddEditTask(taskId)) }
            )
        }
        composable<Route.AddEditTask>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            }
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<Route.AddEditTask>()
            AddEditTaskScreen(
                taskId = args.taskId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
        composable<Route.Search>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            }
        ) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onTaskClick = { taskId -> navController.navigate(Route.TaskDetail(taskId)) }
            )
        }
        composable<Route.History>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            }
        ) {
            HistoryScreen(
                onBack = { navController.popBackStack() },
                onTaskClick = { taskId -> navController.navigate(Route.TaskDetail(taskId)) }
            )
        }
        composable<Route.Profile>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(if (isReducedMotion) 0 else 300, easing = CubicOut)
                ) + fadeIn(animationSpec = tween(if (isReducedMotion) 0 else 200))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(if (isReducedMotion) 0 else 200, easing = CubicOut)
                ) + fadeOut(animationSpec = tween(if (isReducedMotion) 0 else 100))
            }
        ) {
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
