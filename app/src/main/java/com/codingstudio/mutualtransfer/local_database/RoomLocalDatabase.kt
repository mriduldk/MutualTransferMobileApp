package com.codingstudio.mutualtransfer.local_database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.codingstudio.mutualtransfer.local_database.dao.DaoBlock
import com.codingstudio.mutualtransfer.local_database.dao.DaoDistrict
import com.codingstudio.mutualtransfer.local_database.dao.DaoRecentlyViewed
import com.codingstudio.mutualtransfer.local_database.dao.DaoSearchHistory
import com.codingstudio.mutualtransfer.local_database.dao.DaoUserDetails
import com.codingstudio.mutualtransfer.model.auth.UserDetails
import com.codingstudio.mutualtransfer.model.block.ModelBlock
import com.codingstudio.mutualtransfer.model.district.ModelDistrict
import com.codingstudio.mutualtransfer.model.search.ModelRecentlyViewed
import com.codingstudio.mutualtransfer.model.search.ModelSearchHistory


@Database(
    entities = [
        ModelSearchHistory::class,
        ModelDistrict::class,
        ModelBlock::class,
        ModelRecentlyViewed::class,
        UserDetails::class
    ],
    version = 11
)
abstract class RoomLocalDatabase : RoomDatabase() {

    abstract fun daoSearchHistory() : DaoSearchHistory
    abstract fun daoDistrict() : DaoDistrict
    abstract fun daoBlock() : DaoBlock
    abstract fun daoRecentlyViewed() : DaoRecentlyViewed
    abstract fun daoUserDetails() : DaoUserDetails


    companion object {

        @Volatile
        private var instance : RoomLocalDatabase?= null
        private var LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            RoomLocalDatabase::class.java,
            "com.codingstudio.mutualtransfer.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    }

}