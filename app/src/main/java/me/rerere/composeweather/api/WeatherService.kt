package me.rerere.composeweather.api

import me.rerere.composeweather.model.WeatherData
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface WeatherService {
    @FormUrlEncoded
    @POST("forecast.json")
    suspend fun getWeather(
        @Field("q") location: String,
        @Field("days") days: Int = 3,
        @Field("aqi") aqi: String = "yes",
        @Field("alert") alert: String = "yes"
    ): WeatherData
}