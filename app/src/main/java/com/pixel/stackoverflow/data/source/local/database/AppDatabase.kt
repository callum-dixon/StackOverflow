package com.pixel.stackoverflow.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pixel.stackoverflow.data.model.UserDetailsDao
import com.pixel.stackoverflow.domain.model.UserDetails

@Database(entities = [UserDetails::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDetailsDao(): UserDetailsDao
}