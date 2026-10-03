package com.chakra.comicreader.data.page

/**
 * Single source of truth for Android page decoding and cache budgets.
 * The app uses its JVM heap size with this divisor and cap.
 */
object PageConstants {
    /** Long-edge pixel limit for a decoded page; larger scans are downsampled to this. */
    val maxPageDimension: Int = 2560

    /** Spend 1/[cacheMemoryDivisor] of available memory on the page cache. */
    val cacheMemoryDivisor: Int = 4

    /** Hard upper bound on the page cache, in bytes (512 MB). */
    val cacheMaxBytes: Int = 512 * 1024 * 1024
}
