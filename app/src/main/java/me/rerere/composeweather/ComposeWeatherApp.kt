package me.rerere.composeweather

import android.app.Application
import me.rerere.composeweather.api.RetrofitClient
import me.rerere.composeweather.repo.WeatherRepo

class ComposeWeatherApp : Application() {
    companion object {
        lateinit var INSTANCE: Application

        val WEATHER_REPO: WeatherRepo by lazy {
            WeatherRepo(RetrofitClient.weatherService)
        }
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}