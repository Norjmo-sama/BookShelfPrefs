package com.example.bookshelfprefs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookshelfprefs.ui.theme.BookListScreen
import com.example.bookshelfprefs.ui.theme.BookListUiState
import com.example.bookshelfprefs.ui.theme.BookListViewModel
import com.example.bookshelfprefs.ui.theme.PreferenceActions
import com.example.bookshelfprefs.ui.theme.BookShelfTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BookListViewModel by viewModels { BookListViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Recomposes every time the ViewModel's StateFlow emits,
            // which happens on launch and after every DataStore change.
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val darkTheme = (uiState as? BookListUiState.Ready)?.preferences?.darkTheme ?: false

            BookShelfTheme(darkTheme = darkTheme) {
                BookListScreen(
                    uiState = uiState,
                    actions = PreferenceActions(
                        onReadFilterChange = viewModel::setReadFilter,
                        onSortOrderChange = viewModel::setSortOrder,
                        onDarkThemeChange = viewModel::setDarkTheme,
                        onHideLowRatedChange = viewModel::setHideLowRated,
                        onResetToDefaults = viewModel::resetToDefaults,
                    ),
                )
            }
        }
    }
}