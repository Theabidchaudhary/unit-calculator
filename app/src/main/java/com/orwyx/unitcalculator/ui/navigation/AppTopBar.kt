package com.orwyx.unitcalculator.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title   = { Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge) },
        actions = {
            IconButton(onClick = onSettings) {
                Icon(Icons.Rounded.Settings, contentDescription = "Settings")
            }
        },
        colors  = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor            = MaterialTheme.colorScheme.surface,
            scrolledContainerColor    = MaterialTheme.colorScheme.surface,
            titleContentColor         = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor    = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        modifier = modifier.fillMaxWidth(),
    )
}
