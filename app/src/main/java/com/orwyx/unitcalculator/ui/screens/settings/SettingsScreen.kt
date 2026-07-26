package com.orwyx.unitcalculator.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orwyx.unitcalculator.domain.model.AppTheme
import com.orwyx.unitcalculator.domain.model.ThemeMode
import com.orwyx.unitcalculator.ui.components.NeumorphicCard
import com.orwyx.unitcalculator.ui.components.SectionHeader
import com.orwyx.unitcalculator.ui.theme.LocalNeuColors
import com.orwyx.unitcalculator.ui.theme.glassBar
import com.orwyx.unitcalculator.ui.theme.themeData

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassBar(isDark),
            ) {
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

            SectionHeader("Visual theme")
            NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("Background & Accent", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Choose the fluid background and accent colour for the whole app.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(14.dp))
                    // 2-column grid of theme preview cards
                    val themes = AppTheme.entries
                    themes.chunked(2).forEach { row ->
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            row.forEach { theme ->
                                ThemePreviewCard(
                                    theme    = theme,
                                    selected = settings.appTheme == theme,
                                    onClick  = { viewModel.setAppTheme(theme) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(10.dp))
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
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        (0..4).forEach { rowIdx ->
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                (1..7).forEach { colIdx ->
                                    val day = rowIdx * 7 + colIdx
                                    if (day <= 31) {
                                        val selected = settings.readingDate == day
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clip(CircleShape)
                                                .background(
                                                    if (selected) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                                )
                                                .clickable { viewModel.setReadingDate(day) },
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                day.toString(),
                                                style      = MaterialTheme.typography.labelMedium,
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                                color      = if (selected) MaterialTheme.colorScheme.onPrimary
                                                             else MaterialTheme.colorScheme.onSurface,
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

@Composable
private fun ThemePreviewCard(
    theme: AppTheme,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val data    = themeData(theme)
    val isDark  = LocalNeuColors.current.isDark
    val primary = MaterialTheme.colorScheme.primary
    val shape   = RoundedCornerShape(16.dp)
    val onText  = if (isDark) Color.White else Color.White

    Box(
        modifier = modifier
            .aspectRatio(1.65f)
            .clip(shape)
            .then(
                if (selected) Modifier.border(2.dp, primary, shape)
                else Modifier.border(1.dp, Color.White.copy(alpha = if (isDark) 0.15f else 0.5f), shape)
            )
            .drawBehind {
                val w = size.width
                val h = size.height
                // Background base
                drawRect(color = if (isDark) data.bgDark else data.bgLight)
                // Blob 1
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(data.blob1.copy(alpha = 0.80f), Color.Transparent),
                        center = Offset(w * 0.35f, h * 0.30f),
                        radius = w * 0.65f,
                    ),
                    radius = w * 0.65f,
                    center = Offset(w * 0.35f, h * 0.30f),
                )
                // Blob 2
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(data.blob2.copy(alpha = 0.70f), Color.Transparent),
                        center = Offset(w * 0.75f, h * 0.70f),
                        radius = w * 0.55f,
                    ),
                    radius = w * 0.55f,
                    center = Offset(w * 0.75f, h * 0.70f),
                )
                // Blob 3
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(data.blob3.copy(alpha = 0.60f), Color.Transparent),
                        center = Offset(w * 0.85f, h * 0.25f),
                        radius = w * 0.40f,
                    ),
                    radius = w * 0.40f,
                    center = Offset(w * 0.85f, h * 0.25f),
                )
                // Frosted glass overlay
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.10f), Color.Transparent),
                        startY = 0f, endY = h * 0.5f,
                    ),
                )
            }
            .clickable(onClick = onClick)
            .padding(10.dp),
        contentAlignment = Alignment.BottomStart,
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text      = theme.displayName,
                style     = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color     = onText,
                modifier  = Modifier.weight(1f),
            )
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = null,
                        tint     = Color.White,
                        modifier = Modifier.size(13.dp),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun transparentTopBarColors(): TopAppBarColors =
    TopAppBarDefaults.topAppBarColors(
        containerColor        = Color.Transparent,
        scrolledContainerColor = Color.Transparent,
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
