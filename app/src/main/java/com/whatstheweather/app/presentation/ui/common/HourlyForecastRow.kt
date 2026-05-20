package com.whatstheweather.app.presentation.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whatstheweather.app.domain.model.HourlyForecast
import com.whatstheweather.app.domain.model.WeatherCondition
import kotlin.math.roundToInt

@Composable
fun HourlyForecastRow(
    hourlyForecasts: List<HourlyForecast>,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth(), cornerRadius = 16.dp) {
        Column {
            Text(
                text = "Hourly Forecast",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(hourlyForecasts) { forecast ->
                    HourlyItem(forecast = forecast)
                }
            }
        }
    }
}

@Composable
private fun HourlyItem(forecast: HourlyForecast) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = forecast.time.let {
                val parts = it.split(":")
                if (parts.size >= 2) "${parts[0]}:${parts[1]}" else it
            },
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
        WeatherConditionIcon(
            condition = forecast.condition,
            isDay = true,
            size = 28.dp
        )
        Text(
            text = "${forecast.temperature.roundToInt()}°",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        if (forecast.precipitationProbability > 0) {
            Text(
                text = "${forecast.precipitationProbability}%",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF64B5F6),
                fontSize = 10.sp
            )
        }
    }
}
