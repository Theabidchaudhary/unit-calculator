package com.orwyx.unitcalculator.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orwyx.unitcalculator.core.util.Formatters
import com.orwyx.unitcalculator.domain.model.Meter
import com.orwyx.unitcalculator.domain.model.MeterPhase
import com.orwyx.unitcalculator.ui.theme.ConsumptionColors
import com.orwyx.unitcalculator.ui.theme.LocalNeuColors
import com.orwyx.unitcalculator.ui.theme.StatusDeepGreen
import com.orwyx.unitcalculator.ui.theme.StatusRed
import com.orwyx.unitcalculator.ui.theme.WarmAccent
import com.orwyx.unitcalculator.ui.theme.pressScale
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun MeterCard(
    meter: Meter,
    sequenceNumber: Int,
    phase: MeterPhase?,
    remainingDays: Int,
    allowDecimals: Boolean,
    isActive: Boolean,
    isClosed: Boolean,
    meterColor: androidx.compose.ui.graphics.Color? = null,
    modifier: Modifier = Modifier,
    reorderMode: Boolean = false,
    onClick: () -> Unit,
    onCurrentReadingSubmit: (Meter, String) -> Unit,
    onToggleActive: () -> Unit,
    onSetClosedDate: (LocalDate?) -> Unit,
) {
    var showCloseDatePicker by rememberSaveable(meter.id) { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "wiggle")
    val wiggleRotation by infiniteTransition.animateFloat(
        initialValue = if (sequenceNumber % 2 == 0) -2f else 2f,
        targetValue = if (sequenceNumber % 2 == 0) 2f else -2f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "wiggle",
    )

    Box(modifier = modifier.rotate(if (reorderMode) wiggleRotation else 0f)) {
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = if (reorderMode) ({}) else onClick,
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            "Meter $sequenceNumber",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            meter.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        )
                    }
                    StatusBadge(meter.status)
                    Spacer(Modifier.size(8.dp))
                    PowerButton(
                        isActive = isActive,
                        isClosed = isClosed,
                        onClick = if (reorderMode) ({}) else onToggleActive,
                    )
                }

                // Compact reorder row: ref number prominent
                AnimatedVisibility(
                    visible = reorderMode,
                    enter = expandVertically(animationSpec = tween(280, easing = EaseInOut)),
                    exit = shrinkVertically(animationSpec = tween(280, easing = EaseInOut)),
                ) {
                    Text(
                        text = Formatters.maskedReference(meter.referenceNumber),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }

                // Full card content
                AnimatedVisibility(
                    visible = !reorderMode,
                    enter = expandVertically(animationSpec = tween(280, easing = EaseInOut)),
                    exit = shrinkVertically(animationSpec = tween(280, easing = EaseInOut)),
                ) {
                    Column {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = Formatters.maskedReference(meter.referenceNumber),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Spacer(Modifier.height(16.dp))
                        AnimatedProgressBar(fraction = meter.usedFraction)

                        Spacer(Modifier.height(12.dp))
                        SafeBudgetChip(meter = meter, phase = phase, remainingDays = remainingDays, isActive = isActive, isClosed = isClosed)

                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Stat("Consumed", Formatters.units(meter.consumedUnits))
                            Stat("Remaining", Formatters.units(meter.remainingUnits))
                            Stat("Used", Formatters.percent(meter.usedFraction), valueColor = ConsumptionColors.colorFor(meter.usedFraction))
                            Stat("Target", Formatters.units(meter.targetLimit))
                        }

                        Spacer(Modifier.height(14.dp))
                        CurrentReadingRow(
                            meter = meter,
                            allowDecimals = allowDecimals,
                            isClosed = isClosed,
                            isReorderMode = reorderMode,
                            onSubmit = onCurrentReadingSubmit,
                            onCalendarClick = { showCloseDatePicker = true },
                            onClearClosedDate = { onSetClosedDate(null) },
                        )
                    }
                }
            }
        }
    }

    if (showCloseDatePicker) {
        CloseDatePickerDialog(
            initialDate = meter.closedDate,
            onConfirm = { date -> onSetClosedDate(date); showCloseDatePicker = false },
            onDismiss = { showCloseDatePicker = false },
        )
    }
}

@Composable
private fun PowerButton(isActive: Boolean, isClosed: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val isDark = LocalNeuColors.current.isDark
    val (tint, solidBg, accent) = when {
        isClosed -> Triple(Color.White, StatusRed, StatusRed)
        isActive -> Triple(Color.White, StatusDeepGreen, StatusDeepGreen)
        else -> Triple(Color.White.copy(alpha = 0.7f), if (isDark) Color(0xFF2D2D2D) else Color.White, WarmAccent)
    }
    Box(
        modifier = Modifier
            .size(36.dp)
            .pressScale(interaction, pressedScale = 0.88f)
            .clip(MaterialTheme.shapes.small)
            .background(solidBg)
            .accentGradientOverlay(accent = accent, cornerRadius = 8.dp)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Rounded.PowerSettingsNew,
            contentDescription = if (isClosed) "Closed meter" else if (isActive) "Active meter" else "Switch meter on",
            tint = tint, modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun ActivePill() {
    Row(
        modifier = Modifier.clip(MaterialTheme.shapes.small).background(StatusDeepGreen.copy(alpha = 0.14f)).padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(6.dp).clip(MaterialTheme.shapes.small).background(StatusDeepGreen))
        Spacer(Modifier.size(4.dp))
        Text("Active", style = MaterialTheme.typography.labelSmall, color = StatusDeepGreen, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ClosedPill() {
    Row(
        modifier = Modifier.clip(MaterialTheme.shapes.small).background(StatusRed.copy(alpha = 0.14f)).padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(6.dp).clip(MaterialTheme.shapes.small).background(StatusRed))
        Spacer(Modifier.size(4.dp))
        Text("Closed", style = MaterialTheme.typography.labelSmall, color = StatusRed, fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CloseDateIconButton(closedDate: LocalDate?, enabled: Boolean, onClick: () -> Unit, onClear: () -> Unit) {
    val fmt = remember { DateTimeFormatter.ofPattern("d MMM") }
    val interaction = remember { MutableInteractionSource() }
    val isDark = LocalNeuColors.current.isDark
    val hasDate = closedDate != null
    val solidBg = if (hasDate) StatusRed else if (isDark) Color(0xFF2D2D2D) else Color.White
    val accent = if (hasDate) StatusRed else WarmAccent
    val contentColor = Color.White
    Box(
        modifier = Modifier
            .width(72.dp)
            .height(52.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(solidBg)
            .accentGradientOverlay(accent = accent, cornerRadius = 12.dp)
            .pressScale(interaction, pressedScale = 0.88f)
            .combinedClickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                onClick = onClick,
                onDoubleClick = { if (hasDate) onClear() },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (hasDate) {
            Text(
                text = closedDate!!.format(fmt),
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                fontWeight = FontWeight.Bold,
            )
        } else {
            Icon(Icons.Rounded.CalendarMonth, contentDescription = "Set closed date", tint = contentColor, modifier = Modifier.size(22.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CloseDatePickerDialog(initialDate: LocalDate?, onConfirm: (LocalDate) -> Unit, onDismiss: () -> Unit) {
    val initialMillis = initialDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        ?: System.currentTimeMillis()
    val state = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { ms ->
                    onConfirm(Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate())
                } ?: onDismiss()
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    ) { DatePicker(state = state) }
}

@Composable
private fun CurrentReadingRow(
    meter: Meter,
    allowDecimals: Boolean,
    isClosed: Boolean,
    isReorderMode: Boolean,
    onSubmit: (Meter, String) -> Unit,
    onCalendarClick: () -> Unit,
    onClearClosedDate: () -> Unit,
) {
    val fieldDisabled = isClosed || isReorderMode
    var fieldValue by rememberSaveable(meter.id, meter.currentReading) {
        mutableStateOf(formatReading(meter.currentReading, allowDecimals))
    }
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val buttonInteraction = remember { MutableInteractionSource() }

    fun submit() {
        if (fieldDisabled) return
        val trimmed = fieldValue.trim()
        if (trimmed.isEmpty()) return
        onSubmit(meter, trimmed)
        keyboard?.hide()
        focusManager.clearFocus()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CloseDateIconButton(
            closedDate = meter.closedDate,
            enabled = !isReorderMode,
            onClick = onCalendarClick,
            onClear = onClearClosedDate,
        )
        OutlinedTextField(
            value = fieldValue, onValueChange = { fieldValue = it },
            label = {
                Text(
                    "Current reading",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            singleLine = true,
            enabled = !fieldDisabled, shape = MaterialTheme.shapes.medium,
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { submit() }),
            modifier = Modifier.weight(1f).heightIn(min = 52.dp).focusRequester(focusRequester),
        )
        val isDark = LocalNeuColors.current.isDark
        val solidBg = if (isDark) Color(0xFF2D2D2D) else Color.White
        Box(
            modifier = Modifier
                .width(72.dp)
                .height(52.dp)
                .pressScale(buttonInteraction)
                .clip(MaterialTheme.shapes.medium)
                .background(if (fieldDisabled) solidBg.copy(alpha = 0.4f) else solidBg)
                .accentGradientOverlay(cornerRadius = 12.dp)
                .clickable(
                    interactionSource = buttonInteraction,
                    indication = null,
                    enabled = !fieldDisabled,
                    onClick = { submit() },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "Calculate",
                style = MaterialTheme.typography.labelMedium,
                color = if (fieldDisabled) Color.White.copy(alpha = 0.45f) else Color.White,
            )
        }
    }
}

private fun formatReading(value: Double, allowDecimals: Boolean): String {
    if (allowDecimals) return if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
    return value.toLong().toString()
}

@Composable
private fun SafeBudgetChip(meter: Meter, phase: MeterPhase?, remainingDays: Int, isActive: Boolean, isClosed: Boolean) {
    val remaining = meter.remainingUnits
    val color = ConsumptionColors.colorFor(meter.usedFraction)
    val text = when {
        isClosed -> "${Formatters.units(remaining)} units left"
        !isActive -> if (phase != null && phase.isPending && phase.sequenceIndex > 0)
            "Waiting for Meter ${phase.sequenceIndex}"
        else
            "${Formatters.units(remaining)} units left"
        remaining <= 0.0 -> "Over by ${Formatters.units(-remaining)} units"
        phase == null -> {
            if (remainingDays > 0) "Stay under ${Formatters.units(remaining / remainingDays)} units/day - $remainingDays days left"
            else "${Formatters.units(remaining)} units left"
        }
        phase.isComplete -> "${Formatters.units(remaining)} units left"
        phase.remainingDaysInPhase > 0 ->
            "Stay under ${Formatters.units(remaining / phase.remainingDaysInPhase)} units/day - ${phase.remainingDaysInPhase} days left"
        else -> "${Formatters.units(remaining)} units left"
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = color,
        maxLines = 1,
        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 7.dp),
    )
}

@Composable
private fun Stat(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = valueColor)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
