package com.pixel.stackoverflow.data.model.mappers

import com.pixel.stackoverflow.data.model.UserDetailsDto
import com.pixel.stackoverflow.domain.model.UserDetails

// Very helpful to have DTO mappers when network objects get large & local models differ slightly
class Mapper() {

    fun mapUserDetails(dto: UserDetailsDto): List<UserDetails> =
        dto.items.map { item ->
            UserDetails(
                accountId = item.accountID,
                displayName = item.displayName,
                reputation = item.reputation,
                profileImageUrl = item.profileImage
            )
        }
}
