package com.localreader.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.localreader.R
import com.localreader.ui.theme.ThemeViewModel
import com.localreader.ui.bookshelf.BookshelfScreen
import com.localreader.ui.profile.ProfileScreen
import com.localreader.ui.reader.ReaderScreen
import com.localreader.ui.statistics.StatisticsScreen

enum class TabItem(
    val route: String,
    val icon: @Composable () -> Unit,
    val label: @Composable () -> Unit
) {
    BOOKSHELF(
        route = "bookshelf",
        icon = { Icon(Icons.Default.AutoStories, contentDescription = null) },
        label = { Text(stringResource(R.string.tab_bookshelf)) }
    ),
    STATISTICS(
        route = "statistics",
        icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
        label = { Text(stringResource(R.string.tab_statistics)) }
    ),
    PROFILE(
        route = "profile",
        icon = { Icon(Icons.Default.Person, contentDescription = null) },
        label = { Text(stringResource(R.string.tab_profile)) }
    )
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = TabItem.entries.any { it.route == currentRoute }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    TabItem.entries.forEach { tab ->
                        NavigationBarItem(
                            icon = tab.icon,
                            label = tab.label,
                            selected = currentRoute == tab.route,
                            onClick = {
                                if (currentRoute != tab.route) {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TabItem.BOOKSHELF.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(TabItem.BOOKSHELF.route) {
                BookshelfScreen(
                    onBookClick = { bookId ->
                        navController.navigate("reader/$bookId")
                    },
                    navController = navController
                )
            }
            composable(TabItem.STATISTICS.route) {
                StatisticsScreen()
            }
            composable(TabItem.PROFILE.route) {
                ProfileScreen(themeViewModel = themeViewModel)
            }
            readerScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

fun NavGraphBuilder.readerScreen(
    onBack: () -> Unit
) {
    composable("reader/{bookId}") { backStackEntry ->
        val bookId = backStackEntry.arguments?.getString("bookId")?.toLongOrNull() ?: 0L
        ReaderScreen(
            bookId = bookId,
            onBack = onBack
        )
    }
}
