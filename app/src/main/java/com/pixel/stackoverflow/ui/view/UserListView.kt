package com.pixel.stackoverflow.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.pixel.stackoverflow.R
import com.pixel.stackoverflow.domain.model.UserDetails
import com.pixel.stackoverflow.ui.viewmodel.UserListViewModel
import com.pixel.stackoverflow.ui.viewmodel.UserListViewModel.UserListViewState.Error
import com.pixel.stackoverflow.ui.viewmodel.UserListViewModel.UserListViewState.Loading
import com.pixel.stackoverflow.ui.viewmodel.UserListViewModel.UserListViewState.Success
import org.koin.compose.viewmodel.koinViewModel

// Improvements: Remove hardcoded values (padding sizes etc)
// Top Bars are still marked experimental in API, could user Material2
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListView() {

    val userListViewModel = koinViewModel<UserListViewModel>()
    val viewState = userListViewModel.uiState.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val snackbarHostState = remember { SnackbarHostState() }

    // Separate scaffold per screen is easier when app gets large/complex
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },

                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        when (viewState.value) {
            is Success -> {
                val userDetails = (viewState.value as Success).toggleableUserList
                LazyColumn(modifier = Modifier.padding(innerPadding)) {
                    items(userDetails) { user ->
                        UserCard(
                            user.userDetails,
                            user.isFollowing,
                            onToggleFollow = {
                                userListViewModel.toggleFollowing(
                                    user.userDetails.accountId,
                                    !user.isFollowing
                                )
                            })
                        HorizontalDivider(modifier = Modifier.padding(start = 14.dp, end = 14.dp))
                    }
                }
            }

            Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // Snackbar showing here is just to simulate error UI
            // In prod would not remove all content on screen when internet drops
            Error -> {
                val errorString = stringResource(R.string.error_no_internet)
                val actionLabel = stringResource(R.string.dismiss)
                LaunchedEffect(true) {
                    snackbarHostState
                        .showSnackbar(
                            message = errorString,
                            actionLabel = actionLabel,
                            duration = SnackbarDuration.Short
                        )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = stringResource(R.string.error)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = stringResource(R.string.error_no_users))
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: UserDetails,
    isFollowing: Boolean,
    onToggleFollow: () -> Unit
) {
    ListItem(
        leadingContent = {
            AsyncImage(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(10.dp)
                    .clip(shape = RoundedCornerShape(10.dp)),
                model = user.profileImageUrl,
                contentDescription = user.displayName,
                clipToBounds = true,
                contentScale = ContentScale.Crop,
            )
        },
        headlineContent = {
            Text(
                fontSize = 14.sp,
                text = user.displayName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(
                fontSize = 14.sp,
                // String ideally should be formatted elsewhere if being reused a lot, potentially at the entity level
                text = stringResource(
                    R.string.reputation_formatted,
                    String.format(
                        LocalConfiguration.current.locales[0],
                        "%,d",
                        user.reputation
                    )
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        trailingContent = {
            Button(
                colors = if (isFollowing)
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                else
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                onClick = onToggleFollow,
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .fillMaxWidth(0.4f)
            ) {
                if (isFollowing) {
                    Text(text = stringResource(R.string.unfollow))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = stringResource(R.string.error),
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 2.dp)
                        )
                        Text(text = stringResource(R.string.follow))
                    }
                }
            }
        },
        modifier = Modifier.height(100.dp)
    )
}
