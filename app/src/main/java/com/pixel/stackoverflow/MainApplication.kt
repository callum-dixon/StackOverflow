package com.pixel.stackoverflow

import android.app.Application
import androidx.room.Room
import com.pixel.stackoverflow.data.network.KtorClient
import com.pixel.stackoverflow.data.repository.FollowingRepository
import com.pixel.stackoverflow.data.repository.FollowingRepositoryImpl
import com.pixel.stackoverflow.data.repository.UsersRepository
import com.pixel.stackoverflow.data.repository.UsersRepositoryImpl
import com.pixel.stackoverflow.data.source.local.FollowingDataSource
import com.pixel.stackoverflow.data.source.local.LocalFollowingDataSource
import com.pixel.stackoverflow.data.source.local.database.AppDatabase
import com.pixel.stackoverflow.data.source.remote.RemoteUsersDataSource
import com.pixel.stackoverflow.data.source.remote.UsersDataSource
import com.pixel.stackoverflow.data.source.remote.api.StackOverflowApi
import com.pixel.stackoverflow.data.source.remote.api.StackOverflowApiImpl
import com.pixel.stackoverflow.ui.viewmodel.UserListViewModel
import com.pixel.stackoverflow.utils.NetworkObserver
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Koin modules. If project grows, split the modules and have them located closer to where they are used
        startKoin {
            androidContext(applicationContext)
            val appModule = module {
                single { KtorClient().getClient() }
                single { NetworkObserver(get()) }
                single { RemoteUsersDataSource(get()) }
                single { LocalFollowingDataSource(get()) }

                singleOf(::UsersRepositoryImpl) { bind<UsersRepository>() }
                singleOf(::FollowingRepositoryImpl) { bind<FollowingRepository>() }

                singleOf(::RemoteUsersDataSource) { bind<UsersDataSource>() }
                singleOf(::LocalFollowingDataSource) { bind<FollowingDataSource>() }

                singleOf(::StackOverflowApiImpl) { bind<StackOverflowApi>() }

                single<AppDatabase> {
                    Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        AppDatabase::class.java.name
                    ).build()
                }
                single { get<AppDatabase>().userDetailsDao() }

                viewModel { UserListViewModel(get(), get(), get()) }
            }

            modules(appModule)
        }
    }
}