package com.pixel.stackoverflow.domain.model

data class ToggleableUser(
    val userDetails: UserDetails,
    val isFollowing: Boolean,
)