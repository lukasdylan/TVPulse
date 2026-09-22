package com.lukasdylan.tvpulse.presentation

sealed class Screen(val route: String) {

    data object Main : Screen("main")

    data object Detail : Screen("detail/{showId}") {
        const val ARG_SHOW_ID = "showId"
        const val DEEP_LINK_URI_PATTERN = "movieapp://detail/{showId}"
        fun createRoute(showId: Int) = "detail/$showId"
    }
}
