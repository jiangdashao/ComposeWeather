package me.rerere.composeweather.repo

import me.rerere.composeweather.api.WeatherService
import me.rerere.composeweather.model.WeatherData
import java.lang.Exception

class WeatherRepo(private val weatherService: WeatherService) {
    // 获取该区域的天气数据
    suspend fun getWeather(location: String): WeatherData? = try{
        weatherService.getWeather(location)
    }catch (exception: Exception){
        exception.printStackTrace()
        null
    }
}