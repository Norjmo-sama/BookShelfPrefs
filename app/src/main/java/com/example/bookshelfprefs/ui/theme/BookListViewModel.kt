package com.example.bookshelfprefs.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookshelfprefs.BookShelfApplication
import com.example.bookshelfprefs.data.Book
import com.example.bookshelfprefs.data.ReadFilter
import com.example.bookshelfprefs.data.SampleBooks
import com.example.bookshelfprefs.data.SortOrder
import com.example.bookshelfprefs.data.UserPreferences
import com.example.bookshelfprefs.data.UserPreferencesRepository
import com.example.bookshelfprefs.data.applyPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** What the screen draws. Loading lasts only until DataStore's first read. */
sealed interface BookListUiState {
    data object Loading : BookListUiState
    data class Ready(
        val preferences: UserPreferences,
        val books: List<Book>,
        val totalBooks: Int,
    ) : BookListUiState
}

class BookListViewModel(
    private val repository: UserPreferencesRepository,
    private val allBooks: List<Book> = SampleBooks.all,
) : ViewModel() {

    /**
     * Preferences Flow -> filtered/sorted list -> StateFlow that Compose collects.
     * Every DataStore write makes the Flow emit again, so the screen updates by itself.
     */
    val uiState: StateFlow<BookListUiState> = repository.userPreferencesFlow
        .map { prefs ->
            BookListUiState.Ready(
                preferences = prefs,
                books = allBooks.applyPreferences(prefs),
                totalBooks = allBooks.size,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BookListUiState.Loading,
        )

    // The UI calls these. They only WRITE; the new values come back through uiState.
    fun setReadFilter(filter: ReadFilter) {
        viewModelScope.launch { repository.updateReadFilter(filter) }
    }

    fun setSortOrder(order: SortOrder) {
        viewModelScope.launch { repository.updateSortOrder(order) }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch { repository.updateDarkTheme(enabled) }
    }

    fun setHideLowRated(enabled: Boolean) {
        viewModelScope.launch { repository.updateHideLowRated(enabled) }
    }

    fun resetToDefaults() {
        viewModelScope.launch { repository.resetToDefaults() }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as BookShelfApplication
                BookListViewModel(app.userPreferencesRepository)
            }
        }
    }
}