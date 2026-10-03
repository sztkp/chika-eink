package com.chakra.comicreader.data.settings

import android.content.Context
import androidx.core.content.edit

/**
 * Lightweight app-wide preferences backed by [android.content.SharedPreferences].
 *
 * [defaultRightToLeft] is the reading direction applied to newly imported comics; each comic then
 * remembers its own direction once opened (stored per-comic in the database). This is the global
 * half of "remembered per comic + a global default".
 */
class AppSettings(context: Context) {
    // Retain the legacy storage name so existing installations keep their preferences.
    private val prefs = context.getSharedPreferences("chika.settings", Context.MODE_PRIVATE)

    var defaultRightToLeft: Boolean
        get() = prefs.getBoolean(KEY_DEFAULT_RTL, false)
        set(value) { prefs.edit { putBoolean(KEY_DEFAULT_RTL, value) } }

    /** Stored by name so adding sort options never changes an existing selection. */
    var librarySort: String
        get() = prefs.getString(KEY_LIBRARY_SORT, "LAST_READ") ?: "LAST_READ"
        set(value) { prefs.edit { putString(KEY_LIBRARY_SORT, value) } }

    private companion object {
        const val KEY_LIBRARY_SORT = "library_sort"
        const val KEY_DEFAULT_RTL = "default_rtl"
    }
}
