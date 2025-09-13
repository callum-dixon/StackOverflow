package com.pixel.stackoverflow.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDetailsDto(
    val items: List<Item>,

    @SerialName("has_more")
    val hasMore: Boolean,

    @SerialName("quota_max")
    val quotaMax: Long,

    @SerialName("quota_remaining")
    val quotaRemaining: Long
) {
    @Serializable
    data class Item(
        @SerialName("badge_counts")
        val badgeCounts: BadgeCounts,

        val collectives: List<CollectiveElement>? = emptyList(),

        @SerialName("account_id")
        val accountID: String,

        @SerialName("is_employee")
        val isEmployee: Boolean,

        @SerialName("last_modified_date")
        val lastModifiedDate: Long,

        @SerialName("last_access_date")
        val lastAccessDate: Long,

        @SerialName("reputation_change_year")
        val reputationChangeYear: Long,

        @SerialName("reputation_change_quarter")
        val reputationChangeQuarter: Long,

        @SerialName("reputation_change_month")
        val reputationChangeMonth: Long,

        @SerialName("reputation_change_week")
        val reputationChangeWeek: Long,

        @SerialName("reputation_change_day")
        val reputationChangeDay: Long,

        val reputation: Long,

        @SerialName("creation_date")
        val creationDate: Long,

        @SerialName("user_type")
        val userType: String,

        @SerialName("user_id")
        val userID: Long,

        @SerialName("accept_rate")
        val acceptRate: Long? = null,

        val location: String? = null,

        @SerialName("website_url")
        val websiteURL: String,

        val link: String,

        @SerialName("profile_image")
        val profileImage: String,

        @SerialName("display_name")
        val displayName: String
    )

    @Serializable
    data class BadgeCounts(
        val bronze: Long,
        val silver: Long,
        val gold: Long
    )

    @Serializable
    data class CollectiveElement(
        val collective: CollectiveCollective,
        val role: String
    )

    @Serializable
    data class CollectiveCollective(
        val tags: List<String>,

        @SerialName("external_links")
        val externalLinks: List<ExternalLink>,

        val description: String,
        val link: String,
        val name: String,
        val slug: String
    )

    @Serializable
    data class ExternalLink(
        val type: String,
        val link: String
    )
}