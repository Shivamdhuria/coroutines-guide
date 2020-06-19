package com.example.coroutines.main

import com.example.coroutines.main.data.DogDao
import com.example.coroutines.main.network.MainActivityApi
import com.example.coroutines.main.network.RemoteDataSource
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
internal class MainActivityModule {

    @Provides
    fun provideMainActivityRepository(dogDao: DogDao,dogRDS: RemoteDataSource, api: MainActivityApi): MainActivityRepository =
        MainActivityRepository(dogDao,dogRDS,api)

    @Provides
    fun provideEditProfileApi(retrofit: Retrofit): MainActivityApi = retrofit.create(MainActivityApi::class.java)

    @Provides
    fun provideRemoteDataSource(api: MainActivityApi): RemoteDataSource = RemoteDataSource(api)
}