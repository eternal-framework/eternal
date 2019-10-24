package dev.eternal.launcher.check

import dev.eternal.launcher.check.impl.CacheCheck
import dev.eternal.launcher.check.impl.DirectoryCheck
import dev.eternal.launcher.check.impl.RSACheck
import dev.eternal.launcher.check.impl.XteasCheck
import dev.eternal.util.Server.logger
import kotlin.system.exitProcess

/**
 * Stores a set of [Check] instances that are to be performed before startup.
 *
 * @author Cody Fullen
 */
class CheckStore {

    private val checks = mutableListOf<Check>()

    /**
     * Load the checks manually upon instance creation.
     */
    init {
        addCheck(DirectoryCheck())
        addCheck(CacheCheck())
        addCheck(XteasCheck())
        addCheck(RSACheck())
    }

    /**
     * Runs all checks that have been added to this store.
     */
    fun runAllChecks() {
        logger.info { "Preparing to run startup checks." }

        var passed = true
        checks.forEach { check ->
            if(!this.runCheck(check)) {
                passed = false
            }
        }

        if(!passed) {
            logger.error { "Some startup checks have failed. Please review the logs above to correct them." }
            exitProcess(0)
        } else {
            logger.info { "All ${checks.size} checks have passed. Continuing startup." }
        }
    }

    /**
     * Adds a check to the store.
     *
     * @param check The [Check] instance.
     */
    internal fun addCheck(check: Check) = checks.add(check)

    /**
     * Runs a [Check]'s logic and gets the return.
     * If the [Check] implements [CheckAction], invoke runAction().
     *
     * @param check The check to run.
     */
    internal fun runCheck(check: Check): Boolean {
        val result = check.check()
        return if(!result) {
            if(check is CheckAction) {
                this.runAction(check)
                true
            } else {
                logger.info { "Startup Check: ${check::class.java.simpleName} --- FAILED" }
                false
            }
        } else {
            logger.info { "Startup Check: ${check::class.java.simpleName} --- SUCCESS" }
            true
        }
    }

    /**
     * Runs a [Check]'s action if it implements [CheckAction]
     *
     * @param check The [Check] to run.
     */
    private fun runAction(check: CheckAction) {
        check.action()
        logger.info { "Startup Check: ${check::class.java.simpleName} --- RESOLVED" }
    }
}