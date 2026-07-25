package com.orwyx.unitcalculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.orwyx.unitcalculator.domain.model.DayPlan
import com.orwyx.unitcalculator.ui.theme.ConsumptionColors
import com.orwyx.unitcalculator.ui.theme.MeterPalette
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

fun meterAccentColor(sequenceIndex: Int): Color = MeterPalette.colorFor(sequenceIndex)

@Composable
fun PlanningCalendar(
    days: List<DayPlan>,
    onDaySelected: (DayPlan) -> Unit,
    meterColorMap: Map<Long, Color>,
    selectedDay: DayPlan? = null,
    phaseSwitchDays: Set<Int> = emptySet(),
    modifier: Modifier = Modifier,
) {
    if (days.isEmpty()) return
    val weekdayHeaders = listOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY,
    )
    val leadingBlanks = (days.first().date.dayOfWeek.value + 6) % 7

    Column(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            weekdayHeaders.forEach { dow ->
                Text(
                    text = dow.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(1),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        val cells: List<DayPlan?> = List(leadingBlanks) { null } + days
        cells.chunked(7).forEach { week ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                week.forEach { day ->
                    Box(Modifier.weight(1f)) {
                        if (day != null) DayCell(
                            day = day,
                            meterColor = day.meterId?.let { meterColorMap[it] } ?: Color.Transparent,
                            isSelected = selectedDay?.date == day.date,
                            isSwitchDay = day.dayIndex in phaseSwitchDays,
                            onDaySelected = onDaySelected,
                        )
                    }
                }
                repeat(7 - week.size) { Box(Modifier.weight(1f)) {} }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun DayCell(
    day: DayPlan,
    meterColor: Color,
    isSelected: Boolean,
    isSwitchDay: Boolean,
    onDaySelected: (DayPlan) -> Unit,
) {
    val baseAlpha = if (day.isFuture) 0.22f else 0.60f
    val cellColor = if (meterColor != Color.Transparent)
        meterColor.copy(alpha = baseAlpha)
    else
        ConsumptionColors.colorFor(day.usedFraction).copy(alpha = baseAlpha * 0.5f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(cellColor)
            .then(if (day.isToday) Modifier.border(2.dp, meterColor.takeIf { it != Color.Transparent } ?: ConsumptionColors.colorFor(day.usedFraction), RoundedCornerShape(10.dp)) else Modifier)
            .then(if (isSelected && !day.isToday) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp)) else Modifier)
            .then(if (isSwitchDay) Modifier.border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), RoundedCornerShape(10.dp)) else Modifier)
            .clickable { onDaySelected(day) },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (day.isToday || isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (day.isFuture) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun CalendarLegend(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("Safe", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(0.dp))
        Box(
            Modifier
                .padding(horizontal = 8.dp)
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        (0..10).map { ConsumptionColors.colorFor(it / 10f) },
                    ),
                ),
        )
        Text("Over", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
