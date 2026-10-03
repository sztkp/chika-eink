package com.chakra.comicreader.ui.library

import com.chakra.comicreader.data.db.ComicEntity

enum class LibrarySort(val label: String) {
    TITLE_ASCENDING("Title A–Z"),
    TITLE_DESCENDING("Title Z–A"),
    LAST_ADDED("Last added"),
    LAST_READ("Last read");

    fun apply(comics: List<ComicEntity>): List<ComicEntity> {
        val titleOrder = compareBy<ComicEntity, String>(String.CASE_INSENSITIVE_ORDER) { it.title }
        val order = when (this) {
            TITLE_ASCENDING -> titleOrder
            TITLE_DESCENDING -> titleOrder.reversed()
            LAST_ADDED -> compareByDescending<ComicEntity> { it.dateAdded }
            LAST_READ -> compareByDescending<ComicEntity> { it.lastOpened }
        }
        return comics.sortedWith(order.thenBy { it.id })
    }
}
