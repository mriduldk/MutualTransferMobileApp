package com.codingstudio.mutualtransfer.di

import com.codingstudio.mutualtransfer.repository.remote.AuthRepository
import com.codingstudio.mutualtransfer.repository.remote.BlockRepository
import com.codingstudio.mutualtransfer.repository.remote.CurrentRoleRepository
import com.codingstudio.mutualtransfer.repository.remote.DepartmentRepository
import com.codingstudio.mutualtransfer.repository.remote.DistrictRepository
import com.codingstudio.mutualtransfer.repository.remote.MessageRepository
import com.codingstudio.mutualtransfer.repository.remote.PaymentRepository
import com.codingstudio.mutualtransfer.repository.remote.StateRepository
import com.codingstudio.mutualtransfer.repository.remote.SubjectRepository
import com.codingstudio.mutualtransfer.repository.remote.UserDetailsNewRepository
import com.codingstudio.mutualtransfer.repository.remote.UserDetailsRepository
import com.codingstudio.mutualtransfer.repository.remote.UserTypeRepository
import com.codingstudio.mutualtransfer.repository.remote.WalletRepository
import com.codingstudio.mutualtransfer.repository.remote.ZoneDivisionRepository
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
    fun provideUserDetailsNewRepository() : UserDetailsNewRepository {
        return UserDetailsNewRepository()
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
    fun provideStateRepository() : StateRepository {
        return StateRepository()
    }

    @Provides
    fun provideBlockRepository() : BlockRepository {
        return BlockRepository()
    }

    @Provides
    fun providePaymentRepository() : PaymentRepository {
        return PaymentRepository()
    }

    @Provides
    fun provideSubjectRepository() : SubjectRepository {
        return SubjectRepository()
    }
    @Provides
    fun provideMessageRepository() : MessageRepository {
        return MessageRepository()
    }
    @Provides
    fun provideCurrentRoleRepository() : CurrentRoleRepository {
        return CurrentRoleRepository()
    }
    @Provides
    fun provideDepartmentRepository() : DepartmentRepository {
        return DepartmentRepository()
    }
    @Provides
    fun provideZoneDivisionRepository() : ZoneDivisionRepository {
        return ZoneDivisionRepository()
    }
    @Provides
    fun provideUserTypeRepository() : UserTypeRepository {
        return UserTypeRepository()
    }



}