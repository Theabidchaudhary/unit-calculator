package com.orwyx.unitcalculator.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.orwyx.unitcalculator.core.util.Formatters
import com.orwyx.unitcalculator.domain.model.Meter
import com.orwyx.unitcalculator.domain.model.MeterPhase
import com.orwyx.unitcalculator.ui.theme.ConsumptionColors
import com.orwyx.unitcalculator.ui.theme.StatusDeepGreen
import com.orwyx.unitcalculator.ui.theme.StatusRed
import com.orwyx.unitcalculator.ui.theme.pressScale
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

@Composable
fun MeterCard(
    meter: Meter,
    sequenceNumber: Int,
    phase: MeterPhase?,
    remainingDays: Int,
    allowDecimals: Boolean,
    isActive: Boolean,
    isClosed: Boolean,
    cycleStartDate: LocalDate,
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
        targetValue  = if (sequenceNumber % 2 == 0) 2f else -2f,
        animationSpec = infiniteRepeatable(
            animation  = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "wiggle",
    )

    Box(modifier = modifier.rotate(if (reorderMode) wiggleRotation else 0f)) {
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            onClick  = if (reorderMode) ({}) else onClick,
        ) {
            Column {
                Row(
                    modifier             = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment    = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(meter.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            "Meter $sequenceNumber",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    StatusBadge(meter.status)
                    Spacer(Modifier.size(8.dp))
                    PowerButton(
                        isActive = isActive,
                        isClosed = isClosed,
                        onClick  = if (reorderMode) ({}) else onToggleActive,
                    )
                }

                Spacer(Modifier.height(6.dp))
                var revealed by remember(meter.id) { mutableStateOf(false) }
                Text(
                    text     = if (revealed) meter.referenceNumber else Formatters.maskedReference(meter.referenceNumber),
                    style    = MaterialTheme.typography.bodyMedium,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable(enabled = !reorderMode) { revealed = !revealed },
                )

                Spacer(Modifier.height(16.dp))
                AnimatedProgressBar(fraction = meter.usedFraction)

                Spacer(Modifier.height(12.dp))
                SafeBudgetChip(meter = meter, phase = phase, remainingDays = remainingDays)

                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Stat("Consumed",  Formatters.units(meter.consumedUnits))
                    Stat("Remaining", Formatters.units(meter.remainingUnits))
                    Stat("Used",      Formatters.percent(meter.usedFraction), valueColor = ConsumptionColors.colorFor(meter.usedFraction))
                    Stat("Target",    Formatters.units(meter.targetLimit))
                }

                Spacer(Modifier.height(14.dp))
                CurrentReadingRow(
                    meter         = meter,
                    allowDecimals = allowDecimals,
                    isClosed      = isClosed || reorderMode,
                    onSubmit      = onCurrentReadingSubmit,
                    onCalendarClick    = { if (!reorderMode) showCloseDatePicker = true },
                    onClearClosedDate  = { if (!reorderMode) onSetClosedDate(null) },
                )
            }
        }
    }

    if (showCloseDatePicker) {
        CloseDatePickerDialog(
            initialDate    = meter.closedDate,
            cycleStartDate = cycleStartDate,
            onConfirm      = { date -> onSetClosedDate(date); showCloseDatePicker = false },
            onDismiss      = { showCloseDatePicker = false },
        )
    }
}

@Composable
private fun PowerButton(isActive: Boolean, isClosed: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val (tint, bg) = when {
        isClosed -> MaterialTheme.colorScheme.onPrimary to StatusRed
        isActive -> MaterialTheme.colorScheme.onPrimary to StatusDeepGreen
        else     -> MaterialTheme.colorScheme.onSurfaceVariant to MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }
    Box(
        modifier = Modifier
            .size(36.dp)
            .pressScale(interaction, pressedScale = 0.88f)
            .clip(MaterialTheme.shapes.small)
            .background(bg)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Rounded.PowerSettingsNew,
            contentDescription = if (isClosed) "Closed meter" else if (isActive) "Active meter" else "Switch meter on",
            tint     = tint,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun CloseDateIconButton(
    closedDate: LocalDate?,
    enabled: Boolean,
    calendarWidth: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
    onClear: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val hasDate     = closedDate != null
    val tint        = if (hasDate) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    val bg          = if (hasDate) StatusRed else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    Box(
        modifier = Modifier
            .height(58.dp)
            .width(calendarWidth)
            .clip(MaterialTheme.shapes.medium)
            .background(bg)
            .pressScale(interaction, pressedScale = 0.88f)
            .combinedClickable(
                interactionSource = interaction,
                indication        = null,
                enabled           = enabled,
                onClick           = onClick,
                onDoubleClick     = { if (hasDate) onClear() },
            )
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (hasDate) {
            Text(
                text       = closedDate!!.format(DateTimeFormatter.ofPattern("d MMM")),
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color      = tint,
                textAlign  = TextAlign.Center,
                maxLines   = 1,
            )
        } else {
            Icon(
                Icons.Rounded.CalendarMonth,
                contentDescription = "Set closed date",
                tint     = tint,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CloseDatePickerDialog(
    initialDate: LocalDate?,
    cycleStartDate: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val today         = LocalDate.now()
    val initialMillis = (initialDate ?: today)
        .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    // Dates selectable: from billing cycle start to today (no future dates)
    val selectableDates = remember(cycleStartDate, today) {
        val minDay = cycleStartDate.toEpochDay()
        val maxDay = today.toEpochDay()
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val day = utcTimeMillis / 86_400_000L
                return day in minDay..maxDay
            }
            override fun isSelectableYear(year: Int): Boolean =
                year in cycleStartDate.year..today.year
        }
    }

    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        selectableDates           = selectableDates,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton    = {
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
    onSubmit: (Meter, String) -> Unit,
    onCalendarClick: () -> Unit,
    onClearClosedDate: () -> Unit,
) {
    var fieldValue by rememberSaveable(meter.id, meter.currentReading) {
        mutableStateOf(formatReading(meter.currentReading, allowDecimals))
    }
    var isError by rememberSaveable(meter.id) { mutableStateOf(false) }
    val shakeAnim     = remember { Animatable(0f) }
    val scope         = rememberCoroutineScope()
    val keyboard      = LocalSoftwareKeyboardController.current
    val focusManager  = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val buttonInteraction = remember { MutableInteractionSource() }
    var buttonWidthPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    fun submit() {
        if (isClosed) return
        val trimmed = fieldValue.trim()
        if (trimmed.isEmpty()) return
        val parsed = trimmed.toDoubleOrNull()
        if (parsed != null && parsed < meter.previousReading) {
            isError = true
            scope.launch {
                for (target in listOf(10f, -10f, 8f, -8f, 5f, -5f, 0f)) {
                    shakeAnim.animateTo(target, tween(55, easing = LinearEasing))
                }
            }
            return
        }
        isError = false
        onSubmit(meter, trimmed)
        keyboard?.hide()
        focusManager.clearFocus()
    }

    Row(
        modifier             = Modifier.fillMaxWidth(),
        verticalAlignment    = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val calendarWidth = if (buttonWidthPx > 0) with(density) { buttonWidthPx.toDp() } else 72.dp
        CloseDateIconButton(
            closedDate    = meter.closedDate,
            enabled       = !isClosed,
            calendarWidth = calendarWidth,
            onClick       = onCalendarClick,
            onClear       = onClearClosedDate,
        )
        OutlinedTextField(
            value          = fieldValue,
            onValueChange  = { fieldValue = it; isError = false },
            label          = { Text("Current reading", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
            isError        = isError,
            singleLine     = true,
            enabled        = !isClosed,
            shape          = MaterialTheme.shapes.medium,
            textStyle      = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { submit() }),
            modifier        = Modifier
                .weight(1f)
                .height(58.dp)
                .focusRequester(focusRequester)
                .offset { IntOffset(shakeAnim.value.roundToInt(), 0) },
        )
        Button(
            onClick           = { submit() },
            interactionSource = buttonInteraction,
            enabled           = !isClosed,
            modifier          = Modifier
                .height(58.dp)
                .pressScale(buttonInteraction)
                .onSizeChanged { buttonWidthPx = it.width },
            shape             = MaterialTheme.shapes.medium,
            contentPadding    = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp),
        ) { Text("Calculate", style = MaterialTheme.typography.labelMedium) }
    }
}

private fun formatReading(value: Double, allowDecimals: Boolean): String {
    if (allowDecimals) return if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
    return value.toLong().toString()
}

@Composable
private fun SafeBudgetChip(meter: Meter, phase: MeterPhase?, remainingDays: Int) {
    val remaining = meter.remainingUnits
    val color     = ConsumptionColors.colorFor(meter.usedFraction)
    val text = when {
        remaining <= 0.0 -> "Over limit by ${Formatters.units(-remaining)} units"
        phase == null -> {
            if (remainingDays > 0) "≈ ${Formatters.units(remaining / remainingDays)} units/day left to stay safe"
            else "${Formatters.units(remaining)} units left this cycle"
        }
        phase.isComplete -> "${Formatters.units(remaining)} units left until threshold"
        phase.isPending  -> "Not started yet — waiting for meter ${phase.sequenceIndex}"
        phase.remainingDaysInPhase > 0 ->
            "≈ ${Formatters.units(remaining / phase.remainingDaysInPhase)} units/day (${phase.remainingDaysInPhase}d left in phase)"
        phase.isActive && remainingDays > 0 ->
            "≈ ${Formatters.units(remaining / remainingDays)} units/day left to stay safe"
        else -> "${Formatters.units(remaining)} units remaining in phase"
    }
    Text(
        text     = text,
        style    = MaterialTheme.typography.labelLarge,
        color    = color,
        modifier = Modifier
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
