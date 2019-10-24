package dev.eternal.engine

import com.google.common.base.Stopwatch
import dev.eternal.config.Conf
import dev.eternal.config.impl.ServerConfig
import dev.eternal.util.Injectable
import dev.eternal.util.PathConstants
import dev.eternal.util.Server.logger
import net.runelite.cache.fs.Store
import java.io.File
import java.util.concurrent.TimeUnit

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
     * The cache [Store] object.
     */
    val cachestore: Store = Store(File(PathConstants.CACHE_FOLDER_PATH))

    /**
     * Initializes the engine.
     */
    fun init() {
        logger.info { "Initializing game engine." }

        this.loadCacheStore()

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

    /**
     * Loads the cache store into memory.
     */
    private fun loadCacheStore() {
        val stopwatch = Stopwatch.createStarted()
        logger.info { "Loading cache store..." }
        cachestore.load()
        stopwatch.stop()
        logger.info { "Finished loading cache store in ${stopwatch.elapsed(TimeUnit.MILLISECONDS)}ms." }
    }
}