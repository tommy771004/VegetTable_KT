package com.vegettable.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room — 本機價格提醒快取
 */
@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "remote_id")
    val remoteId: Int?,

    @ColumnInfo(name = "crop_name")
    val cropName: String,

    @ColumnInfo(name = "target_price")
    val targetPrice: Double,

    @ColumnInfo(name = "condition")
    val condition: String,   // "below" | "above"

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "last_triggered_at")
    val lastTriggeredAt: String?,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
