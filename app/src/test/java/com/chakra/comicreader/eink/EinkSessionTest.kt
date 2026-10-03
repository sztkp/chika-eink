package com.chakra.comicreader.eink

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class EinkSessionTest {
    private class Backend : EinkBackend {
        val calls = mutableListOf<String>()
        var enterFailure: Throwable? = null
        var refreshFailure: Throwable? = null
        var restoreFailure: Throwable? = null
        override fun enter() { calls += "enter"; enterFailure?.let { throw it } }
        override fun restore() { calls += "restore"; restoreFailure?.let { throw it } }
        override fun refresh(refresh: EinkRefresh) { calls += refresh.name; refreshFailure?.let { throw it } }
    }

    @Test
    fun enterIsIdempotentAndLeaveRestoresOnlyOnce() {
        val backend = Backend()
        val session = EinkSession(backend, EinkRefreshPolicy()) { error("unexpected failure") }
        session.enter()
        session.enter()
        session.refresh(session.imageDrawn(0, "whole")!!)
        session.leave()
        session.leave()
        session.refresh(EinkRefresh.FULL)
        assertNull(session.imageDrawn(1, "whole"))
        assertEquals(listOf("enter", "PARTIAL", "restore"), backend.calls)
    }

    @Test
    fun hiddenApiFailureRollsBackAndDisablesFurtherVendorOperations() {
        val backend = Backend().apply { enterFailure = SecurityException("blocked") }
        val failures = mutableListOf<Throwable>()
        val session = EinkSession(backend, EinkRefreshPolicy(), failures::add)
        session.enter()
        session.enter()
        session.requestFullRefresh()
        session.refresh(EinkRefresh.FULL)
        assertFalse(session.available)
        assertNull(session.imageDrawn(0, "whole"))
        assertEquals(listOf("enter", "restore"), backend.calls)
        assertEquals(1, failures.size)
        session.leave()
        assertEquals("restore", backend.calls.last())
    }

    @Test
    fun runtimeLinkageFailureRestoresOriginalModeAndStopsRefreshes() {
        val backend = Backend().apply { refreshFailure = NoClassDefFoundError("firmware changed") }
        val failures = mutableListOf<Throwable>()
        val session = EinkSession(backend, EinkRefreshPolicy(), failures::add)
        session.enter()
        session.refresh(EinkRefresh.PARTIAL)
        session.refresh(EinkRefresh.FULL)
        assertFalse(session.available)
        assertNull(session.imageDrawn(1, "whole"))
        assertEquals(listOf("enter", "PARTIAL", "restore"), backend.calls)
        assertEquals(1, failures.size)
    }

    @Test
    fun failedRestorationStillDisablesVendorOperationsWithoutThrowing() {
        val backend = Backend().apply { restoreFailure = SecurityException("blocked") }
        val failures = mutableListOf<Throwable>()
        val session = EinkSession(backend, EinkRefreshPolicy(), failures::add)
        session.enter()
        session.leave()
        session.enter()
        session.refresh(EinkRefresh.PARTIAL)
        assertFalse(session.available)
        assertNull(session.imageDrawn(0, "whole"))
        assertEquals(listOf("enter", "restore", "restore"), backend.calls)
        assertEquals(1, failures.size)
    }

    @Test
    fun pauseAndResumeUseFreshPolicyWithoutPersistingCounters() {
        val backend = Backend()
        val session = EinkSession(backend, EinkRefreshPolicy(fullRefreshInterval = 1)) { error("unexpected failure") }
        session.enter()
        session.imageDrawn(0, "whole")
        session.interactionStarted()
        session.requestFullRefresh()
        session.leave()
        session.enter()
        assertEquals(EinkRefresh.PARTIAL, session.imageDrawn(1, "whole"))
        assertEquals(listOf("enter", "restore", "enter"), backend.calls)
    }
}
