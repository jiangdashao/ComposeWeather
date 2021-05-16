package me.rerere.composeweather

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.app.Service
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.SideEffect
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import me.rerere.composeweather.ui.route.CityPage
import me.rerere.composeweather.ui.route.Index
import me.rerere.composeweather.ui.theme.ComposeWeatherTheme
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {
    private val weatherViewModel by viewModels<WeatherViewModel>()

    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Dexter.withContext(this)
            .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object: MultiplePermissionsListener{
                @SuppressLint("MissingPermission")
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0 != null) {
                        if(p0.areAllPermissionsGranted()){
                            val locationManager = getSystemService(Service.LOCATION_SERVICE) as LocationManager
                            locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                1000_0L,
                                0f
                            ) {
                                weatherViewModel.setCurrentLocation(it)
                            }
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    println("权限被拒绝！")
                    weatherViewModel.gps = false
                }

            })
            .check()

        setContent {
            ComposeWeatherTheme {
                val navController = rememberNavController()

                val systemUiController = rememberSystemUiController()
                val systemColor = MaterialTheme.colors.primary
                val isLight = false

                SideEffect {
                    systemUiController.apply {
                        // 设置导航栏
                        // 必须先设置导航栏再设置状态栏，否则在某些系统上（MIUI?）状态栏会显示白色icon :(
                        setNavigationBarColor(
                            color = systemColor,
                            darkIcons = isLight
                        )
                        // 设置状态栏
                        setStatusBarColor(
                            color = systemColor,
                            darkIcons = isLight
                        )
                    }
                }

                if(weatherViewModel.gps) {
                    NavHost(navController, "index") {
                        composable("index") {
                            Index(navController, weatherViewModel)
                        }

                        composable("area") {
                            CityPage(navController, weatherViewModel)
                        }
                    }
                }else {
                    Text(text = "请给与定位权限")
                }
            }
        }
    }
}