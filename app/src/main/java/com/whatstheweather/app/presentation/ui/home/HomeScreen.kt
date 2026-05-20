package com.whatstheweather.app.presentation.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.*
import com.whatstheweather.app.domain.model.*
import com.whatstheweather.app.presentation.ui.common.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onNavigateToCityManager: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val savedCities by viewModel.savedCities.collectAsStateWithLifecycle()
    val activeCityIndex by viewModel.activeCityIndex.collectAsStateWithLifecycle()

    val locationPermission = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(locationPermission.allPermissionsGranted) {
        if (locationPermission.allPermissionsGranted) viewModel.fetchCurrentLocation()
    }

    val pagerState = rememberPagerState(
        initialPage = activeCityIndex,
        pageCount = { maxOf(savedCities.size, 1) }
    )

    LaunchedEffect(pagerState.currentPage) {
        viewModel.setActiveCity(pagerState.currentPage)
    }

    val pullRefreshState = rememberPullToRefreshState()
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.refresh()
            pullRefreshState.endRefresh()
        }
    }

    // Get weather condition for background
    val condition = (uiState as? HomeUiState.Success)?.weatherData?.condition ?: WeatherCondition.CLEAR_SKY
    val isDay = (uiState as? HomeUiState.Success)?.weatherData?.isDay ?: true

    Box(modifier = Modifier.fillMaxSize()) {
        // Animated background
        WeatherBackground(condition = condition, isDay = isDay, modifier = Modifier.fillMaxSize())

        // Pull to refresh
        PullToRefreshContainer(state = pullRefreshState, modifier = Modifier.align(Alignment.TopCenter))

        Column(modifier = Modifier
            .fillMaxSize()
            .nestedScroll(pullRefreshState.nestedScrollConnection)
            .statusBarsPadding()
        ) {
            // Top bar
            HomeTopBar(
                cityCount = savedCities.size,
                currentPage = pagerState.currentPage,
                onCityManagerClick = onNavigateToCityManager,
                onSettingsClick = onNavigateToSettings
            )

            when (val state = uiState) {
                is HomeUiState.Loading -> LoadingContent()

                is HomeUiState.NoCitySaved -> NoCityContent(
                    onLocationPermissionRequest = { locationPermission.launchMultiplePermissionRequest() },
                    onAddCityClick = onNavigateToCityManager
                )

                is HomeUiState.LocationPermissionRequired -> NoCityContent(
                    onLocationPermissionRequest = { locationPermission.launchMultiplePermissionRequest() },
                    onAddCityClick = onNavigateToCityManager
                )

                is HomeUiState.Error -> ErrorContent(
                    message = state.message,
                    onRetry = { viewModel.refresh() }
                )

                is HomeUiState.Success -> {
                    // City pager dots (if multiple cities)
                    if (savedCities.size > 1) {
                        PagerIndicator(
                            count = savedCities.size,
                            current = pagerState.currentPage,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 4.dp)
                        )
                    }
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        WeatherContent(
                            data = state.weatherData,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    cityCount: Int,
    currentPage: Int,
    onCityManagerClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onCityManagerClick) {
            Icon(Icons.Outlined.LocationCity, "Cities", tint = Color.White)
        }
        Text(
            text = "What's the Weather",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
        IconButton(onClick = onSettingsClick) {
            Icon(Icons.Outlined.Settings, "Settings", tint = Color.White)
        }
    }
}

@Composable
private fun WeatherContent(data: WeatherData, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // City name
        Text(
            text = data.city.name + if (data.city.country.isNotBlank()) ", ${data.city.country}" else "",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Condition string
        Text(
            text = data.condition.toDisplayName(),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Big temperature
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = "${data.currentTemp.roundToInt()}",
                fontSize = 96.sp,
                color = Color.White,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Thin
            )
            Text(
                text = "°",
                fontSize = 48.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        // Feels like + H/L
        Text(
            text = "Feels like ${data.feelsLike.roundToInt()}°",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = "High ${data.tempMax.roundToInt()}°  •  Low ${data.tempMin.roundToInt()}°",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Condition icon large
        WeatherConditionIcon(
            condition = data.condition,
            isDay = data.isDay,
            size = 80.dp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Stats row
        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Humidity", value = "${data.humidity}%", icon = Icons.Outlined.WaterDrop)
                StatItem(label = "Wind", value = "${data.windSpeed.roundToInt()} km/h", icon = Icons.Outlined.Air)
                StatItem(label = "UV Index", value = data.uvIndex.roundToInt().toString(), icon = Icons.Outlined.WbSunny)
                StatItem(label = "Rain", value = "${data.precipitation}mm", icon = Icons.Outlined.Opacity)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sunrise/sunset
        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Sunrise", value = data.sunrise, icon = Icons.Outlined.WbTwilight)
                StatItem(label = "Sunset", value = data.sunset, icon = Icons.Outlined.Nightlight)
            }
        }

        // Air quality
        data.airQuality?.let { aqi ->
            Spacer(modifier = Modifier.height(12.dp))
            AqiCard(airQuality = aqi)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hourly
        HourlyForecastRow(hourlyForecasts = data.hourlyForecast)

        Spacer(modifier = Modifier.height(12.dp))

        // Daily
        DailyForecastList(forecasts = data.dailyForecast)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun StatItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = Color.White)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
    }
}

@Composable
private fun AqiCard(airQuality: AirQuality) {
    val category = airQuality.getCategory()
    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Air, null, tint = Color.White.copy(0.7f), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Air Quality", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(0.7f))
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = airQuality.europeanAqi.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White
                )
                Column {
                    Text(text = category, style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Text(text = "EU AQI  •  PM2.5: ${airQuality.pm25.roundToInt()} μg/m³", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.6f))
                }
            }
        }
    }
}

@Composable
private fun PagerIndicator(count: Int, current: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(count) { i ->
            Box(
                modifier = Modifier
                    .size(if (i == current) 8.dp else 5.dp)
                    .background(
                        color = if (i == current) Color.White else Color.White.copy(alpha = 0.4f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color.White)
            Spacer(Modifier.height(16.dp))
            Text("Loading weather...", color = Color.White.copy(0.7f))
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Outlined.CloudOff, null, tint = Color.White, modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text(message, color = Color.White, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = onRetry, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun NoCityContent(onLocationPermissionRequest: () -> Unit, onAddCityClick: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Outlined.LocationOn, null, tint = Color.White, modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text("No location set", style = MaterialTheme.typography.headlineSmall, color = Color.White, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text("Use your current location or add a city manually", color = Color.White.copy(0.7f), textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onLocationPermissionRequest) {
                Icon(Icons.Outlined.MyLocation, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Use My Location")
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onAddCityClick, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
                Icon(Icons.Outlined.Search, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Search for a City")
            }
        }
    }
}

private fun WeatherCondition.toDisplayName() = when (this) {
    WeatherCondition.CLEAR_SKY -> "Clear Sky"
    WeatherCondition.PARTLY_CLOUDY -> "Partly Cloudy"
    WeatherCondition.OVERCAST -> "Overcast"
    WeatherCondition.FOG -> "Foggy"
    WeatherCondition.DRIZZLE -> "Drizzle"
    WeatherCondition.RAIN -> "Rain"
    WeatherCondition.SNOW -> "Snow"
    WeatherCondition.RAIN_SHOWERS -> "Rain Showers"
    WeatherCondition.SNOW_SHOWERS -> "Snow Showers"
    WeatherCondition.THUNDERSTORM -> "Thunderstorm"
    WeatherCondition.THUNDERSTORM_WITH_HAIL -> "Thunderstorm with Hail"
    WeatherCondition.UNKNOWN -> "Unknown"
}
