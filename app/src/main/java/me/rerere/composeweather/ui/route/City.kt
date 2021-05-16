package me.rerere.composeweather.ui.route

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import me.rerere.composeweather.WeatherViewModel
import java.util.*

@ExperimentalAnimationApi
@Composable
fun CityPage(navController: NavController, weatherData: WeatherViewModel) {
    val areas by weatherData.weatherData.observeAsState(emptyList())
    val refreshState = rememberSwipeRefreshState(weatherData.isRefreshing)
    var showAdd by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("城市管理")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAdd = !showAdd }) {
                        Crossfade(showAdd) {
                            if (it) {
                                Icon(Icons.Default.Close, null)
                            } else {
                                Icon(Icons.Default.Add, null)
                            }
                        }
                    }
                },
                elevation = if (showAdd) 0.dp else 2.dp
            )
        }
    ) {
        Column {
            AnimatedVisibility(showAdd) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colors.primary)
                        .fillMaxWidth()
                        .height(70.dp)
                ) {
                    var cityName by remember {
                        mutableStateOf("")
                    }
                    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                        TextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp),
                            value = cityName,
                            onValueChange = {
                                cityName = it
                            },
                            label = {
                                Text(text = "输入城市名字")
                            },
                            maxLines = 1
                        )

                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Default.Add, null)
                        }
                    }
                }
            }

            SwipeRefresh(state = refreshState, onRefresh = {
                weatherData.updateAllArena()
            }) {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(areas) {
                        var showButtons by remember {
                            mutableStateOf(false)
                        }
                        Card(
                            modifier = Modifier
                                .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessLow))
                                .fillMaxWidth()
                                .height(if (showButtons) 150.dp else 120.dp)
                                .padding(16.dp)
                                .clickable {
                                    showButtons = !showButtons
                                },
                            backgroundColor = MaterialTheme.colors.primaryVariant
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column {
                                    Row(
                                        Modifier
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(Modifier.weight(1f)) {
                                            Text(it.name.toUpperCase(Locale.ROOT))
                                            it.weather?.let {
                                                Text(text = "${it.current.condition.text} ${it.forecast.forecastday.first().day.maxtempC}° /${it.forecast.forecastday.first().day.mintempC}°")
                                            }
                                        }
                                        Box {
                                            Text(
                                                text = it.weather?.current?.tempC?.let { "$it °" }
                                                    ?: "",
                                                style = TextStyle.Default.copy(fontWeight = FontWeight.Bold),
                                                fontSize = 25.sp
                                            )
                                        }
                                    }

                                    if (showButtons) {
                                        Row(Modifier.fillMaxWidth()) {
                                            IconButton(modifier = Modifier.weight(1f), onClick = {
                                                // TODO: DELETE
                                            }) {
                                                Icon(Icons.Default.Delete, null)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}