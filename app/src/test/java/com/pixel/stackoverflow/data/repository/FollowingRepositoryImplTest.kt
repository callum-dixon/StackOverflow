package com.pixel.stackoverflow.data.repository

import com.pixel.stackoverflow.data.source.local.FollowingDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

class FollowingRepositoryImplTest {

    private lateinit var repository: FollowingRepositoryImpl
    private val localDataSource = mockk<FollowingDataSource>()

    @Before
    fun setUp() {
        repository = FollowingRepositoryImpl(localDataSource)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `followingStream returns local data when available`() {
        // Arrange
        val expectedFlow = flowOf(listOf("User 1", "User 2"))
        every { localDataSource.followingStream } returns (expectedFlow)

        // Act
        val result = repository.getFlow()

        // Assert
        assertEquals(expectedFlow, result)
    }

    // If taken further, data source could be faked using interface to verify toggle behaviour
    // by calling getFlow, checking value, toggling, and asserting it has changed
    @Test
    fun `toggleFollowing toggles data source when called`() = runTest {
        // Arrange
        val expectedAccountId = "User 1"
        val expectedFlow = flowOf(listOf(expectedAccountId))
        every { localDataSource.followingStream } returns (expectedFlow)
        coEvery { localDataSource.toggleFollowing(expectedAccountId, true) } returns Unit

        // Act
        repository.toggleFollowing(expectedAccountId, true)

        // Assert
        coVerify { localDataSource.toggleFollowing(expectedAccountId, true) }
    }
}