package com.chakra.comicreader.eink

internal enum class EinkRefresh { PARTIAL, FULL }

/** Reader-only, in-memory policy. Decisions are made on drawn content, never recomposition. */
internal class EinkRefreshPolicy(
    private val fullRefreshInterval: Int = DEFAULT_FULL_REFRESH_PAGE_INTERVAL,
    private val supportsFullRefresh: Boolean = true,
) {
    private var active = false
    private var interacting = false
    private var cleanInteraction = false
    private var fullRequested = false
    private var lastPage: Int? = null
    private var lastImage: Any? = null
    private var pageChanges = 0

    init { require(fullRefreshInterval >= 0) }

    fun enter() {
        leave()
        active = true
    }

    fun leave() {
        active = false
        interacting = false
        cleanInteraction = false
        fullRequested = false
        lastPage = null
        lastImage = null
        pageChanges = 0
    }

    fun interactionStarted() {
        if (active) interacting = true
    }

    fun interactionEnded() {
        if (active && interacting) {
            interacting = false
            cleanInteraction = true
        }
    }

    fun requestFullRefresh() {
        if (active && supportsFullRefresh) fullRequested = true
    }

    fun imageDrawn(page: Int, image: Any): EinkRefresh? {
        if (!active || interacting) return null
        val changed = page != lastPage || image != lastImage
        if (!changed && !cleanInteraction && !fullRequested) return null
        if (lastPage != null && page != lastPage) pageChanges++
        lastPage = page
        lastImage = image
        cleanInteraction = false
        val full = supportsFullRefresh && (fullRequested ||
            (fullRefreshInterval > 0 && pageChanges >= fullRefreshInterval))
        fullRequested = false
        if (full) pageChanges = 0
        return if (full) EinkRefresh.FULL else EinkRefresh.PARTIAL
    }

    companion object {
        const val DEFAULT_FULL_REFRESH_PAGE_INTERVAL = 5
    }
}

/** Match vendor identity, not a model name such as Palma that could be ambiguous. */
internal fun isBooxDevice(manufacturer: String, brand: String): Boolean =
    listOf(manufacturer, brand).any { value ->
        val words = value.lowercase().split(Regex("[^a-z0-9]+"))
        words.any { it == "onyx" || it == "boox" }
    }
