package com.codingstudio.mutualtransfer.di

import android.content.Context
import androidx.room.Room
import com.codingstudio.mutualtransfer.local_database.RoomLocalDatabase
import com.codingstudio.mutualtransfer.local_database.dao.DaoRecentlyViewed
import com.codingstudio.mutualtransfer.local_database.dao.DaoUserDetails
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun provideRoomLocalDatabase(@ApplicationContext appContext: Context): RoomLocalDatabase {
        return Room.databaseBuilder(
            appContext.applicationContext,
            RoomLocalDatabase::class.java,
            "com.codingstudio.mutualtransfer.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDetailsDao(database: RoomLocalDatabase): DaoUserDetails {
        return database.daoUserDetails()
    }

    @Provides
    fun provideRecentlyViewedDao(database: RoomLocalDatabase): DaoRecentlyViewed {
        return database.daoRecentlyViewed()
    }


}