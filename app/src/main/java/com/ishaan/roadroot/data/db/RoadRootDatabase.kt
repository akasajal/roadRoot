package com.ishaan.roadroot.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ishaan.roadroot.model.Project
import com.ishaan.roadroot.model.ProjectAccent
import com.ishaan.roadroot.model.RoadmapItem

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE projects ADD COLUMN accentColor INTEGER NOT NULL DEFAULT ${ProjectAccent.GREEN.argb}"
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE roadmap_items ADD COLUMN dueDate INTEGER")
    }
}

@TypeConverters(Converters::class)
@Database(
    entities = [Project::class, RoadmapItem::class],
    version = 3,
    exportSchema = false
)
abstract class RoadRootDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun roadmapItemDao(): RoadmapItemDao
}
