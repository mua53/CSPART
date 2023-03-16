package com.example.cspart.api

import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


object RetrofitClient {

//    private const val BASE_URL = "http://10.15.181.125:8080/api/"
//    private const val BASE_URL = "http://14.232.152.154:8084/api/"

    private var BASE_URL = "http://192.168.1.2:8084/api/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT))
        .addInterceptor { chain ->
            val original = chain.request()

            val requestBuilder = original.newBuilder()
                .method(original.method(), original.body())

            val request = requestBuilder.build()
            chain.proceed(request)
        }.
        build()

    fun changeApiBaseUrl(newApiBaseUrl: String) {
        BASE_URL = newApiBaseUrl
    }

    val instance: ApiInterface by lazy{
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        retrofit.create(ApiInterface::class.java)
    }

}