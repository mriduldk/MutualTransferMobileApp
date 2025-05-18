package com.codingstudio.mutualtransfer.local_database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codingstudio.mutualtransfer.local_database.dao.DaoBlock
import com.codingstudio.mutualtransfer.local_database.dao.DaoDistrict
import com.codingstudio.mutualtransfer.local_database.dao.DaoRecentlyViewed
import com.codingstudio.mutualtransfer.local_database.dao.DaoRecentlyViewedNew
import com.codingstudio.mutualtransfer.local_database.dao.DaoSearchHistory
import com.codingstudio.mutualtransfer.local_database.dao.DaoUserDetails
import com.codingstudio.mutualtransfer.local_database.dao.DaoUserDetailsNew
import com.codingstudio.mutualtransfer.model.auth.UserDetails
import com.codingstudio.mutualtransfer.model.auth.UserDetailsNew
import com.codingstudio.mutualtransfer.model.block.ModelBlock
import com.codingstudio.mutualtransfer.model.district.ModelDistrict
import com.codingstudio.mutualtransfer.model.search.ModelRecentlyViewed
import com.codingstudio.mutualtransfer.model.search.ModelRecentlyViewedNew
import com.codingstudio.mutualtransfer.model.search.ModelSearchHistory


@Database(
    entities = [
        ModelSearchHistory::class,
        ModelDistrict::class,
        ModelBlock::class,
        ModelRecentlyViewed::class,
        ModelRecentlyViewedNew::class,
        UserDetails::class,
        UserDetailsNew::class
    ],
    version = 15
)
abstract class RoomLocalDatabase : RoomDatabase() {

    abstract fun daoSearchHistory() : DaoSearchHistory
    abstract fun daoDistrict() : DaoDistrict
    abstract fun daoBlock() : DaoBlock
    abstract fun daoRecentlyViewed() : DaoRecentlyViewed
    abstract fun daoRecentlyViewedNew() : DaoRecentlyViewedNew
    abstract fun daoUserDetails() : DaoUserDetails
    abstract fun daoUserDetailsNew() : DaoUserDetailsNew


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
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Your initialization logic here
                }
            })
            .build()

    }

}