package com.ishaan.roadroot.di

import android.content.Context
import androidx.room.Room
import com.ishaan.roadroot.data.db.MIGRATION_1_2
import com.ishaan.roadroot.data.db.MIGRATION_2_3
import com.ishaan.roadroot.data.db.ProjectDao
import com.ishaan.roadroot.data.db.RoadRootDatabase
import com.ishaan.roadroot.data.db.RoadmapItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RoadRootDatabase {
        return Room.databaseBuilder(
            context,
            RoadRootDatabase::class.java,
            "roadroot.db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }

    @Provides
    fun provideProjectDao(db: RoadRootDatabase): ProjectDao = db.projectDao()

    @Provides
    fun provideRoadmapItemDao(db: RoadRootDatabase): RoadmapItemDao = db.roadmapItemDao()
}
