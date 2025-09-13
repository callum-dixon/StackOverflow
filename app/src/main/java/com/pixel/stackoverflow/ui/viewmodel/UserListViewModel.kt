package com.pixel.stackoverflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixel.stackoverflow.data.repository.FollowingRepository
import com.pixel.stackoverflow.data.repository.UsersRepository
import com.pixel.stackoverflow.domain.model.ToggleableUser
import com.pixel.stackoverflow.utils.NetworkObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserListViewModel(
    usersRepository: UsersRepository,
    private val followingRepository: FollowingRepository,
    networkObserver: NetworkObserver
) : ViewModel() {

    // Create UI item flow from combined repos
    private val combinedUserFlow: Flow<List<ToggleableUser>> = combine(
        // Number of users is defined here, but could be passed in from UI
        usersRepository.getTopUsers(20),
        followingRepository.getFlow(),
    ) { users, following ->
        users.map { user ->
            val isFollowing = following.contains(user.accountId)
            ToggleableUser(user, isFollowing)
        }
    }

    // Create UI flow with 5 sec timeout
    val uiState: StateFlow<UserListViewState> = combine(
        combinedUserFlow,
        networkObserver.getConnectionFlow()
    ) { toggleableUsers, isConnected ->

        // Check connection here, could get fancy with showing offline data, disabling actions etc
        // Eg keep showing list but disable buttons
        // Just showing error on network drop for time constraint
        if (toggleableUsers.isEmpty() || !isConnected) {
            UserListViewState.Error
        } else {
            UserListViewState.Success(toggleableUsers)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserListViewState.Loading
    )

    fun toggleFollowing(accountId: String, isFollowing: Boolean) {
        viewModelScope.launch {
            followingRepository.toggleFollowing(accountId, isFollowing)
        }
    }

    sealed interface UserListViewState {
        data object Loading :
            UserListViewState

        data object Error :
            UserListViewState

        data class Success(
            val toggleableUserList: List<ToggleableUser>
        ) : UserListViewState
    }
}