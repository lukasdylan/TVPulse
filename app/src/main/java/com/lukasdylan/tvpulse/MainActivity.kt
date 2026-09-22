package com.lukasdylan.tvpulse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.lukasdylan.tvpulse.presentation.Screen
import com.lukasdylan.tvpulse.presentation.detail.DetailTvShowScreen
import com.lukasdylan.tvpulse.presentation.main.MainScreen
import com.lukasdylan.tvpulse.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var navController: NavController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val controller = rememberNavController()
                navController = controller
                NavHost(
                    navController = controller,
                    startDestination = Screen.Main.route,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    composable(route = Screen.Main.route) {
                        MainScreen(
                            onShowClick = { showId ->
                                controller.navigate(
                                    Screen.Detail.createRoute(showId)
                                )
                            },
                        )
                    }
                    composable(
                        route = Screen.Detail.route,
                        arguments = listOf(navArgument(Screen.Detail.ARG_SHOW_ID) {
                            type = NavType.IntType
                        }),
                        deepLinks = listOf(navDeepLink {
                            uriPattern = Screen.Detail.DEEP_LINK_URI_PATTERN
                        }),
                    ) {
                        DetailTvShowScreen(onBackClick = controller::popBackStack)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navController?.handleDeepLink(intent)
    }
}
