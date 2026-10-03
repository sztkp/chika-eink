package com.chakra.comicreader.eink

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class EinkRefreshPolicyTest {
    @Test
    fun drawnImageIsRefreshedOnceNotOnRepeatedDraws() {
        val policy = EinkRefreshPolicy().apply { enter() }
        assertEquals(EinkRefresh.PARTIAL, policy.imageDrawn(0, "panel0"))
        repeat(10) { assertNull(policy.imageDrawn(0, "panel0")) }
        assertEquals(EinkRefresh.PARTIAL, policy.imageDrawn(0, "panel1"))
        assertNull(policy.imageDrawn(0, "panel1"))
    }

    @Test
    fun onlyPageChangesCountTowardGcAndFirstPageDoesNotCount() {
        val policy = EinkRefreshPolicy(fullRefreshInterval = 3).apply { enter() }
        for (page in 0..2) {
            assertEquals(EinkRefresh.PARTIAL, policy.imageDrawn(page, "panel0"))
            for (panel in 1..20) assertEquals(EinkRefresh.PARTIAL, policy.imageDrawn(page, "panel$panel"))
        }
        assertEquals(EinkRefresh.FULL, policy.imageDrawn(3, "panel0"))
        assertNull(policy.imageDrawn(3, "panel0"))
        assertEquals(EinkRefresh.PARTIAL, policy.imageDrawn(4, "panel0"))
        assertEquals(EinkRefresh.PARTIAL, policy.imageDrawn(5, "panel0"))
        assertEquals(EinkRefresh.FULL, policy.imageDrawn(6, "panel0"))
    }

    @Test
    fun defaultPolicyClearsAfterFivePageChanges() {
        val policy = EinkRefreshPolicy().apply { enter() }
        assertEquals(5, EinkRefreshPolicy.DEFAULT_FULL_REFRESH_PAGE_INTERVAL)
        repeat(5) { assertEquals(EinkRefresh.PARTIAL, policy.imageDrawn(it, "whole")) }
        assertEquals(EinkRefresh.FULL, policy.imageDrawn(5, "whole"))
    }

    @Test
    fun backwardStepsAndLargeJumpsCountOncePerDisplayedPage() {
        val policy = EinkRefreshPolicy(fullRefreshInterval = 3).apply { enter() }
        assertEquals(EinkRefresh.PARTIAL, policy.imageDrawn(10, "whole"))
        assertEquals(EinkRefresh.PARTIAL, policy.imageDrawn(9, "whole"))
        assertEquals(EinkRefresh.PARTIAL, policy.imageDrawn(50, "whole"))
        assertEquals(EinkRefresh.FULL, policy.imageDrawn(1, "whole"))
    }

    @Test
    fun zeroIntervalDisablesPeriodicRefreshButAllowsManualGc() {
        val policy = EinkRefreshPolicy(fullRefreshInterval = 0).apply { enter() }
        repeat(100) { assertEquals(EinkRefresh.PARTIAL, policy.imageDrawn(it, "whole")) }
        policy.requestFullRefresh()
        policy.requestFullRefresh()
        assertEquals(EinkRefresh.FULL, policy.imageDrawn(99, "whole"))
        assertNull(policy.imageDrawn(99, "whole"))
    }

    @Test
    fun manualGcResetsPeriodicCounterAndReplacesPartial() {
        val policy = EinkRefreshPolicy(fullRefreshInterval = 2).apply { enter() }
        policy.imageDrawn(0, "whole")
        policy.imageDrawn(1, "whole")
        policy.requestFullRefresh()
        assertEquals(EinkRefresh.FULL, policy.imageDrawn(2, "whole"))
        assertEquals(EinkRefresh.PARTIAL, policy.imageDrawn(3, "whole"))
        assertEquals(EinkRefresh.FULL, policy.imageDrawn(4, "whole"))
    }

    @Test
    fun interactionDefersRefreshAndCleansExactlyOnceOnEnd() {
        val policy = EinkRefreshPolicy().apply { enter() }
        policy.imageDrawn(0, "panel")
        policy.interactionStarted()
        repeat(10) { assertNull(policy.imageDrawn(0, "panel")) }
        policy.interactionEnded()
        policy.interactionEnded()
        assertEquals(EinkRefresh.PARTIAL, policy.imageDrawn(0, "panel"))
        assertNull(policy.imageDrawn(0, "panel"))
    }

    @Test
    fun navigationAndManualRefreshDuringInteractionCoalesceIntoOneGc() {
        val policy = EinkRefreshPolicy(fullRefreshInterval = 1).apply { enter() }
        policy.imageDrawn(0, "whole")
        policy.interactionStarted()
        assertNull(policy.imageDrawn(1, "whole"))
        policy.requestFullRefresh()
        policy.interactionEnded()
        assertEquals(EinkRefresh.FULL, policy.imageDrawn(1, "whole"))
        assertNull(policy.imageDrawn(1, "whole"))
    }

    @Test
    fun endingAnInteractionThatNeverStartedDoesNotRefresh() {
        val policy = EinkRefreshPolicy().apply { enter() }
        policy.imageDrawn(0, "whole")
        policy.interactionEnded()
        assertNull(policy.imageDrawn(0, "whole"))
    }

    @Test
    fun leavingResetsCountersAndPendingInteractionAndManualRequests() {
        val policy = EinkRefreshPolicy(fullRefreshInterval = 1).apply { enter() }
        policy.imageDrawn(0, "whole")
        policy.interactionStarted()
        policy.requestFullRefresh()
        policy.leave()
        assertNull(policy.imageDrawn(1, "whole"))
        policy.interactionEnded()
        policy.enter()
        assertEquals(EinkRefresh.PARTIAL, policy.imageDrawn(1, "whole"))
    }

    @Test
    fun missingGcCapabilityNeverProducesFullRefresh() {
        val policy = EinkRefreshPolicy(fullRefreshInterval = 1, supportsFullRefresh = false).apply { enter() }
        policy.requestFullRefresh()
        repeat(20) { assertEquals(EinkRefresh.PARTIAL, policy.imageDrawn(it, "whole")) }
    }

    @Test
    fun negativeIntervalIsRejected() {
        assertFailsWith<IllegalArgumentException> { EinkRefreshPolicy(fullRefreshInterval = -1) }
    }

    @Test
    fun vendorIdentityRequiresACompleteOnyxOrBooxToken() {
        assertTrue(isBooxDevice("ONYX", "BOOX"))
        assertTrue(isBooxDevice("Onyx International Inc.", "unknown"))
        assertTrue(isBooxDevice("unknown", "boox"))
        assertFalse(isBooxDevice("Samsung", "Samsung"))
        assertFalse(isBooxDevice("OnyxLike", "Booxish"))
        assertFalse(isBooxDevice("", ""))
        assertFalse(isBooxDevice("Palma2", "generic"))
    }
    @Test
    fun firmwareIdentifiersRequireTheExactVerifiedPalma2Build() {
        val fingerprint = "ONYX/TabBoox/TabBoox:13/TKQ1.230615.001/GV2.027.SQ83A:user/release-keys"
        val display = "2026-05-13_18-43_4.2-rel_05132_72a2c1b9e"
        assertEquals(OnyxModeIdentifiers(6, 2, 98), verifiedPalma2Modes("Palma2", 33, fingerprint, display))
        assertNull(verifiedPalma2Modes("Palma", 33, fingerprint, display))
        assertNull(verifiedPalma2Modes("Palma2", 34, fingerprint, display))
        assertNull(verifiedPalma2Modes("Palma2", 33, fingerprint + "changed", display))
        assertNull(verifiedPalma2Modes("Palma2", 33, fingerprint, display + "changed"))
    }

}
