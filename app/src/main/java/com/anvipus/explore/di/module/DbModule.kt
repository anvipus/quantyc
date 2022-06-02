package com.anvipus.explore.di.module

import android.app.Application
import androidx.room.Room
import com.anvipus.explore.db.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbModule{

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app.applicationContext, AppDatabase::class.java, "seno.db")
            .fallbackToDestructiveMigration()
            .build()
    }

}