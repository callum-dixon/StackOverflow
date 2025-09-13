package com.pixel.stackoverflow.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// These should be split into new files given more time / as they grow
interface FollowingDataSource {

    val followingStream: Flow<List<String>>

    // References new value, eg if isFollowing = true, we're setting it to true
    suspend fun toggleFollowing(accountId: String, isFollowing: Boolean)
}

// For proper handling these should be in a list rather than as keys
// Also inject datastore with more time
class LocalFollowingDataSource(private val context: Context) : FollowingDataSource {

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "following")

    override val followingStream: Flow<List<String>> = context.dataStore.data.map { following ->
        following.asMap().keys.map { it.name }
    }

    // References new value, eg if isFollowing = true, we're setting it to true
    override suspend fun toggleFollowing(accountId: String, isFollowing: Boolean) {
        val prefKey = stringPreferencesKey(accountId)
        if (isFollowing) {
            context.dataStore.edit { following ->
                following[prefKey] = accountId
            }
        } else {
            context.dataStore.edit { following ->
                following.remove(prefKey)
            }
        }
    }
}