package com.chakra.comicreader.eink

/** Implementations must preserve the original View mode even if entering fails part-way. */
internal interface EinkBackend {
    fun enter()
    fun restore()
    fun refresh(refresh: EinkRefresh)
}

/** Failure circuit breaker, independently testable without Android or vendor classes. */
internal class EinkSession(
    private val backend: EinkBackend,
    private val policy: EinkRefreshPolicy,
    private val onFailure: (Throwable) -> Unit,
) {
    var available = true
        private set
    private var active = false

    fun enter() {
        if (!available || active) return
        active = true
        if (guard { backend.enter() }) policy.enter()
    }

    fun leave() {
        policy.leave()
        if (active) {
            active = false
            guard { backend.restore() }
        }
    }

    fun imageDrawn(page: Int, image: Any): EinkRefresh? =
        if (available && active) policy.imageDrawn(page, image) else null

    fun refresh(refresh: EinkRefresh) {
        if (available && active) guard { backend.refresh(refresh) }
    }

    fun interactionStarted() = policy.interactionStarted()
    fun interactionEnded() = policy.interactionEnded()
    fun requestFullRefresh() = policy.requestFullRefresh()

    private fun guard(operation: () -> Unit): Boolean = try {
        operation()
        true
    } catch (failure: Exception) {
        fail(failure)
        false
    } catch (failure: LinkageError) {
        fail(failure)
        false
    }

    private fun fail(failure: Throwable) {
        if (available) onFailure(failure)
        available = false
        policy.leave()
        // The setter may have changed firmware state before throwing. Always attempt rollback.
        try {
            backend.restore()
        } catch (_: Exception) {
            // No bypass of firmware/hidden-API restrictions; a later leave can retry restoration.
        } catch (_: LinkageError) {
            // Keep normal Android rendering alive even on incompatible firmware.
        }
    }
}
