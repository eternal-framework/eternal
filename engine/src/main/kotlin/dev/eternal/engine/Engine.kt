package dev.eternal.engine

import dev.eternal.util.Injectable
import dev.eternal.util.Server.logger

/**
 * Represents the core of the game.
 *
 * @author Cody Fullen
 */
class Engine : Injectable {

    /**
     * Initializes the engine.
     */
    fun init() {
        logger.info { "Initializing game engine." }
    }

    /**
     * Removes references from memory and attempts to save any
     * persistable data in memory.
     */
    fun terminate() {
        logger.info { "Terminating game engine." }
    }

}