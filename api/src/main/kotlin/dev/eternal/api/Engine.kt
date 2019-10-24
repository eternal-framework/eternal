package dev.eternal.api

import net.runelite.cache.fs.Store

/**
 * The core of the server's game engine API.
 *
 * @author Cody Fullen
 */
interface Engine {

    /**
     * The server's name loaded from the ServerConfig.
     */
    val serverName: String

    /**
     * The server's revision loaded from the ServerConfig.
     */
    val revision: Int

    /**
     * The loaded cachestore for game cache interaction.
     */
    val cachestore: Store

    /**
     * Terminates the engine and therefor all other child interactions.
     */
    fun terminate()
}