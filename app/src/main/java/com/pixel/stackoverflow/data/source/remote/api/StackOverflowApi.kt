package com.pixel.stackoverflow.data.source.remote.api

import com.pixel.stackoverflow.data.model.UserDetailsDto
import com.pixel.stackoverflow.data.model.mappers.Mapper
import com.pixel.stackoverflow.domain.model.UserDetails
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.path

// Api as interface to allow easy swapping - can mock network response while keeping all other layers intact if needed
interface StackOverflowApi {

    suspend fun fetchTopUsers(amount: Int): List<UserDetails>
}

class StackOverflowApiImpl(private val client: HttpClient) : StackOverflowApi {

    private val mapper = Mapper()

    // Could be expanded to take parameters from user input
    override suspend fun fetchTopUsers(amount: Int): List<UserDetails> {
        val response: UserDetailsDto = client.get {
            url {
                path("/users")
                parameters.append("page", "1")
                parameters.append("pagesize", amount.toString())
                parameters.append("order", "desc")
                parameters.append("sort", "reputation")
                parameters.append("site", "stackoverflow")
            }
        }.body()
        return mapper.mapUserDetails(response)
    }
}
