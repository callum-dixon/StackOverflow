package com.pixel.stackoverflow.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity model geared towards UI usage
@Entity
data class UserDetails(
    @PrimaryKey val accountId: String,
    val displayName: String,
    val reputation: Long,
    val profileImageUrl: String
)