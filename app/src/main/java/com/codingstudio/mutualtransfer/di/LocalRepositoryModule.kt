package com.codingstudio.mutualtransfer.di

import com.codingstudio.mutualtransfer.local_database.RoomLocalDatabase
import com.codingstudio.mutualtransfer.local_database.dao.DaoRecentlyViewed
import com.codingstudio.mutualtransfer.local_database.dao.DaoRecentlyViewedNew
import com.codingstudio.mutualtransfer.local_database.dao.DaoUserDetails
import com.codingstudio.mutualtransfer.local_database.dao.DaoUserDetailsNew
import com.codingstudio.mutualtransfer.repository.local.LocalUserDetailsNewRepository
import com.codingstudio.mutualtransfer.repository.local.LocalUserDetailsRepository
import com.codingstudio.mutualtransfer.repository.local.RecentlyViewedNewRepository
import com.codingstudio.mutualtransfer.repository.local.RecentlyViewedRepository
import com.codingstudio.mutualtransfer.repository.remote.UserDetailsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class LocalRepositoryModule {

    @Provides
    fun provideLocalUserDetailsRepository(userDetailsDao: DaoUserDetails): LocalUserDetailsRepository {
        return LocalUserDetailsRepository(userDetailsDao)
    }

    @Provides
    fun provideLocalUserDetailsNewRepository(userDetailsNewDao: DaoUserDetailsNew): LocalUserDetailsNewRepository {
        return LocalUserDetailsNewRepository(userDetailsNewDao)
    }

    @Provides
    fun provideRecentlyViewedRepository(daoRecentlyViewed: DaoRecentlyViewed): RecentlyViewedRepository {
        return RecentlyViewedRepository(daoRecentlyViewed)
    }

    @Provides
    fun provideRecentlyViewedNewRepository(daoRecentlyViewed: DaoRecentlyViewedNew): RecentlyViewedNewRepository {
        return RecentlyViewedNewRepository(daoRecentlyViewed)
    }

}