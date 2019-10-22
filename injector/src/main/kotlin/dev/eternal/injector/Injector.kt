package dev.eternal.injector

import org.koin.core.context.startKoin

/**
 * The global dependency injection loader.
 *
 * @author Cody Fullen
 */

class Injector {

    /**
     * Starts the koin framework injector.
     */
    fun start() {
        startKoin { modules(global_apps) }
    }

    companion object {
        /**
         * The global module set.
         */
        val global_apps = listOf(
            app_module,
            engine_module
        )
    }
}