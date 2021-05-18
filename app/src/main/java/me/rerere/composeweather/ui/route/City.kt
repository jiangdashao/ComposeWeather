package me.rerere.composeweather.ui.route

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import me.rerere.composeweather.WeatherViewModel
import me.rerere.composeweather.model.Area
import java.util.*
import kotlin.math.roundToInt

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun CityPage(navController: NavController, weatherData: WeatherViewModel) {
    val areas by weatherData.weatherData.observeAsState(emptyList())
    val refreshState = rememberSwipeRefreshState(weatherData.isRefreshing)
    var showAdd by remember {
        mutableStateOf(false)
    }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
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
                            singleLine = true
                        )
                        IconButton(onClick = {
                            if (cityName.isNotEmpty() && cityName.isNotBlank()) {
                                weatherData.addArea(cityName)
                                scaffoldState.snackbarHostState.let {
                                    coroutineScope.launch {
                                        it.showSnackbar("已添加该城市")
                                    }
                                }

                                showAdd = false
                                cityName = ""
                            }
                        }) {
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
                        CityItem(area = it, weatherViewModel = weatherData)
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun CityItem(area: Area, weatherViewModel: WeatherViewModel) {
    val swipeState = rememberSwipeableState(initialValue = 0)
    val width = with(LocalDensity.current) { -120.dp.toPx() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    // 检测swipe状态开启后，调用requestFocus()设置焦点
    LaunchedEffect(swipeState.isAnimationRunning) {
        if (swipeState.targetValue == 1) {
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .swipeable(
                state = swipeState,
                anchors = mapOf(
                    0f to 0,
                    width to 1
                ),
                orientation = Orientation.Horizontal
            )
            .focusRequester(focusRequester)
            .onFocusChanged {
                // 失去焦点，拉回侧拉菜单
                if (it == FocusState.Inactive) {
                    coroutineScope.launch {
                        swipeState.animateTo(0)
                    }
                }
            }
            .focusModifier()
    ) {
        // 下层按钮
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .alpha(swipeState.offset.value / width),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                IconButton(onClick = {
                    if (!area.isCurrentLocation) {
                        weatherViewModel.removeArea(area)
                    } else {
                        Toast.makeText(context, "你不能删除你所在的城市！", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(Icons.Default.Delete, null)
                }
            }
        }

        // 上层卡片
        Card(
            modifier = Modifier
                .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessLow))
                .fillMaxSize()
                .padding(16.dp)
                .offset {
                    IntOffset(swipeState.offset.value.roundToInt(), 0)
                }
                .clickable(indication = null, interactionSource = remember {
                    MutableInteractionSource()
                }) {
                    // 点击的时候 展开/收起 卡片
                    coroutineScope.launch {
                        swipeState.animateTo(
                            when (swipeState.currentValue) {
                                0 -> 1
                                else -> 0
                            }
                        )
                    }
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
                            Text(area.name.toUpperCase(Locale.ROOT))
                            area.weather?.let {
                                Text(text = "${it.current.condition.text} ${it.forecast.forecastday.first().day.maxtempC}° /${it.forecast.forecastday.first().day.mintempC}°")
                            }
                        }
                        Box {
                            Text(
                                text = area.weather?.current?.tempC?.let { "$it °" }
                                    ?: "",
                                style = TextStyle.Default.copy(fontWeight = FontWeight.Bold),
                                fontSize = 25.sp
                            )
                        }
                    }
                }
            }
        }
    }
}