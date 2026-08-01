package com.orwyx.unitcalculator.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Surface with shadowElevation is the correct M3 way — it renders the shadow
    // correctly even when content behind it is the same surface color.
    Surface(
        modifier       = modifier.fillMaxWidth(),
        color          = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
    ) {
        CenterAlignedTopAppBar(
            title   = { Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge) },
            actions = {
                IconButton(onClick = onSettings) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                }
            },
            colors  = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor             = Color.Transparent,
                scrolledContainerColor     = Color.Transparent,
                titleContentColor          = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor     = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
