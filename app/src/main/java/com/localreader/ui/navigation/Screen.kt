package com.localreader.ui.navigation

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Reader : Screen("reader/{bookId}") {
        fun createRoute(bookId: Long) = "reader/$bookId"
    }
}
