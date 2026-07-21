package com.ishaan.roadroot.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0,
    val accentColor: Int = ProjectAccent.GREEN.argb
)

/** Curated palette — each project picks one */
enum class ProjectAccent(val argb: Int, val label: String) {
    GREEN(Color(0xFF4ADE80).toArgb(),  "Green"),
    BLUE(Color(0xFF60A5FA).toArgb(),   "Blue"),
    PURPLE(Color(0xFFA78BFA).toArgb(), "Purple"),
    PINK(Color(0xFFF472B6).toArgb(),   "Pink"),
    ORANGE(Color(0xFFFB923C).toArgb(), "Orange"),
    YELLOW(Color(0xFFFBBF24).toArgb(), "Yellow"),
    RED(Color(0xFFF87171).toArgb(),    "Red"),
    CYAN(Color(0xFF22D3EE).toArgb(),   "Cyan");

    val color: Color get() = Color(argb)

    companion object {
        fun fromArgb(argb: Int): ProjectAccent =
            entries.firstOrNull { it.argb == argb } ?: GREEN
    }
}
