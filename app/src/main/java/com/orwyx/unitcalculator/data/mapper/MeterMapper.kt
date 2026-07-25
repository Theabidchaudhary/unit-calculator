package com.orwyx.unitcalculator.data.mapper

import com.orwyx.unitcalculator.data.local.entity.HistoryEntity
import com.orwyx.unitcalculator.data.local.entity.MeterEntity
import com.orwyx.unitcalculator.domain.model.Meter
import com.orwyx.unitcalculator.domain.model.MeterStatus
import com.orwyx.unitcalculator.domain.model.ReadingHistory
import java.time.LocalDate

fun MeterEntity.toDomain(): Meter = Meter(
    id = id, name = name, referenceNumber = referenceNumber, providerId = providerId,
    targetLimit = targetLimit, previousReading = previousReading, currentReading = currentReading,
    createdAt = createdAt, updatedAt = updatedAt, sortOrder = sortOrder,
    closedDate = if (closedDateEpochDay == 0L) null else LocalDate.ofEpochDay(closedDateEpochDay),
)

fun Meter.toEntity(): MeterEntity = MeterEntity(
    id = id, name = name, referenceNumber = referenceNumber, providerId = providerId,
    targetLimit = targetLimit, previousReading = previousReading, currentReading = currentReading,
    createdAt = createdAt, updatedAt = updatedAt, sortOrder = sortOrder,
    closedDateEpochDay = closedDate?.toEpochDay() ?: 0L,
)

fun HistoryEntity.toDomain(): ReadingHistory = ReadingHistory(
    id = id, meterId = meterId, monthLabel = monthLabel,
    previousReading = previousReading, currentReading = currentReading,
    unitsConsumed = unitsConsumed, target = target, remaining = remaining,
    billAmount = billAmount, avgDailyUsage = avgDailyUsage,
    status = runCatching { MeterStatus.valueOf(status) }.getOrDefault(MeterStatus.SAFE),
    closedAt = closedAt,
)
