package com.chakra.comicreader.data.archive

import java.io.File

/** Thrown when a file is neither a readable ZIP nor RAR comic archive. */
class UnsupportedComicException(message: String) : Exception(message)

/**
 * Opens a local comic [File] as a [ComicArchive]. Format detection (magic bytes, extension
 * fallback) lives in the shared [ComicFormatDetector] to keep format routing consistent.
 */
object ComicArchiveFactory {

    fun detectFormat(file: File): ComicFormat {
        val head = ByteArray(8)
        val read = file.inputStream().use { it.read(head) }
        val leading = if (read > 0) head.copyOf(read) else ByteArray(0)
        return ComicFormatDetector.detect(leading, file.extension)
    }

    fun open(file: File): ComicArchive = when (detectFormat(file)) {
        ComicFormat.CBZ -> ZipComicArchive(file)
        ComicFormat.CBR -> RarComicArchive(file)
        ComicFormat.UNKNOWN ->
            throw UnsupportedComicException("Not a CBZ or CBR archive: ${file.name}")
    }
}
