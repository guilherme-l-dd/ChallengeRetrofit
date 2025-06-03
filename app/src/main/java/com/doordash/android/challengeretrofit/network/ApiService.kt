package com.doordash.android.challengeretrofit.network

import com.doordash.android.challengeretrofit.data.ApiResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("test-endpoint")
    fun getTestData(): Call<ApiResponse>
}
