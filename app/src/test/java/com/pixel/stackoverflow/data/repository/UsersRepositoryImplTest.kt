package com.pixel.stackoverflow.data.repository

import com.pixel.stackoverflow.data.model.UserDetailsDao
import com.pixel.stackoverflow.data.source.local.database.AppDatabase
import com.pixel.stackoverflow.data.source.remote.RemoteUsersDataSource
import com.pixel.stackoverflow.domain.model.UserDetails
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test


class UsersRepositoryImplTest {

    private lateinit var db: AppDatabase
    private lateinit var dataSource: RemoteUsersDataSource
    private lateinit var repository: UsersRepository

    @Before
    fun setUp() {
        db = mockk()
        dataSource = mockk()
        repository = UsersRepositoryImpl(db, dataSource)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getAllUsers returns correct list from sources`() = runTest {
        // Arrange
        val mockDao: UserDetailsDao = mockk(relaxed = true)
        every { db.userDetailsDao() } returns mockDao

        val userList = listOf(
            UserDetails(
                accountId = "1",
                displayName = "User 1",
                reputation = 10,
                profileImageUrl = "fakeUrl"
            ),
            UserDetails(
                accountId = "2",
                displayName = "User 2",
                reputation = 10,
                profileImageUrl = "fakeUrl"
            ),
        )

        coEvery { mockDao.getTopUsers(20) } returns userList
        coEvery { dataSource.getTopUsers(20) } returns userList

        coJustRun { mockDao.insertAll(*userList.toTypedArray()) }

        // Act
        val result = repository.getTopUsers(20).first()

        // Assert
        assertEquals(userList, result)
        coVerify(exactly = 2) { mockDao.getTopUsers(20) }
        coVerify(exactly = 1) { dataSource.getTopUsers(20) }
    }

    @Test
    fun `getAllUsers returns local data when network fails`() = runTest {
        // Arrange
        val mockDao: UserDetailsDao = mockk(relaxed = true)
        every { db.userDetailsDao() } returns mockDao

        val userList = listOf(
            UserDetails(
                accountId = "1",
                displayName = "User 1",
                reputation = 10,
                profileImageUrl = "fakeUrl"
            )
        )

        coEvery { mockDao.getTopUsers(20) } returns userList
        coEvery { dataSource.getTopUsers(20) } throws Exception("Network error")

        // Act
        val result = repository.getTopUsers(20).first()

        // Assert
        assertEquals(userList, result)
        coVerify(exactly = 1) { mockDao.getTopUsers(20) }
        coVerify(exactly = 1) { dataSource.getTopUsers(20) }
    }
}