package com.chakra.comicreader

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.chakra.comicreader.ui.nav.AppNavHost
import com.chakra.comicreader.ui.theme.ComicReaderTheme
import com.chakra.comicreader.ui.reader.ReaderKeyHandler
import com.chakra.comicreader.eink.createEinkController

class MainActivity : ComponentActivity() {
    private val einkDelegate = lazy { createEinkController() }
    internal val einkController get() = einkDelegate.value

    override fun onResume() {
        super.onResume()
        if (einkDelegate.isInitialized()) einkController.resume()
    }

    override fun onPause() {
        if (einkDelegate.isInitialized()) einkController.pause()
        super.onPause()
    }

    override fun onDestroy() {
        if (einkDelegate.isInitialized()) einkController.dispose()
        super.onDestroy()
    }

    internal var readerKeyHandler: ReaderKeyHandler? = null

    // Public Activity/Window.Callback override inherited through androidx.activity.ComponentActivity.
    // Lint incorrectly propagates the restriction on its internal androidx.core base class.
    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (readerKeyHandler?.handle(event.keyCode, event.action, event.repeatCount) == true) return true
        return super.dispatchKeyEvent(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(android.graphics.Color.WHITE, android.graphics.Color.WHITE),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.WHITE, android.graphics.Color.WHITE),
        )
        super.onCreate(savedInstanceState)
        setContent {
            ComicReaderTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavHost()
                }
            }
        }
    }
}
