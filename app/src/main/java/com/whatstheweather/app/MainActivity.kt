package com.whatstheweather.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.whatstheweather.app.presentation.AppThemeViewModel
import com.whatstheweather.app.presentation.navigation.WeatherNavGraph
import com.whatstheweather.app.presentation.theme.WhatsTheWeatherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: AppThemeViewModel = hiltViewModel()
            val appTheme by themeViewModel.theme.collectAsStateWithLifecycle()

            WhatsTheWeatherTheme(appTheme = appTheme) {
                WeatherNavGraph()
            }
        }
    }
}
