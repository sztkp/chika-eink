package com.chakra.comicreader.ui.library

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chakra.comicreader.ComicReaderApp
import com.chakra.comicreader.data.db.ComicEntity
import com.chakra.comicreader.data.library.LibraryRepository
import com.chakra.comicreader.data.settings.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val repository: LibraryRepository,
    private val settings: AppSettings,
) : ViewModel() {

    private val _sort = MutableStateFlow(
        LibrarySort.entries.firstOrNull { it.name == settings.librarySort } ?: LibrarySort.LAST_READ,
    )
    val sort: StateFlow<LibrarySort> = _sort.asStateFlow()

    val comics: StateFlow<List<ComicEntity>> = combine(repository.comics, sort) { comics, order ->
        order.apply(comics)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setSort(order: LibrarySort) {
        settings.librarySort = order.name
        _sort.value = order
    }

    private val _importing = MutableStateFlow(false)
    val importing: StateFlow<Boolean> = _importing.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun importComic(uri: Uri) {
        viewModelScope.launch {
            _importing.value = true
            val result = repository.importComic(uri)
            _importing.value = false
            result.exceptionOrNull()?.let {
                _message.value = it.message ?: "Import failed."
            }
        }
    }

    fun deleteComic(id: Long) {
        viewModelScope.launch { repository.deleteComic(id) }
    }

    fun resetProgress(id: Long) {
        viewModelScope.launch {
            runCatching { repository.resetProgress(id) }
                .onSuccess { _message.value = if (it > 0) "Progress reset." else "Comic no longer available." }
                .onFailure { _message.value = "Could not reset progress." }
        }
    }

    fun consumeMessage() {
        _message.value = null
    }

    companion object {
        fun factory(app: ComicReaderApp): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    LibraryViewModel(app.libraryRepository, app.settings) as T
            }
    }
}
