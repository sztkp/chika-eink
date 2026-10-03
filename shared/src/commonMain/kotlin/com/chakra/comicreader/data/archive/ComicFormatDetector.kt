package com.chakra.comicreader.data.archive

/** A comic archive container format. */
enum class ComicFormat { CBZ, CBR, UNKNOWN }

/**
 * Detects a comic's container format from its leading bytes, falling back to the file extension.
 * Used by the Android archive factory for consistent file routing — a mislabeled ".cbz" that is really
 * a RAR (common in the wild) is opened as CBR.
 */
object ComicFormatDetector {
    private val ZIP_MAGIC = byteArrayOf(0x50, 0x4B, 0x03, 0x04)             // "PK\x03\x04"
    private val ZIP_EMPTY = byteArrayOf(0x50, 0x4B, 0x05, 0x06)             // empty archive
    private val RAR_MAGIC = byteArrayOf(0x52, 0x61, 0x72, 0x21, 0x1A, 0x07) // "Rar!\x1a\x07"

    /**
     * @param head the first bytes of the file (8 is plenty); fewer is fine, magic just won't match.
     * @param fileExtension the file's extension (without the dot), used only when magic is absent.
     */
    fun detect(head: ByteArray, fileExtension: String): ComicFormat {
        if (head.size >= 4 && (head.startsWith(ZIP_MAGIC) || head.startsWith(ZIP_EMPTY))) return ComicFormat.CBZ
        if (head.size >= 6 && head.startsWith(RAR_MAGIC)) return ComicFormat.CBR
        return when (fileExtension.lowercase()) {
            "cbz", "zip" -> ComicFormat.CBZ
            "cbr", "rar" -> ComicFormat.CBR
            else -> ComicFormat.UNKNOWN
        }
    }

    private fun ByteArray.startsWith(prefix: ByteArray): Boolean {
        if (size < prefix.size) return false
        for (i in prefix.indices) if (this[i] != prefix[i]) return false
        return true
    }
}
