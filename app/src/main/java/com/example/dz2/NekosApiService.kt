package com.example.dz2

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NekosApiService {
    @GET("neko")
    suspend fun getImages(
        @Query("amount") amount: Int = 1,
        @Query("page") page: Int
    ): Response<NekosApiResponse>
}

data class NekosApiResponse(
    val results: List<NekosImageData>
)

