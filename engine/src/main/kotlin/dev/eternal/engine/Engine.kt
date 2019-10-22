package dev.eternal.engine

import dev.eternal.config.Conf
import dev.eternal.config.impl.ServerConfig
import dev.eternal.util.Injectable
import dev.eternal.util.Server.logger

/**
 * Represents the core of the game.
 *
 * @author Cody Fullen
 */
class Engine : Injectable {

    /**
     * The server name loaded from the config.
     */
    val serverName: String = Conf.SERVER[ServerConfig.server_name]

    /**
     * The current running engine revision.
     */
    val revision: Int = Conf.SERVER[ServerConfig.revision]

    /**
     * Initializes the engine.
     */
    fun init() {
        logger.info { "Initializing game engine." }

        this.postInit()
    }

    /**
     * Invoked after the [Engine] has finished initializing.
     */
    private fun postInit() {
        logger.info { "$serverName is running OSRS revision $revision." }
    }

    /**
     * Removes references from memory and attempts to save any
     * persistable data in memory.
     */
    fun terminate() {
        logger.info { "Terminating game engine." }
    }

}