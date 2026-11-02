package com.example.pdf_reader.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pdf_reader.ui.home.HomeScreen
import com.example.pdf_reader.ui.reader.ReaderScreen

private const val ARG_URI = "uri"
private const val ROUTE_HOME = "home"
private const val ROUTE_READER = "reader?$ARG_URI={$ARG_URI}"

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_HOME,
    ) {
        composable(ROUTE_HOME) {
            HomeScreen(
                onOpenDocument = { uri ->
                    navController.navigate("reader?$ARG_URI=${Uri.encode(uri.toString())}")
                },
            )
        }
        composable(
            route = ROUTE_READER,
            arguments = listOf(
                navArgument(ARG_URI) { type = NavType.StringType },
            ),
        ) {
            ReaderScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
