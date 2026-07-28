package com.orwyx.unitcalculator.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orwyx.unitcalculator.domain.model.AccentColor
import com.orwyx.unitcalculator.domain.model.ThemeMode
import com.orwyx.unitcalculator.ui.components.NeumorphicCard
import com.orwyx.unitcalculator.ui.components.SectionHeader
import com.orwyx.unitcalculator.ui.theme.LocalNeuColors
import com.orwyx.unitcalculator.ui.theme.frostedBlurBackground
import com.orwyx.unitcalculator.ui.theme.glassBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val message  by viewModel.message.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val isDark = LocalNeuColors.current.isDark

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri -> uri?.let { viewModel.exportBackup(it) } }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri -> uri?.let { viewModel.importBackup(it) } }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassBar(isDark),
            ) {
                // Frosted blur background layer
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .frostedBlurBackground(isDark),
                )
                // Sharp content on top
                TopAppBar(
                    title = { Text("Settings", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = transparentTopBarColors(),
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionHeader("Appearance")
            NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("Theme", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = settings.themeMode == mode,
                                    onClick  = { viewModel.setTheme(mode) },
                                )
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = settings.themeMode == mode,
                                onClick  = { viewModel.setTheme(mode) },
                            )
                            Text(mode.name.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            }

            SectionHeader("Accent colour")
            NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("App colour", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Changes the main colour and background gradient used throughout the app.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                    // 5-column grid with weight(1f) so swatches fill full card width
                    val accentEntries = AccentColor.entries
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        accentEntries.chunked(5).forEach { rowAccents ->
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                rowAccents.forEach { accent ->
                                    val selected = settings.accentColor == accent
                                    val accentHue = accentColorValue(accent)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .clip(CircleShape)
                                            .background(accentHue)
                                            .then(
                                                if (selected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                                else Modifier
                                            )
                                            .clickable { viewModel.setAccentColor(accent) },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (selected) {
                                            Icon(
                                                Icons.Rounded.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp),
                                            )
                                        }
                                    }
                                }
                                // Fill empty slots in the last row
                                repeat(5 - rowAccents.size) {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            SectionHeader("Reading cycle")
            NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("Monthly reading date", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Planning and forecasts start from this day each month.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                    // 7-column grid: 5 rows × 7 = 35 slots, days 1-31 + 4 empty
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        (0..4).forEach { rowIdx ->
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                (0..6).forEach { colIdx ->
                                    val day = rowIdx * 7 + colIdx + 1
                                    if (day <= 31) {
                                        val selected = settings.readingDate == day
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clip(MaterialTheme.shapes.small)
                                                .background(
                                                    if (selected) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                                                )
                                                .clickable { viewModel.setReadingDate(day) },
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                day.toString(),
                                                style      = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                                color      = if (selected) MaterialTheme.colorScheme.onPrimary
                                                             else MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    } else {
                                        Spacer(Modifier.weight(1f).aspectRatio(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            SectionHeader("Defaults")
            NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("Default target", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Used as the starting target for new meters.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(10.dp))
                    var rawTarget by remember(settings.defaultTarget) {
                        mutableStateOf(
                            if (settings.defaultTarget % 1.0 == 0.0) settings.defaultTarget.toLong().toString()
                            else settings.defaultTarget.toString()
                        )
                    }
                    OutlinedTextField(
                        value        = rawTarget,
                        onValueChange = { rawTarget = it },
                        placeholder  = { Text("180") },
                        suffix       = { Text("units", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        singleLine   = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            rawTarget.toDoubleOrNull()?.let { viewModel.setDefaultTarget(it) }
                            focusManager.clearFocus()
                        }),
                        modifier = Modifier.fillMaxWidth(),
                        shape    = MaterialTheme.shapes.medium,
                    )
                }
            }

            SectionHeader("Backup")
            NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    ActionRow(
                        icon     = Icons.Rounded.Upload,
                        title    = "Export backup",
                        subtitle = "Save meters, history and settings to a file",
                        onClick  = { exportLauncher.launch("unit-calculator-backup.json") },
                    )
                    Spacer(Modifier.height(6.dp))
                    ActionRow(
                        icon     = Icons.Rounded.Download,
                        title    = "Import backup",
                        subtitle = "Restore from a previously saved file",
                        onClick  = { importLauncher.launch(arrayOf("application/json")) },
                    )
                }
            }

            SectionHeader("About")
            NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("Unit Calculator", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Version 1.0.0", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Offline-first electricity meter management. All your data stays on this " +
                            "device — no account, no ads, no tracking.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun accentColorValue(accent: AccentColor): Color = when (accent) {
    AccentColor.BLUE         -> Color(0xFF3A5BFF)
    AccentColor.NAVY         -> Color(0xFF0D47A1)
    AccentColor.INDIGO       -> Color(0xFF3949AB)
    AccentColor.DEEP_PURPLE  -> Color(0xFF512DA8)
    AccentColor.PURPLE       -> Color(0xFF7C4DFF)
    AccentColor.VIOLET       -> Color(0xFF7B1FA2)
    AccentColor.MAGENTA      -> Color(0xFF880E4F)
    AccentColor.PINK         -> Color(0xFFD81B60)
    AccentColor.ROSE         -> Color(0xFFE91E63)
    AccentColor.RED          -> Color(0xFFC62828)
    AccentColor.DEEP_ORANGE  -> Color(0xFFBF360C)
    AccentColor.ORANGE       -> Color(0xFFE65100)
    AccentColor.AMBER        -> Color(0xFFFF6F00)
    AccentColor.LIME         -> Color(0xFF558B2F)
    AccentColor.GREEN        -> Color(0xFF00897B)
    AccentColor.EMERALD      -> Color(0xFF1B5E20)
    AccentColor.TEAL         -> Color(0xFF0097A7)
    AccentColor.CYAN         -> Color(0xFF006064)
    AccentColor.BROWN        -> Color(0xFF4E342E)
    AccentColor.SLATE        -> Color(0xFF37474F)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun transparentTopBarColors(): TopAppBarColors =
    TopAppBarDefaults.topAppBarColors(
        containerColor             = Color.Transparent,
        scrolledContainerColor     = Color.Transparent,
        titleContentColor          = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor     = MaterialTheme.colorScheme.onSurface,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
    )

@Composable
private fun ActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(26.dp))
        Spacer(Modifier.size(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
