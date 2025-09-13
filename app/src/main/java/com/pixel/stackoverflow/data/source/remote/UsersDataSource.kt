package com.pixel.stackoverflow.data.source.remote

import com.pixel.stackoverflow.data.source.remote.api.StackOverflowApi
import com.pixel.stackoverflow.domain.model.UserDetails

// These should be split into new files given more time / as they grow
interface UsersDataSource {

    suspend fun getTopUsers(amount: Int): List<UserDetails>
}

// Class implementation here as only one data source, could be an interface if other sources added
class RemoteUsersDataSource(private val stackOverflowApi: StackOverflowApi) : UsersDataSource {

    // Again, from network api
    override suspend fun getTopUsers(amount: Int): List<UserDetails> =
        stackOverflowApi.fetchTopUsers(amount)
}

