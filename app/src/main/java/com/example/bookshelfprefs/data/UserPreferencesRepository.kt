package com.example.bookshelfprefs.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * The ONLY class that talks to Preferences DataStore.
 *  - Reads:  userPreferencesFlow emits new settings every time they change.
 *  - Writes: suspend functions that call dataStore.edit { }, which is an
 *            atomic, transactional read-modify-write.
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
) {

    // The keys the values are stored under in the DataStore file.
    private object Keys {
        val READ_FILTER = stringPreferencesKey("read_filter")
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val HIDE_LOW_RATED = booleanPreferencesKey("hide_low_rated")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            // IOException = the file couldn't be read. Use the defaults instead.
            // Any other exception is a real bug, so rethrow it.
            if (exception is IOException) {
                Log.e("UserPrefsRepository", "Error reading preferences; using defaults.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs -> mapUserPreferences(prefs) }
        .distinctUntilChanged()

    suspend fun updateReadFilter(filter: ReadFilter) {
        dataStore.edit { prefs -> prefs[Keys.READ_FILTER] = filter.name }
    }

    suspend fun updateSortOrder(order: SortOrder) {
        dataStore.edit { prefs -> prefs[Keys.SORT_ORDER] = order.name }
    }

    suspend fun updateDarkTheme(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[Keys.DARK_THEME] = enabled }
    }

    suspend fun updateHideLowRated(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[Keys.HIDE_LOW_RATED] = enabled }
    }

    /** Removes every saved key, so the app is back to the fresh-install defaults. */
    suspend fun resetToDefaults() {
        dataStore.edit { prefs -> prefs.clear() }
    }

    // Turns the raw stored values into a UserPreferences object.
    // Any missing (or unknown) value falls back to its default.
    private fun mapUserPreferences(prefs: Preferences): UserPreferences {
        val defaults = UserPreferences()
        return UserPreferences(
            readFilter = ReadFilter.entries.firstOrNull { it.name == prefs[Keys.READ_FILTER] }
                ?: defaults.readFilter,
            sortOrder = SortOrder.entries.firstOrNull { it.name == prefs[Keys.SORT_ORDER] }
                ?: defaults.sortOrder,
            darkTheme = prefs[Keys.DARK_THEME] ?: defaults.darkTheme,
            hideLowRated = prefs[Keys.HIDE_LOW_RATED] ?: defaults.hideLowRated,
        )
    }
}