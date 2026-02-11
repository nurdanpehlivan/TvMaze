package com.example.tvmaze.ui.extensions

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier

fun Modifier.fullScreenPadding(innerPadding: PaddingValues) =
    fillMaxSize().padding(innerPadding)