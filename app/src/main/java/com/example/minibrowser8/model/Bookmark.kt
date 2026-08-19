package com.example.minibrowser8.model

import androidx.compose.runtime.mutableStateListOf
import java.util.UUID

data class Bookmark(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val isDefault: Boolean = false
)

object BookmarkManager {
    val bookmarks = mutableStateListOf<Bookmark>()

    init {
        // Pre-populate with essential popular bookmarks
        bookmarks.addAll(
            listOf(
                Bookmark(title = "Google", url = "https://www.google.com", isDefault = true),
                Bookmark(title = "YouTube", url = "https://m.youtube.com", isDefault = true),
                Bookmark(title = "Reddit", url = "https://www.reddit.com", isDefault = true),
                Bookmark(title = "Wikipedia", url = "https://en.m.wikipedia.org", isDefault = true),
                Bookmark(title = "GitHub", url = "https://github.com", isDefault = true),
                Bookmark(title = "DuckDuckGo", url = "https://duckduckgo.com", isDefault = true),
                Bookmark(title = "Hacker News", url = "https://news.ycombinator.com", isDefault = true),
                Bookmark(title = "Twitch", url = "https://m.twitch.tv", isDefault = true)
            )
        )
    }

    fun addBookmark(title: String, url: String) {
        val cleanUrl = BrowserSlotState.formatUrl(url)
        if (cleanUrl.isNotBlank()) {
            val cleanTitle = title.ifBlank { cleanUrl }
            bookmarks.add(0, Bookmark(title = cleanTitle, url = cleanUrl, isDefault = false))
        }
    }

    fun removeBookmark(id: String) {
        bookmarks.removeAll { it.id == id }
    }

    fun isBookmarked(url: String): Boolean {
        val cleanUrl = BrowserSlotState.formatUrl(url)
        return bookmarks.any { it.url.equals(cleanUrl, ignoreCase = true) }
    }
}
