package com.orwyx.unitcalculator.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.orwyx.unitcalculator.ui.theme.LocalNeuColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark      = LocalNeuColors.current.isDark
    val shadowColor = Color.Black.copy(alpha = if (isDark) 0.55f else 0.18f)
    val surface     = MaterialTheme.colorScheme.surface

    // Wrap in a Box so the shadow is drawn on the outer container — TopAppBar clips its own modifier shadow
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(elevation = 8.dp, ambientColor = shadowColor, spotColor = shadowColor)
            .background(surface),
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
