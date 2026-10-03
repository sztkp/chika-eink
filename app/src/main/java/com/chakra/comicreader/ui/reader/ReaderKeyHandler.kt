package com.chakra.comicreader.ui.reader

import android.view.KeyEvent

/** Standard Android button events; no BOOX SDK or simulated swipe is required. */
internal class ReaderKeyHandler(
    private val onNext: () -> Unit,
    private val onPrevious: () -> Unit,
) {
    fun handle(keyCode: Int, action: Int, repeatCount: Int): Boolean {
        val navigate = when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_PAGE_DOWN -> onNext
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_PAGE_UP -> onPrevious
            else -> return false
        }
        // Consume release/repeat events too, so holding a key neither races through
        // panels nor changes volume after the initial navigation step.
        if (action == KeyEvent.ACTION_DOWN && repeatCount == 0) navigate()
        return true
    }
}
