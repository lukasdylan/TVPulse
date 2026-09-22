package com.lukasdylan.tvpulse.presentation.home

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import coil3.compose.AsyncImage
import com.lukasdylan.tvpulse.domain.model.TvShow
import com.lukasdylan.tvpulse.presentation.base.common.EmptyView
import com.lukasdylan.tvpulse.presentation.base.shimmer.ShimmerShowGrid
import com.lukasdylan.tvpulse.presentation.base.viewmodel.UiEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), onShowClick: (Int) -> Unit) {
    val uiState: HomeUiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    var showErrorDialog by remember { mutableStateOf<UiEvent.Dialog?>(null) }
    HomeContent(
        uiState = uiState,
        onInputSearchTvShow = viewModel::inputSearchTvShow,
        onShowClick = onShowClick
    )
    LaunchedEffect(key1 = viewModel.event, key2 = lifecycleOwner) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collectLatest {
                if (it is UiEvent.Dialog) {
                    showErrorDialog = it
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

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onInputSearchTvShow: (String) -> Unit,
    onShowClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = uiState.searchText,
            onValueChange = onInputSearchTvShow,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = { Text("Cari Serial TV (misal: horror)...") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (uiState.searchText.isNotEmpty()) {
                    IconButton(onClick = { onInputSearchTvShow("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear search")
                    }
                }
            },
        )

        when (uiState) {
            is HomeUiState.Empty -> {
                val title =
                    if (uiState.onSearchState()) "Hasil Pencarian Tidak Ditemukan" else "Tidak Ada Acara TV"
                val subtitle = "Silakan cek kembali"
                EmptyView(
                    title = title,
                    subtitle = subtitle,
                    icon = Icons.Default.Info,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            is HomeUiState.Error -> {
                val title =
                    "Gagal memuat halaman"
                val subtitle = "Silakan cek kembali"
                EmptyView(
                    title = title,
                    subtitle = subtitle,
                    icon = Icons.Default.Warning,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            is HomeUiState.Loading -> {
                ShimmerShowGrid(modifier = Modifier.fillMaxSize())
            }

            is HomeUiState.Success -> {
                Text(
                    text = if (uiState.onSearchState()) "Hasil Pencarian (${uiState.data.size} acara)" else "Acara Populer",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.titleMedium,
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(items = uiState.data, key = { it.id }) { show ->
                        TvShowCard(tvShow = show, onClick = { onShowClick(show.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun TvShowCard(
    tvShow: TvShow,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(width = 1.dp, color = Color.DarkGray, shape = RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = tvShow.imageUrl,
            contentDescription = tvShow.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f),
            contentScale = ContentScale.Crop,
        )
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = tvShow.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (tvShow.rating != 0.0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFA726),
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = " ${tvShow.rating}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                Text(
                    text = "No Rating",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
