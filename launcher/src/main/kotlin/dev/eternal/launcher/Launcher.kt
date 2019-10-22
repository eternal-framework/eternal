package dev.eternal.launcher

import dev.eternal.config.Conf
import dev.eternal.engine.Engine
import dev.eternal.injector.Injector
import dev.eternal.launcher.check.CheckStore
import dev.eternal.util.Injectable
import dev.eternal.util.Server.logger
import org.koin.core.inject

/**
 * Launches the server and required dependency injector.
 *
 * @author Cody Fullen
 */

object Launcher : Injectable {

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

        this.loadConfigs()

        this.startEngine()
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

    /**
     * Loads the base configs required for server startup.
     */
    private fun loadConfigs() {
        /**
         * Load the server config
         */
        Conf.SERVER.loadFile()

        logger.info { "Finished loading all config files." }
    }

    /**
     * Initializes the game engine.
     */
    private fun startEngine() {
        /**
         * Dependency injected [Engine] singleton.
         */
        val engine: Engine = inject<Engine>().value

        engine.init()
    }

}