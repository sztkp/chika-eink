package com.chakra.comicreader

import android.app.Application
import com.chakra.comicreader.data.db.AppDatabase
import com.chakra.comicreader.data.library.LibraryRepository
import com.chakra.comicreader.data.settings.AppSettings
import com.chakra.comicreader.detection.MlPanelDetector
import com.chakra.comicreader.detection.NoopPanelSource
import com.chakra.comicreader.detection.PanelSource

/**
 * Application entry point. Holds process-wide singletons (the Room database, the
 * [LibraryRepository], and the panel detector).
 */
class ComicReaderApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.get(this) }
    val settings: AppSettings by lazy { AppSettings(this) }
    val libraryRepository: LibraryRepository by lazy {
        LibraryRepository(this, database.comicDao(), settings)
    }

    /** On-device ML panel detector; falls back to whole-page-only if the model can't load. */
    val panelSource: PanelSource by lazy {
        MlPanelDetector.tryCreate(this) ?: NoopPanelSource
    }

}
