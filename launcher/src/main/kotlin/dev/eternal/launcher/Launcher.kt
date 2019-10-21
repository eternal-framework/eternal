package dev.eternal.launcher

import dev.eternal.injector.Injector
import dev.eternal.launcher.check.CheckStore
import dev.eternal.util.Server.logger

/**
 * Launches the server and required dependency injector.
 *
 * @author Cody Fullen
 */

object Launcher {

    /**
     * Static entry method.
     *
     * @param args The CLI arguments.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        logger.info { "Initializing..." }

        this.startInjector()

        this.runStartupChecks()
    }

    /**
     * Starts the dependency injector.
     */
    private fun startInjector() {
        logger.info { "Loading dependency injector." }

        val injector = Injector()
        injector.start()

        logger.info { "Dependency injector has finished loading." }
    }

    /**
     * Runs the server startup checks.
     * This ensures the pre-start requirements have been met to prevent startup errors.
     *
     * If any of the checks fail and do not have a actionable resolution, the process will exit.
     */
    private fun runStartupChecks() {
        val checkStore = CheckStore()
        checkStore.runAllChecks()
    }

}