package dev.eternal.launcher.check

import dev.eternal.launcher.check.lib.ActionTestCheck
import dev.eternal.launcher.check.lib.FailTestCheck
import dev.eternal.launcher.check.lib.PassTestCheck
import dev.eternal.util.test.TestUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class CheckStoreTest {

    private val checkstore = CheckStore()

    private val passCheck = mockk<PassTestCheck>()
    private val actionCheck = mockk<ActionTestCheck>()
    private val failCheck = mockk<FailTestCheck>()

    @Before
    fun before() {
        /**
         * Clear the stored checks
         */
        val checks = TestUtils.getField<MutableList<Check>>(checkstore, "checks")
        checks.clear()

    }

    @Test
    fun runAllChecks() {
        checkstore.runAllChecks()
    }

    @Test
    fun `addCheck$launcher`() {
        every { passCheck.check() } returns true

        checkstore.addCheck(passCheck)
        checkstore.runAllChecks()
    }

    @Test
    fun `runCheck$launcher`() {
        every { passCheck.check() } returns true
        assertTrue(checkstore.runCheck(passCheck))
    }

    @Test
    fun `Actionable check test`() {
        var invoked = false

        every { actionCheck.check() } returns false
        every { actionCheck.action() } answers { invoked = true }

        checkstore.addCheck(actionCheck)
        checkstore.runAllChecks()

        verify { actionCheck.action() }

        assertTrue(invoked)
    }

    @Test
    fun `Failing check test`() {
        every { failCheck.check() } returns false
        assertFalse(checkstore.runCheck(failCheck))
    }
}