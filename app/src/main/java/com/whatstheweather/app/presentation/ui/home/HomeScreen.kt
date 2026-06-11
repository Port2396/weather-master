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
import com.whatstheweather.app.presentation.util.formatTemperature
import com.whatstheweather.app.presentation.util.formatTime
import com.whatstheweather.app.presentation.util.formatWindSpeed
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
    val settings by viewModel.settings.collectAsStateWithLifecycle()

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

    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            viewModel.refresh()
            isRefreshing = false
        }
    }

    // Get weather condition for background
    val condition = (uiState as? HomeUiState.Success)?.weatherData?.condition ?: WeatherCondition.CLEAR_SKY
    val actualIsDay = (uiState as? HomeUiState.Success)?.weatherData?.isDay ?: true
    // Light/Dark theme overrides the background's day/night look; System follows real time of day.
    val isDay = when (settings.theme) {
        AppTheme.LIGHT -> true
        AppTheme.DARK -> false
        AppTheme.SYSTEM -> actualIsDay
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { isRefreshing = true },
        state = pullRefreshState,
        modifier = Modifier.fillMaxSize()
    ) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Animated background
        WeatherBackground(
            condition = condition,
            isDay = isDay,
            animationsEnabled = settings.animatedBackground,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier
            .fillMaxSize()
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
                            settings = settings,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
    } // PullToRefreshBox
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
private fun WeatherContent(data: WeatherData, settings: AppSettings, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // City name
        Text(
            text = data.city.name + if (data.city.country.isNotBlank()) ", ${data.city.country}" else "",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Condition string
        Text(
            text = data.condition.toDisplayName(),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.75f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Big temperature
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = formatTemperature(data.currentTemp, settings.temperatureUnit).dropLast(1),
                fontSize = 96.sp,
                color = Color.White,
                fontFamily = com.whatstheweather.app.presentation.theme.OutfitFontFamily,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Light
            )
            Text(
                text = if (settings.temperatureUnit == TemperatureUnit.FAHRENHEIT) "°F" else "°C",
                fontSize = 36.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = com.whatstheweather.app.presentation.theme.OutfitFontFamily,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Light,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Feels like + H/L
        Text(
            text = "Feels like ${formatTemperature(data.feelsLike, settings.temperatureUnit)}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "H: ${formatTemperature(data.tempMax, settings.temperatureUnit)}  ·  L: ${formatTemperature(data.tempMin, settings.temperatureUnit)}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(28.dp))

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
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                StatItem(
                    label = "Humidity",
                    value = "${data.humidity}%",
                    icon = Icons.Outlined.WaterDrop,
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "Wind",
                    value = formatWindSpeed(data.windSpeed, settings.windSpeedUnit),
                    icon = Icons.Outlined.Air,
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "UV Index",
                    value = data.uvIndex.roundToInt().toString(),
                    icon = Icons.Outlined.WbSunny,
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "Rain",
                    value = "${data.precipitation}mm",
                    icon = Icons.Outlined.Opacity,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sunrise/sunset
        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                StatItem(
                    label = "Sunrise",
                    value = formatTime(data.sunrise, settings.timeFormat),
                    icon = Icons.Outlined.WbTwilight,
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "Sunset",
                    value = formatTime(data.sunset, settings.timeFormat),
                    icon = Icons.Outlined.Nightlight,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Air quality
        data.airQuality?.let { aqi ->
            Spacer(modifier = Modifier.height(16.dp))
            AqiCard(airQuality = aqi)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hourly
        HourlyForecastRow(
            hourlyForecasts = data.hourlyForecast,
            temperatureUnit = settings.temperatureUnit,
            timeFormat = settings.timeFormat
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Daily
        DailyForecastList(forecasts = data.dailyForecast, temperatureUnit = settings.temperatureUnit)

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.6f),
            maxLines = 1,
            textAlign = TextAlign.Center
        )
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
