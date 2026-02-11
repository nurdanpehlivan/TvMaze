package com.example.tvmaze.ui.screens

import android.text.Html
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBarsPadding
import coil.compose.AsyncImage
import com.example.tvmaze.viewmodel.ShowViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.res.stringResource
import com.example.tvmaze.R
import com.example.tvmaze.ui.extensions.fullScreenPadding
private fun htmlToText(html: String): String {
    return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailScreen(
    showId: Int,
    viewModel: ShowViewModel
) {
    LaunchedEffect(showId) {
        viewModel.loadDetail(showId)
    }

    val show = viewModel.detail ?: viewModel.getShowById(showId) ?: return

    val tabs = listOf(
        stringResource(R.string.tab_main),
        stringResource(R.string.tab_episodes),
        stringResource(R.string.tab_seasons),
        stringResource(R.string.tab_cast),
        stringResource(R.string.tab_crew),
        stringResource(R.string.tab_characters),
        stringResource(R.string.tab_gallery)
    )
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                TopAppBar(
                    title = { Text(show.name, maxLines = 1) }
                )

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 8.dp
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier.fullScreenPadding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            viewModel.errorMessage?.let { msg ->
                item { Text(msg, color = MaterialTheme.colorScheme.error) }
            }

            when (selectedTab) {

                // ---------- MAIN ----------
                0 -> {
                    item {
                        AsyncImage(
                            model = show.image?.original,
                            contentDescription = stringResource(R.string.poster_desc),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                    }
                    item {
                        Text(
                            text = stringResource(
                                R.string.genres_format,
                                show.genres.joinToString(", ")
                            )
                        )}
                    item {
                        Text(
                            text = show.summary?.let { htmlToText(it) } ?: ""
                        )
                    }
                    item {
                        Button(onClick = { viewModel.toggleFavorite(show) }) {
                            Text(
                                if (show.isFavorite)
                                    stringResource(R.string.remove_from_favorites)
                                else
                                    stringResource(R.string.add_to_favorites))
                        }
                    }
                }

                // ---------- EPISODES (foto + score) ----------
                1 -> {
                    if (viewModel.episodes.isEmpty()) {
                        item { Text(stringResource(R.string.no_episodes)) }
                    } else {
                        items(viewModel.episodes) { ep ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                AsyncImage(
                                    model = ep.image?.medium,
                                    contentDescription = ep.name,
                                    modifier = Modifier.size(90.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${ep.season}x${ep.number}  ${ep.name}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = stringResource(
                                            R.string.score_format,
                                            ep.rating?.average?.toString() ?: "-"
                                        )
                                    )

                                    // ❌ "Airdate: ..."
                                    ep.airdate?.let {
                                        Text(
                                            text = stringResource(
                                                R.string.airdate_format,
                                                it
                                            )
                                        )
                                    }
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }

                // ---------- SEASONS (foto + yazı) ----------
                2 -> {
                    if (viewModel.seasons.isEmpty()) {
                        item { Text(stringResource(R.string.no_seasons)) }
                    } else {
                        items(viewModel.seasons) { s ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                AsyncImage(
                                    model = s.image?.medium,
                                    contentDescription = "Season ${s.number ?: "-"}",
                                    modifier = Modifier.size(90.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Season ${s.number ?: "-"}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text("Episodes: ${s.episodeOrder ?: "-"}")
                                    Text("${s.premiereDate ?: "-"} → ${s.endDate ?: "-"}")
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }

                // ---------- CAST (eşit boy + aşağı kaydırmalı grid) ----------
                3 -> {
                    if (viewModel.cast.isEmpty()) {
                        item { Text(stringResource(R.string.no_cast)) }
                    } else {
                        // Grid yüksekliğini kaba bir şekilde hesaplayalım (scroll çakışmasın)
                        val rows = (viewModel.cast.size + 1) / 2
                        val gridHeight = (rows * 272).dp // 260 kart + spacing payı

                        item {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                userScrollEnabled = false, // ✅ dış LazyColumn scroll etsin
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(gridHeight)
                            ) {
                                gridItems(viewModel.cast) { c ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(260.dp)
                                    ) {
                                        Column {
                                            AsyncImage(
                                                model = c.person.image?.original ?: c.person.image?.medium,
                                                contentDescription = c.person.name,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(190.dp)
                                            )

                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(
                                                    text = c.person.name,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = stringResource(
                                                        R.string.as_character_format,
                                                        c.character.name
                                                    ),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ---------- CREW (foto + type) ----------
                4 -> {
                    if (viewModel.crew.isEmpty()) {
                        item { Text(stringResource(R.string.no_crew)) }
                    } else {
                        items(viewModel.crew) { cr ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                AsyncImage(
                                    model = cr.person.image?.medium,
                                    contentDescription = cr.person.name,
                                    modifier = Modifier.size(70.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(cr.person.name, style = MaterialTheme.typography.titleMedium)
                                    Text(cr.type)
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }

                // ---------- CHARACTERS (cast içinden) ----------
                5 -> {
                    val characters = viewModel.cast.map { it.character }.distinctBy { it.id }
                    if (characters.isEmpty()) {
                        item { Text(stringResource(R.string.no_characters)) }
                    } else {
                        items(characters) { ch ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                AsyncImage(
                                    model = ch.image?.medium,
                                    contentDescription = ch.name,
                                    modifier = Modifier.size(70.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(ch.name, style = MaterialTheme.typography.titleMedium)
                            }
                            HorizontalDivider()
                        }
                    }
                }

                // ---------- GALLERY (type başlığı 1 kez + altında görseller) ----------
                6 -> {
                    if (viewModel.images.isEmpty()) {
                        item { Text(stringResource(R.string.no_images)) }
                    } else {
                        val grouped = viewModel.images.groupBy { it.type ?: "Other" }

                        grouped.forEach { (type, imgs) ->
                            item {
                                Text(
                                    text = type.uppercase(),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            items(imgs) { img ->
                                val url = img.resolutions.original?.url ?: img.resolutions.medium?.url
                                if (url != null) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = type,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(220.dp)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                            }

                            item { Spacer(Modifier.height(12.dp)) }
                        }
                    }
                }
            }
        }
    }
}
