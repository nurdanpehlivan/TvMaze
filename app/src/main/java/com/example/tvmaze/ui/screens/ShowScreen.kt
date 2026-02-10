package com.example.tvmaze.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tvmaze.R
import com.example.tvmaze.data.Show
import com.example.tvmaze.viewmodel.ShowViewModel
import androidx.navigation.NavController
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay


@Composable
fun ShowScreen(viewModel: ShowViewModel, navController: NavController) {

    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(searchText) {
        val query= searchText.trim()
        if(query.length<3) return@LaunchedEffect
        delay(500)
        if(query==searchText.trim()) {
            viewModel.searchShow(query)
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {

            // Logo
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = stringResource(R.string.app_logo_desc),
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Arama TextField
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text(stringResource(R.string.search_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Arama ve Popüler Butonları
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = { viewModel.searchShow(searchText) },
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.search_button))
                }

                Button(
                    onClick = { viewModel.getPopularShows() },
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.popular_button)) }

                Button(
                    onClick = { navController.navigate("favorites") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.favorites_button))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dinamik genre butonları → veri geldiyse göster
            if (viewModel.hasData) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // "Tümü" butonu
                    Button(onClick = { viewModel.filterByGenre(null) }) {
                        Text(stringResource(R.string.all_genres))
                    }

                    // API’den gelen türler
                    viewModel.allGenres.forEach { genre: String ->
                        Button(onClick = { viewModel.filterByGenre(genre) }) {
                            Text(genre)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Grid + Skeleton
            if (viewModel.isLoading) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(6) { // placeholder sayısı
                        Box(
                            modifier = Modifier
                                .height(260.dp)
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(Color.Gray.copy(alpha = 0.3f))
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.shows) { show ->
                        ShowItem(show, navController, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ShowItem(show: Show, navController: NavController, viewModel: ShowViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clickable { navController.navigate("show_detail/${show.id}") }
    ) {
        Column {
            AsyncImage(
                model = show.image?.medium,
                contentDescription = show.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = show.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)   // ✅ ikona yer aç
                )

                IconButton(
                    onClick = { viewModel.toggleFavorite(show) }
                ) {
                    Icon(
                        imageVector = if (show.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite_icon_desc)
                    )
                }
            }
        }
    }
}
