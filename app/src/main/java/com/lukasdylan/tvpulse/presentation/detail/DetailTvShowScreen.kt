package com.lukasdylan.tvpulse.presentation.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import coil3.compose.AsyncImage
import com.lukasdylan.tvpulse.domain.model.TvShowDetail
import com.lukasdylan.tvpulse.presentation.base.common.EmptyView
import com.lukasdylan.tvpulse.presentation.base.shimmer.shimmerEffect
import com.lukasdylan.tvpulse.presentation.base.viewmodel.UiEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun DetailTvShowScreen(
    viewModel: DetailTvShowViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    var showErrorDialog by remember { mutableStateOf<UiEvent.Dialog?>(null) }
    var snackBarMessage by rememberSaveable { mutableStateOf("")}

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is DetailTvShowUiState.DetailShown -> DetailTvShowView(
                tvShowDetail = (uiState as DetailTvShowUiState.DetailShown).detail,
                isFavorite = uiState.isFavorite,
                onAddFavorite = viewModel::addToFavorite,
                onRemoveFavorite = viewModel::removeFromFavorite,
                onBackClick = onBackClick
            )

            is DetailTvShowUiState.Loading -> LoadingTvShowDetailView(onClickBack = onBackClick)
            is DetailTvShowUiState.NotFound -> EmptyView(
                title = "Tv Show tidak ditemukan",
                subtitle = "Silakan coba lagi",
                modifier = Modifier.fillMaxSize(),
                icon = Icons.Default.Warning,
            )
        }
        AnimatedVisibility(
            visible = snackBarMessage.isNotBlank(),
            enter =
                fadeIn(animationSpec = tween(durationMillis = 400)) +
                        expandVertically(
                            animationSpec =
                                tween(
                                    durationMillis = 400,
                                    easing = CubicBezierEasing(0.34f, 1.54f, 0.84f, 1f),
                                ),
                        ),
            exit =
                fadeOut(animationSpec = tween(durationMillis = 400)) +
                        shrinkVertically(
                            animationSpec = tween(durationMillis = 400),
                        ),
        ) {
            Row(
                modifier =
                    Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = snackBarMessage,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }

    LaunchedEffect(key1 = viewModel.event, key2 = lifecycleOwner) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collectLatest {
                if (it is UiEvent.Dialog) {
                    showErrorDialog = it
                } else if (it is UiEvent.SnackBar) {
                    snackBarMessage = it.message
                    delay(1.seconds)
                    snackBarMessage = ""
                }
            }
    }

    if (showErrorDialog != null) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = null },
            title = {
                Text(
                    text = when (showErrorDialog) {
                        UiEvent.Dialog.ConnectionErrorDialog -> "Koneksi Bermasalah"
                        is UiEvent.Dialog.GeneralErrorDialog -> "Gagal Memuat Data"
                        null -> ""
                    }
                )
            },
            text = {
                Text(
                    text = when (showErrorDialog) {
                        UiEvent.Dialog.ConnectionErrorDialog -> "Silakan cek kembali jaringan internet anda"
                        is UiEvent.Dialog.GeneralErrorDialog -> "Terjadi kesalahan dengan sistem \nAlasan: (${(showErrorDialog as UiEvent.Dialog.GeneralErrorDialog).errorReason})"
                        null -> ""
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = null }) {
                    Text("Ok")
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTvShowView(
    tvShowDetail: TvShowDetail,
    isFavorite: Boolean,
    onAddFavorite: () -> Unit,
    onRemoveFavorite: () -> Unit,
    onBackClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(connection = scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(tvShowDetail.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = tvShowDetail.imageUrl,
                contentDescription = tvShowDetail.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = tvShowDetail.name,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (tvShowDetail.genres.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp),
                    ) {
                        items(tvShowDetail.genres) { genre ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                            ) {
                                Text(
                                    text = genre,
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        }
                    }
                }

                if (tvShowDetail.rating != 0.0) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFA726),
                    )
                    Text(
                        text = " ${tvShowDetail.rating} · ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(text = "No Rating", style = MaterialTheme.typography.bodyMedium)
                }

                Button(
                    onClick = if (isFavorite) onRemoveFavorite else onAddFavorite,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = if (isFavorite) {
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    } else {
                        ButtonDefaults.buttonColors()
                    },
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                    )
                    Text(
                        text = if (isFavorite) "Hapus dari Favorit" else "Tambah ke Favorit",
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }

                Text(
                    text = "Sinopsis",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
                )
                Text(
                    text = AnnotatedString.fromHtml(tvShowDetail.synopsis),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun LoadingTvShowDetailView(onClickBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconButton(onClick = onClickBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .shimmerEffect()
        )
    }
}