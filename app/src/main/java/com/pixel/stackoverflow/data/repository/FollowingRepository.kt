package com.pixel.stackoverflow.data.repository

import com.pixel.stackoverflow.data.source.local.FollowingDataSource
import kotlinx.coroutines.flow.Flow

interface FollowingRepository {

    fun getFlow(): Flow<List<String>>

    suspend fun toggleFollowing(accountId: String, isFollowing: Boolean)
}

// Main implementation
class FollowingRepositoryImpl(private val dataSource: FollowingDataSource) :
    FollowingRepository {

    override fun getFlow(): Flow<List<String>> = dataSource.followingStream

    override suspend fun toggleFollowing(accountId: String, isFollowing: Boolean) {
        dataSource.toggleFollowing(accountId, isFollowing)
    }
}