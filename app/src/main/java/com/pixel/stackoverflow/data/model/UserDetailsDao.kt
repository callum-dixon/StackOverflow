package com.pixel.stackoverflow.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pixel.stackoverflow.domain.model.UserDetails

// Could easily expand these methods to sort / filter in various ways
@Dao
interface UserDetailsDao {
    @Query("SELECT * FROM userdetails ORDER BY reputation DESC LIMIT :amount ")
    suspend fun getTopUsers(amount: Int): List<UserDetails>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(vararg items: UserDetails)
}