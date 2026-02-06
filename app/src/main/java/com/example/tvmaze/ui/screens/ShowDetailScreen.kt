package com.example.tvmaze.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tvmaze.viewmodel.ShowViewModel

@Composable
fun ShowDetailScreen(
    showId: Int,
    viewModel: ShowViewModel
) {
    LaunchedEffect(showId) {
        viewModel.loadDetail(showId)
    }

    val show = viewModel.detail ?: viewModel.getShowById(showId) ?: return

    val tabs = listOf("Main", "Episodes", "Seasons", "Cast", "Crew", "Gallery")
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        // Tek scroll kaynağı: LazyColumn
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Hata mesajı varsa göster
            viewModel.errorMessage?.let { msg ->
                item { Text(msg, color = MaterialTheme.colorScheme.error) }
            }

            when (selectedTab) {
                // ---------- MAIN ----------
                0 -> {
                    item {
                        AsyncImage(
                            model = show.image?.original,
                            contentDescription = show.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                    }
                    item {
                        Text(show.name, style = MaterialTheme.typography.headlineMedium)
                        Text("Genres: ${show.genres.joinToString(", ")}")
                    }
                    item { Text(text = show.summary ?: "") }
                    item {
                        Button(onClick = { viewModel.toggleFavorite(show) }) {
                            Text(if (show.isFavorite) "Favoriden Çıkar" else "Favoriye Ekle")
                        }
                    }
                }

                // ---------- EPISODES ----------
                1 -> {
                    if (viewModel.episodes.isEmpty()) {
                        item { Text("Bölüm bulunamadı") }
                    } else {
                        items(viewModel.episodes) { ep ->
                            Text("${ep.season}x${ep.number}: ${ep.name}")
                        }
                    }
                }

                // ---------- SEASONS ----------
                2 -> {
                    if (viewModel.seasons.isEmpty()) {
                        item { Text("Sezon bulunamadı") }
                    } else {
                        items(viewModel.seasons) { s ->
                            Text("Season ${s.number ?: "-"} • Episodes: ${s.episodeOrder ?: "-"}")
                        }
                    }
                }

                // ---------- CAST ----------
                3 -> {
                    if (viewModel.cast.isEmpty()) {
                        item { Text("Cast bulunamadı") }
                    } else {
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(viewModel.cast) { c ->
                                    Column(modifier = Modifier.width(120.dp)) {
                                        AsyncImage(
                                            model = c.person.image?.medium,
                                            contentDescription = c.person.name,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(140.dp)
                                        )
                                        Text(c.person.name, maxLines = 1)
                                        Text("as ${c.character.name}", maxLines = 1)
                                    }
                                }
                            }
                        }
                    }
                }

                // ---------- CREW ----------
                4 -> {
                    if (viewModel.crew.isEmpty()) {
                        item { Text("Crew bulunamadı") }
                    } else {
                        items(viewModel.crew) { cr ->
                            Text("${cr.type}: ${cr.person.name}")
                        }
                    }
                }

                // ---------- GALLERY ----------
                5 -> {
                    if (viewModel.images.isEmpty()) {
                        item { Text("Görsel bulunamadı") }
                    } else {
                        items(viewModel.images) { img ->
                            // Gallery modelinde url: resolutions.original.url
                            val url = img.resolutions.original?.url ?: img.resolutions.medium?.url
                            if (url != null) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = img.type ?: "image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                )
                                Text(img.type ?: "image")
                            }
                        }
                    }
                }
            }
        }
    }
}
