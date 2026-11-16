@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.bookshelfprefs.ui.theme

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bookshelfprefs.data.Book
import com.example.bookshelfprefs.data.ReadFilter
import com.example.bookshelfprefs.data.SortOrder
import com.example.bookshelfprefs.data.UserPreferences

/** Callbacks for every setting the user can change. */
data class PreferenceActions(
    val onReadFilterChange: (ReadFilter) -> Unit,
    val onSortOrderChange: (SortOrder) -> Unit,
    val onDarkThemeChange: (Boolean) -> Unit,
    val onHideLowRatedChange: (Boolean) -> Unit,
    val onResetToDefaults: () -> Unit,
)

@Composable
fun BookListScreen(uiState: BookListUiState, actions: PreferenceActions) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BookShelf") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
    ) { innerPadding ->
        when (uiState) {
            BookListUiState.Loading -> Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is BookListUiState.Ready -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { SavedSettingsCard(uiState.preferences) }
                item { ControlsCard(uiState.preferences, actions) }
                item {
                    Text(
                        "Showing ${uiState.books.size} of ${uiState.totalBooks} books",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (uiState.books.isEmpty()) {
                    item {
                        Text(
                            "No books match these settings.",
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                items(uiState.books, key = { it.id }) { book -> BookRow(book) }
            }
        }
    }
}

/** Shows exactly what is saved, which proves the settings survived a restart. */
@Composable
private fun SavedSettingsCard(prefs: UserPreferences) {
    val isDefault = prefs == UserPreferences()
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Saved settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Filter: ${prefs.readFilter.label}")
            Text("Sort: ${prefs.sortOrder.label}")
            Text("Dark theme: ${if (prefs.darkTheme) "On" else "Off"}")
            Text("Hide low-rated (< ${UserPreferences.LOW_RATING_THRESHOLD}★): ${if (prefs.hideLowRated) "On" else "Off"}")
            if (isDefault) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Using default settings",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun ControlsCard(prefs: UserPreferences, actions: PreferenceActions) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Show", style = MaterialTheme.typography.titleSmall)
            ChipRow(
                options = ReadFilter.entries,
                selected = prefs.readFilter,
                label = { it.label },
                onSelect = actions.onReadFilterChange,
            )

            Text("Sort by", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
            ChipRow(
                options = SortOrder.entries,
                selected = prefs.sortOrder,
                label = { it.label },
                onSelect = actions.onSortOrderChange,
            )

            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            SwitchRow("Dark theme", prefs.darkTheme, actions.onDarkThemeChange)
            SwitchRow(
                "Hide books rated below ${UserPreferences.LOW_RATING_THRESHOLD}★",
                prefs.hideLowRated,
                actions.onHideLowRatedChange,
            )
            TextButton(onClick = actions.onResetToDefaults, modifier = Modifier.align(Alignment.End)) {
                Text("Reset to defaults")
            }
        }
    }
}

@Composable
private fun <T> ChipRow(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(option) },
                label = { Text(label(option)) },
                leadingIcon = if (isSelected) {
                    { Icon(Icons.Filled.Check, contentDescription = null) }
                } else null,
            )
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun BookRow(book: Book) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(book.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(book.author, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "★".repeat(book.rating) + "☆".repeat(5 - book.rating) + "   Added ${book.dateAdded}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(8.dp))
            AssistChip(onClick = {}, label = { Text(if (book.isRead) "Read" else "Unread") })
        }
    }
}