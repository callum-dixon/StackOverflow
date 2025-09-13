package com.pixel.stackoverflow.ui.viewmodel

import com.pixel.stackoverflow.data.repository.FollowingRepository
import com.pixel.stackoverflow.data.repository.UsersRepository
import com.pixel.stackoverflow.domain.model.ToggleableUser
import com.pixel.stackoverflow.domain.model.UserDetails
import com.pixel.stackoverflow.utils.NetworkObserver
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class UserListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var usersRepository: UsersRepository
    private lateinit var followingRepository: FollowingRepository
    private lateinit var networkObserver: NetworkObserver

    private lateinit var viewModel: UserListViewModel

    @Before
    fun setup() {
        usersRepository = mockk()
        followingRepository = mockk()
        networkObserver = mockk()
        coEvery { networkObserver.getConnectionFlow() } returns flowOf(true)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `state is initially Loading`() {
        // Arrange
        viewModel = UserListViewModel(
            usersRepository = FakeUserRepository(),
            followingRepository = DakeFollowingRepository(),
            networkObserver = networkObserver
        )

        // Assert
        assertEquals(UserListViewModel.UserListViewState.Loading, viewModel.uiState.value)
    }


    @Test
    // Viewmodel is defined in each test as takes different parameters in each
    fun `state is Error when empty UserList`() = runTest {
        // Arrange
        coEvery { usersRepository.getTopUsers(20) } returns flowOf(emptyList())
        viewModel = UserListViewModel(
            usersRepository = usersRepository,
            followingRepository = DakeFollowingRepository(),
            networkObserver = networkObserver
        )

        // Act
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // Assert
        assertEquals(
            UserListViewModel.UserListViewState.Error,
            viewModel.uiState.value,
        )
    }

    @Test
    fun `state is Error when no connection`() = runTest {
        // Arrange
        coEvery { networkObserver.getConnectionFlow() } returns flowOf(false)
        viewModel = UserListViewModel(
            usersRepository = FakeUserRepository(),
            followingRepository = DakeFollowingRepository(),
            networkObserver = networkObserver
        )

        // Act
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // Assert
        assertEquals(
            UserListViewModel.UserListViewState.Error,
            viewModel.uiState.value,
        )
    }

    @Test
    fun `state is Success when connected and valid data`() = runTest {
        // Arrange
        viewModel = UserListViewModel(
            usersRepository = FakeUserRepository(),
            followingRepository = DakeFollowingRepository(),
            networkObserver = networkObserver
        )

        // Act
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val expectedUserList = listOf(
            UserDetails(
                accountId = "fakeId",
                displayName = "fakeName",
                reputation = 0,
                profileImageUrl = "fakeUrl"
            )
        )

        // Assert
        assertEquals(
            UserListViewModel.UserListViewState.Success(
                listOf(
                    ToggleableUser(
                        expectedUserList[0],
                        false
                    )
                )
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `toggleFollowing calls toggleFollowing on repository`() = runTest {
        // Arrange
        val accountId = "12345"
        val isFollowing = true

        coEvery { followingRepository.getFlow() } returns flowOf()
        coEvery { followingRepository.toggleFollowing(any(), any()) } returns Unit

        viewModel = UserListViewModel(
            usersRepository = FakeUserRepository(),
            followingRepository = followingRepository,
            networkObserver = networkObserver
        )

        // Act
        viewModel.toggleFollowing(accountId, isFollowing)

        // Assert
        coVerify { followingRepository.toggleFollowing(accountId, true) }
    }

    // Example of some fake repository implementation
    inner class FakeUserRepository() : UsersRepository {

        override fun getTopUsers(amount: Int): Flow<List<UserDetails>> = flowOf(
            listOf(
                UserDetails(
                    accountId = "fakeId",
                    displayName = "fakeName",
                    reputation = 0,
                    profileImageUrl = "fakeUrl"
                )
            )
        )
    }

    inner class DakeFollowingRepository() : FollowingRepository {

        override fun getFlow(): Flow<List<String>> = flowOf(listOf())
        override suspend fun toggleFollowing(accountId: String, isFollowing: Boolean) {}
    }
}

// Reusable JUnit TestRule to override the Main dispatcher
// Would live somewhere else but there is only one test file here
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
