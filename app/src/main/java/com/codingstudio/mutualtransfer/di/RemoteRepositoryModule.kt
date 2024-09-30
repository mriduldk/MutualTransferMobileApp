package com.codingstudio.mutualtransfer.di

import com.codingstudio.mutualtransfer.repository.remote.AuthRepository
import com.codingstudio.mutualtransfer.repository.remote.BlockRepository
import com.codingstudio.mutualtransfer.repository.remote.DistrictRepository
import com.codingstudio.mutualtransfer.repository.remote.PaymentRepository
import com.codingstudio.mutualtransfer.repository.remote.UserDetailsRepository
import com.codingstudio.mutualtransfer.repository.remote.WalletRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RemoteRepositoryModule {

    @Provides
    fun provideUserDetailsRepository() : UserDetailsRepository {
        return UserDetailsRepository()
    }

    @Provides
    fun provideWalletRepository() : WalletRepository {
        return WalletRepository()
    }

    @Provides
    fun provideAuthRepository() : AuthRepository {
        return AuthRepository()
    }

    @Provides
    fun provideDistrictRepository() : DistrictRepository {
        return DistrictRepository()
    }

    @Provides
    fun provideBlockRepository() : BlockRepository {
        return BlockRepository()
    }

    @Provides
    fun providePaymentRepository() : PaymentRepository {
        return PaymentRepository()
    }



}