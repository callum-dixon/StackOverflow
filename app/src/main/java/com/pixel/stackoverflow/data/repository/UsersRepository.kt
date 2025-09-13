package com.pixel.stackoverflow.data.repository

import com.pixel.stackoverflow.data.source.local.database.AppDatabase
import com.pixel.stackoverflow.data.source.remote.UsersDataSource
import com.pixel.stackoverflow.domain.model.UserDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface UsersRepository {

    fun getTopUsers(amount: Int): Flow<List<UserDetails>>
}

// Main implementation
// If using offline first approach in future, expand it here. Abstract DB away
class UsersRepositoryImpl(
    private val database: AppDatabase,
    private val dataSource: UsersDataSource,
) : UsersRepository {

    // Emit from DB, then fetch remote + update DB
    override fun getTopUsers(amount: Int): Flow<List<UserDetails>> {
        return flow {
            val progressDao = database.userDetailsDao()
            emit(progressDao.getTopUsers(amount))

            try {
                val fetchedList = dataSource.getTopUsers(amount)
                progressDao.insertAll(*fetchedList.toTypedArray())
                emit(progressDao.getTopUsers(amount))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.flowOn(Dispatchers.IO)
    }
}