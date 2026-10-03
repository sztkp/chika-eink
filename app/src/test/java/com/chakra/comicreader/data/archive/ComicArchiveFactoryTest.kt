package com.chakra.comicreader.data.archive

import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ComicArchiveFactoryTest {
    @Test
    fun zipPagesRemainReadableAndNaturallySorted() {
        withArchive("cbz") { file ->
            ZipOutputStream(file.outputStream()).use { zip ->
                for (name in listOf("page10.jpg", "page2.jpg", "notes.txt")) {
                    zip.putNextEntry(ZipEntry(name))
                    zip.write(name.toByteArray())
                    zip.closeEntry()
                }
            }
            ComicArchiveFactory.open(file).use { archive ->
                assertEquals(2, archive.pageCount)
                assertEquals("page2.jpg", archive.pageName(0))
                assertContentEquals("page2.jpg".toByteArray(), archive.readPage(0))
                assertEquals("page10.jpg", archive.pageName(1))
            }
        }
    }

    @Test
    fun rar4AndRar5AreRejectedEvenWhenRenamedCbz() {
        for (suffix in listOf(byteArrayOf(0x00), byteArrayOf(0x01, 0x00))) {
            for (extension in listOf("cbr", "rar", "cbz")) {
                withArchive(extension) { file ->
                    val bytes = byteArrayOf(0x52, 0x61, 0x72, 0x21, 0x1A, 0x07) + suffix
                    file.writeBytes(bytes)
                    val error = assertFailsWith<UnsupportedComicException> {
                        ComicArchiveFactory.open(file)
                    }
                    assertTrue(error.message.orEmpty().contains("ZIP them into a CBZ"))
                    assertContentEquals(bytes, file.readBytes())
                }
            }
        }
    }

    @Test
    fun zipContainerWithCbrExtensionRemainsReadable() {
        withArchive("cbr") { file ->
            ZipOutputStream(file.outputStream()).use { zip ->
                zip.putNextEntry(ZipEntry("page.jpg"))
                zip.write(byteArrayOf(1, 2, 3))
                zip.closeEntry()
            }
            ComicArchiveFactory.open(file).use { archive ->
                assertEquals(1, archive.pageCount)
                assertContentEquals(byteArrayOf(1, 2, 3), archive.readPage(0))
            }
        }
    }

    @Test
    fun unknownContainerIsRejected() {
        withArchive("pdf") { file ->
            file.writeText("%PDF-1.7")
            assertFailsWith<UnsupportedComicException> { ComicArchiveFactory.open(file) }
        }
    }

    private fun withArchive(extension: String, block: (java.io.File) -> Unit) {
        val file = Files.createTempFile("kuro-archive-test-", ".$extension").toFile()
        try {
            block(file)
        } finally {
            file.delete()
        }
    }
}
