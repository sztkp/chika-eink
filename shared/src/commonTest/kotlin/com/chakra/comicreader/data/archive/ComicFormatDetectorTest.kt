package com.chakra.comicreader.data.archive

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The core format-detection logic used by Android's ComicArchiveFactory.
 * Magic bytes win over the extension so a mislabeled file uses the correct backend.
 */
class ComicFormatDetectorTest {

    private val zipMagic = byteArrayOf(0x50, 0x4B, 0x03, 0x04)
    private val rarMagic = byteArrayOf(0x52, 0x61, 0x72, 0x21, 0x1A, 0x07)

    @Test
    fun zipMagicIsCbz() {
        assertEquals(ComicFormat.CBZ, ComicFormatDetector.detect(zipMagic, "cbz"))
    }

    @Test
    fun rarMagicIsCbr() {
        assertEquals(ComicFormat.CBR, ComicFormatDetector.detect(rarMagic, "cbr"))
    }

    @Test
    fun rarBytesUnderCbzExtensionStillCbr() {
        // The case that matters: a .cbz that is really a RAR must convert, not copy in broken.
        assertEquals(ComicFormat.CBR, ComicFormatDetector.detect(rarMagic, "cbz"))
    }

    @Test
    fun zipBytesUnderCbrExtensionStillCbz() {
        assertEquals(ComicFormat.CBZ, ComicFormatDetector.detect(zipMagic, "cbr"))
    }

    @Test
    fun noMagicFallsBackToExtension() {
        val junk = byteArrayOf(0x00, 0x11, 0x22, 0x33)
        assertEquals(ComicFormat.CBR, ComicFormatDetector.detect(junk, "cbr"))
        assertEquals(ComicFormat.CBR, ComicFormatDetector.detect(junk, "rar"))
        assertEquals(ComicFormat.CBZ, ComicFormatDetector.detect(junk, "zip"))
        assertEquals(ComicFormat.CBZ, ComicFormatDetector.detect(junk, "CBZ")) // case-insensitive
        assertEquals(ComicFormat.UNKNOWN, ComicFormatDetector.detect(junk, "pdf"))
    }

    @Test
    fun emptyHeadFallsBackToExtension() {
        assertEquals(ComicFormat.CBR, ComicFormatDetector.detect(ByteArray(0), "cbr"))
        assertEquals(ComicFormat.UNKNOWN, ComicFormatDetector.detect(ByteArray(0), ""))
    }
}
