package me.rerere.composeweather.api

import me.rerere.composeweather.util.HttpClientBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://api.weatherapi.com/v1/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(HttpClientBuilder.getHttpClient())
        .build()

    val weatherService: WeatherService = retrofit.create(WeatherService::class.java)
}