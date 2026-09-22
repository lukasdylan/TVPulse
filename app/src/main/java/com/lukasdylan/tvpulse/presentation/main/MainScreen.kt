package com.lukasdylan.tvpulse.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.lukasdylan.tvpulse.presentation.favorite.FavoriteScreen
import com.lukasdylan.tvpulse.presentation.home.HomeScreen
import kotlinx.coroutines.launch

enum class MainTabScreen {
    HOME, FAVORITE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onShowClick: (Int) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { MainTabScreen.entries.size })
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "TVPulse",
                    fontWeight = FontWeight.SemiBold,
                )
            },
        )
        SecondaryTabRow(selectedTabIndex = pagerState.currentPage) {
            MainTabScreen.entries.forEachIndexed { index, screen ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page = index)
                        }
                    },
                    text = {
                        Text(
                            text = screen.name,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                )
            }
        }
        HorizontalPager(state = pagerState) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (page == MainTabScreen.HOME.ordinal) {
                    HomeScreen(onShowClick = onShowClick)
                } else {
                    FavoriteScreen(onShowClick = onShowClick)
                }
            }
        }
    }
}