package com.example.simongame.navigation

import android.annotation.SuppressLint
import android.graphics.Shader
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.core.graphics.translationMatrix
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.simongame.R
import com.example.simongame.view.BestScoreView
import com.example.simongame.view.GameResultView
import com.example.simongame.view.GameView
import com.example.simongame.view.HomeView

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val largeRadialGradient = object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val biggerDimension = maxOf(size.height, size.width)
            return RadialGradientShader(
                colors = listOf(Color(0xFFbbf0fe), Color(0xFF4ad8fe)),
                center = size.center,
                radius = biggerDimension / 2f,
                colorStops = listOf(0.0f, 1.0f)
            )
        }
    }

    var showBottomBar by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = colorResource(id = R.color.bottom_bar),
        bottomBar = { BottomNavigation(showBottomBar, navController) }
    ) {
            NavHost(
                navController = navController,
                startDestination = Destination.Home.route,
                modifier = Modifier
                    .padding(it)
                    .background(largeRadialGradient)
            ) {
                composable(
                    route = Destination.Home.route
                ) {
                    HomeView(
                        onNavigateToGameView = {
                            navController.navigate(Destination.Game.route)
                            showBottomBar = false
                        }
                    )
                }

                composable(
                    route = Destination.Game.route
                ) {
                    GameView(
                        onBackPressed = {
                            navController.popBackStack()
                            showBottomBar = true
                        },
                        onGameFinished = { score ->
                            navController.navigate(Destination.GameResult.buildRoute(score))
                            showBottomBar = false
                        }
                    )
                }

                composable(
                    route = Destination.GameResult.routeWithArgs,
                    arguments = listOf(navArgument(Destination.GameResult.scoreArgs) {
                        type = NavType.IntType
                        nullable = false
                    })
                ) {
                    GameResultView(
                        onNavigateToHome = {
                            navController.navigate(Destination.Home.route)
                            showBottomBar = true
                        },
                        onNavigateToGame = {
                            navController.navigate(Destination.Game.route)
                            showBottomBar = false
                        }
                    )
                }

                composable(
                    route = Destination.BestScore.route
                ) {
                    BestScoreView()
                }
        }
    }
}

@Composable
fun BottomNavigation(show: Boolean, navController: NavController) {
    // Color
    val barColor = colorResource(id = R.color.bottom_bar)

    var selected by remember { mutableIntStateOf(0) }

    AnimatedVisibility(
        visible = show,
        enter = expandVertically(animationSpec = tween(600, easing = EaseOut)),
        exit = shrinkVertically(animationSpec = tween(600, easing = EaseOut))
    ) {
        NavigationBar(
            containerColor = barColor
        ) {
            BottomNavigationItem(
                text = stringResource(id = R.string.home),
                selected = selected == 0,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                onClick = { navController.navigate(Destination.Home.route); selected = 0 }
            )
            BottomNavigationItem(
                text = stringResource(id = R.string.best_score),
                selected = selected == 1,
                selectedIcon = Icons.Filled.List,
                unselectedIcon = Icons.Outlined.List,
                onClick = { navController.navigate(Destination.BestScore.route); selected = 1 }
            )
        }
    }
}


@Composable
fun RowScope.BottomNavigationItem(
    text: String = stringResource(id = R.string.example),
    selectedIcon: ImageVector = Icons.Default.Home,
    unselectedIcon: ImageVector = Icons.Outlined.Home,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val fontFamily = FontFamily(Font(R.font.bowlby_one_sc))

    // Color
    val textColor = colorResource(id = R.color.text_light)
    val barItemSelectedColor = colorResource(id = R.color.bottom_bar_selected_item)
    val barItemNotSelectedColor = colorResource(id = R.color.bottom_bar)

    val iconColors = NavigationBarItemDefaults.colors(
        selectedIconColor = barItemSelectedColor,
        selectedTextColor = barItemSelectedColor,
        indicatorColor = barItemSelectedColor,
        unselectedIconColor = barItemNotSelectedColor,
        unselectedTextColor = barItemNotSelectedColor
    )

    // Dimen
    val fontSize = dimensionResource(id = R.dimen.font_size_small)

    NavigationBarItem(
        colors = iconColors,
        selected = selected,
        onClick = onClick,
        icon = {
            Box {
                Icon(
                    imageVector = if (selected) selectedIcon else unselectedIcon,
                    tint = Color.White,
                    contentDescription = text
                )
            }
        },
        label = {
            Text(
                text = text,
                fontSize = TextUnit(fontSize.value, TextUnitType.Sp),
                fontFamily = fontFamily,
                color = textColor
            )
        }
    )
}

interface Destination {
    object Home {
        const val route = "home"
    }

    object BestScore {
        const val route = "best_score"
    }

    object Game {
        const val route = "game"
    }

    object GameResult {
        const val route = "game_result"
        const val scoreArgs = "score"

        val routeWithArgs = "$route/{$scoreArgs}"

        fun buildRoute(score: Int): String {
            return "$route/$score"
        }
    }
}

