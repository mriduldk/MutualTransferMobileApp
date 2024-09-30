package com.codingstudio.mutualtransfer

import android.app.Application
import com.codingstudio.mutualtransfer.local_database.RoomLocalDatabase
import com.codingstudio.mutualtransfer.local_database.dao.DaoSearchHistory
import com.codingstudio.mutualtransfer.local_database.dao.DaoSearchHistory_Impl
import com.codingstudio.mutualtransfer.repository.local.HistoryRepository
import com.codingstudio.mutualtransfer.repository.local.LocalUserDetailsRepository
import com.codingstudio.mutualtransfer.repository.local.RecentlyViewedRepository
import com.codingstudio.mutualtransfer.repository.remote.AuthRepository
import com.codingstudio.mutualtransfer.repository.remote.BlockRepository
import com.codingstudio.mutualtransfer.repository.remote.DistrictRepository
import com.codingstudio.mutualtransfer.repository.remote.PaymentRepository
import com.codingstudio.mutualtransfer.repository.remote.SearchRepository
import com.codingstudio.mutualtransfer.repository.remote.UserDetailsRepository
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {

    val authRepository by lazy { AuthRepository() }
    val userDetailsRepository by lazy { UserDetailsRepository() }
    val districtRepository by lazy { DistrictRepository() }
    val blockRepository by lazy { BlockRepository() }
    val searchRepository by lazy { SearchRepository() }
    val paymentRepository by lazy { PaymentRepository() }

    private val localDatabase by lazy { RoomLocalDatabase.invoke(this) }
    val historyRepository by lazy { HistoryRepository(localDatabase.daoSearchHistory()) }
    val recentlyViewedRepository by lazy { RecentlyViewedRepository(localDatabase.daoRecentlyViewed()) }
    val localUserDetailsRepository by lazy { LocalUserDetailsRepository(localDatabase.daoUserDetails()) }

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}