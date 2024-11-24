package com.example.dz2

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://nekos.best/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: NekosApiService by lazy {
        retrofit.create(NekosApiService::class.java)
    }
}
