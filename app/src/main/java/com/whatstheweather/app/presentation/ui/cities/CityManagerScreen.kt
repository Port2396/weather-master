package com.whatstheweather.app.presentation.ui.cities

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.whatstheweather.app.domain.model.City
import com.whatstheweather.app.presentation.theme.GradNightBottom
import com.whatstheweather.app.presentation.theme.GradNightTop
import com.whatstheweather.app.presentation.ui.common.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityManagerScreen(
    onBack: () -> Unit,
    viewModel: CityManagerViewModel = hiltViewModel()
) {
    val savedCities by viewModel.savedCities.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(GradNightTop, GradNightBottom)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, "Back", tint = Color.White)
                }
                Text(
                    text = "Manage Cities",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }

            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search for a city...", color = Color.White.copy(0.5f)) },
                leadingIcon = { Icon(Icons.Outlined.Search, null, tint = Color.White.copy(0.7f)) },
                trailingIcon = {
                    if (isSearching) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    else if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Outlined.Clear, null, tint = Color.White.copy(0.7f))
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White.copy(0.5f),
                    unfocusedBorderColor = Color.White.copy(0.2f),
                    cursorColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            // Search results
            AnimatedVisibility(
                visible = searchResults.isNotEmpty() && searchQuery.isNotEmpty(),
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Results",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(0.6f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(searchResults) { city ->
                            SearchResultItem(
                                city = city,
                                onAdd = { viewModel.saveCity(city) }
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            // Saved cities
            if (searchQuery.isEmpty()) {
                Text(
                    text = "Saved Cities",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(0.6f),
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(savedCities, key = { it.id }) { city ->
                        SavedCityItem(
                            city = city,
                            onDelete = { viewModel.deleteCity(city) }
                        )
                    }
                    if (savedCities.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No saved cities. Search above to add one.",
                                    color = Color.White.copy(0.5f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(city: City, onAdd: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 12.dp, alpha = 0.1f) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(city.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text(
                    listOfNotNull(city.admin1.takeIf { it.isNotBlank() }, city.country.takeIf { it.isNotBlank() }).joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.6f)
                )
            }
            IconButton(onClick = onAdd) {
                Icon(Icons.Outlined.AddCircleOutline, "Add", tint = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedCityItem(city: City, onDelete: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }
    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 12.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    if (city.isCurrentLocation) Icons.Outlined.MyLocation else Icons.Outlined.LocationOn,
                    null, tint = Color.White.copy(0.7f), modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(city.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                    if (city.country.isNotBlank()) {
                        Text(city.country, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.6f))
                    }
                }
            }
            if (!city.isCurrentLocation) {
                IconButton(onClick = { showConfirm = true }) {
                    Icon(Icons.Filled.Delete, "Delete", tint = Color.White.copy(0.6f))
                }
            }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Remove ${city.name}?") },
            text = { Text("This city will be removed from your saved list.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showConfirm = false }) { Text("Remove") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
