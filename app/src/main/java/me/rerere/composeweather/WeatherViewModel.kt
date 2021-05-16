package me.rerere.composeweather

import android.location.Location
import androidx.compose.runtime.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.rerere.composeweather.model.Area
import me.rerere.composeweather.model.Loc


class WeatherViewModel : ViewModel() {
    private val weatherRepo = ComposeWeatherApp.WEATHER_REPO

    // 天气数据
    private val _weatherData = MutableLiveData<List<Area>>()
    val weatherData: LiveData<List<Area>> = _weatherData

    // 当前位置
    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location>
        get() = _location

    // 定位
    var gps by mutableStateOf(true)

    // 是否正在刷新
    var isRefreshing by mutableStateOf(false)

    // 初始化
    init {
        _weatherData.value = listOf(
            // 北京
            Area("Beijing")
        )

        updateAllArena()
    }

    fun setCurrentLocation(location: Location) {
        _location.value = location

        val first = _weatherData.value?.first()
        first?.let { area ->
            if (area.location == null) {
                val list = _weatherData.value
                list?.let {
                    val newlist = ArrayList(list)
                    newlist.add(
                        0, Area(
                            "",
                            Loc(location.latitude, location.longitude),
                            isCurrentLocation = true
                        )
                    )

                    _weatherData.value = emptyList()
                    _weatherData.value = newlist

                    update(newlist[0])
                }
            }
        }
    }

    fun update(arena: Area) = viewModelScope.launch {
        isRefreshing = true

        weatherRepo.getWeather(arena.location?.toString() ?: arena.name)?.let { result ->
            withContext(Dispatchers.Main) {
                val list = ArrayList<Area>()
                _weatherData.value?.forEach {
                    if (it == arena) {
                        it.weather = result
                        it.name = result.location.name
                    }
                    list.add(it)
                }
                _weatherData.value = emptyList()
                _weatherData.value = list
            }
        }

        isRefreshing = false
    }

    fun updateAllArena() {
        _weatherData.value?.forEach(this@WeatherViewModel::update)
    }
}