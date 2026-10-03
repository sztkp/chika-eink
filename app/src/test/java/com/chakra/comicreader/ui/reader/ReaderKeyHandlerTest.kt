package com.chakra.comicreader.ui.reader

import android.view.KeyEvent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReaderKeyHandlerTest {
    @Test
    fun volumeAndPageKeysUsePanelActions() {
        var next = 0
        var previous = 0
        val handler = ReaderKeyHandler({ next++ }, { previous++ })
        for (key in listOf(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_PAGE_DOWN)) {
            assertTrue(handler.handle(key, KeyEvent.ACTION_DOWN, 0))
        }
        for (key in listOf(KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_PAGE_UP)) {
            assertTrue(handler.handle(key, KeyEvent.ACTION_DOWN, 0))
        }
        assertEquals(2, next)
        assertEquals(2, previous)
    }

    @Test
    fun heldKeyAndReleaseDoNotNavigateAgain() {
        var steps = 0
        val handler = ReaderKeyHandler({ steps++ }, { steps++ })
        for (key in listOf(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_PAGE_DOWN, KeyEvent.KEYCODE_PAGE_UP)) {
            assertTrue(handler.handle(key, KeyEvent.ACTION_DOWN, 0))
            assertTrue(handler.handle(key, KeyEvent.ACTION_DOWN, 1))
            assertTrue(handler.handle(key, KeyEvent.ACTION_DOWN, 8))
            assertTrue(handler.handle(key, KeyEvent.ACTION_UP, 0))
        }
        assertEquals(4, steps)
    }

    @Test
    fun systemAndOtherKeysRemainUnhandled() {
        var steps = 0
        val handler = ReaderKeyHandler({ steps++ }, { steps++ })
        for (key in listOf(KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_POWER,
            KeyEvent.KEYCODE_VOLUME_MUTE, KeyEvent.KEYCODE_ENTER)) {
            assertFalse(handler.handle(key, KeyEvent.ACTION_DOWN, 0))
            assertFalse(handler.handle(key, KeyEvent.ACTION_UP, 0))
        }
        assertEquals(0, steps)
    }
}
