package com.example.bookshelfprefs.data


/**
 * A single book on the shelf.
 * dateAdded is an ISO date (yyyy-MM-dd), which sorts correctly as plain text.
 */
data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val rating: Int,        // 1–5 stars
    val dateAdded: String,
    val isRead: Boolean,
)

/** The 8 sample books. */
object SampleBooks {
    val all: List<Book> = listOf(
        Book(1, "To Kill a Mockingbird", "Harper Lee", rating = 5, dateAdded = "2026-01-12", isRead = true),
        Book(2, "1984", "George Orwell", rating = 4, dateAdded = "2026-02-03", isRead = true),
        Book(3, "The Great Gatsby", "F. Scott Fitzgerald", rating = 2, dateAdded = "2025-11-20", isRead = true),
        Book(4, "Project Hail Mary", "Andy Weir", rating = 5, dateAdded = "2026-06-10", isRead = false),
        Book(5, "Dune", "Frank Herbert", rating = 4, dateAdded = "2026-04-18", isRead = false),
        Book(6, "Moby-Dick", "Herman Melville", rating = 2, dateAdded = "2025-09-05", isRead = false),
        Book(7, "The Hobbit", "J.R.R. Tolkien", rating = 5, dateAdded = "2025-12-24", isRead = true),
        Book(8, "Pride and Prejudice", "Jane Austen", rating = 3, dateAdded = "2026-05-30", isRead = false),
    )
}