package com.ishaan.roadroot.data.db

import androidx.room.TypeConverter
import com.ishaan.roadroot.model.ItemStatus

class Converters {
    @TypeConverter
    fun fromItemStatus(status: ItemStatus): String = status.name

    @TypeConverter
    fun toItemStatus(value: String): ItemStatus = ItemStatus.valueOf(value)
}
