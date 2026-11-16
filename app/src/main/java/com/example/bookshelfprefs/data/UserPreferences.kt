package com.example.bookshelfprefs.data

/** Which books to show based on read status. */
enum class ReadFilter(val label: String) {
    ALL("All"),
    READ("Read"),
    UNREAD("Unread"),
}

/** How the list is ordered. */
enum class SortOrder(val label: String) {
    TITLE("Title (A–Z)"),
    RATING("Rating (high → low)"),
    DATE_ADDED("Date added (newest)"),
}

/**
 * Everything the user can change. These default values are the
 * "fresh install" settings, used when nothing is saved yet
 * (or when the file can't be read).
 */
data class UserPreferences(
    val readFilter: ReadFilter = ReadFilter.ALL,
    val sortOrder: SortOrder = SortOrder.TITLE,
    val darkTheme: Boolean = false,      // original preference #1
    val hideLowRated: Boolean = false,   // original preference #2
) {
    companion object {
        const val LOW_RATING_THRESHOLD = 3
    }
}

/** Applies the saved preferences to the book list: filter, then sort. */
fun List<Book>.applyPreferences(prefs: UserPreferences): List<Book> {
    val filtered = filter { book ->
        when (prefs.readFilter) {
            ReadFilter.ALL -> true
            ReadFilter.READ -> book.isRead
            ReadFilter.UNREAD -> !book.isRead
        }
    }.filter { book ->
        !prefs.hideLowRated || book.rating >= UserPreferences.LOW_RATING_THRESHOLD
    }

    return when (prefs.sortOrder) {
        SortOrder.TITLE -> filtered.sortedBy { it.title.lowercase() }
        SortOrder.RATING -> filtered.sortedWith(
            compareByDescending<Book> { it.rating }.thenBy { it.title.lowercase() }
        )
        SortOrder.DATE_ADDED -> filtered.sortedByDescending { it.dateAdded }
    }
}