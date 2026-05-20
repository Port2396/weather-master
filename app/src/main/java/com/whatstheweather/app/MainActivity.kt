package com.whatstheweather.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.whatstheweather.app.presentation.navigation.WeatherNavGraph
import com.whatstheweather.app.presentation.theme.WhatsTheWeatherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhatsTheWeatherTheme {
                WeatherNavGraph()
            }
        }
    }
}
