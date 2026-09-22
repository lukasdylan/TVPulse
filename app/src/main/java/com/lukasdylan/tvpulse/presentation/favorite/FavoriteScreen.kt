package com.lukasdylan.tvpulse.presentation.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.lukasdylan.tvpulse.domain.model.TvShow
import com.lukasdylan.tvpulse.presentation.base.common.EmptyView

@Composable
fun FavoriteScreen(viewModel: FavoriteViewModel = hiltViewModel(), onShowClick: (Int) -> Unit) {
    val uiState: FavoriteUiState by viewModel.state.collectAsStateWithLifecycle()
    FavoriteContent(
        uiState = uiState,
        onDeleteFavorite = viewModel::removeFavorite,
        onShowClick = onShowClick
    )
}

@Composable
private fun FavoriteContent(
    uiState: FavoriteUiState,
    onDeleteFavorite: (Int) -> Unit,
    onShowClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is FavoriteUiState.Available -> {
                Text(
                    text = "Acara Favorite Kamu (${uiState.favorites.size} acara)",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.titleMedium,
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(items = uiState.favorites, key = { it.id }) { show ->
                        TvShowCard(
                            tvShow = show,
                            onClick = { onShowClick(show.id) },
                            onDeleteClick = onDeleteFavorite
                        )
                    }
                }
            }

            FavoriteUiState.Empty -> {
                EmptyView(
                    title = "Belum Ada Favorit",
                    subtitle = "Tambahkan serial TV favoritmu dari halaman detail.",
                    icon = Icons.Default.FavoriteBorder,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun TvShowCard(
    tvShow: TvShow,
    onClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick(tvShow.id) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = tvShow.imageUrl,
            contentDescription = tvShow.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 56.dp, height = 80.dp)
                .clip(RoundedCornerShape(8.dp)),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            Text(
                text = tvShow.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            if (tvShow.genres.isNotEmpty()) {
                Text(
                    text = tvShow.genres.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        IconButton(onClick = { onDeleteClick(tvShow.id) }) {
            Icon(Icons.Default.Delete, contentDescription = "Hapus dari favorit")
        }
    }
}
