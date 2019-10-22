package dev.eternal.launcher

import dev.eternal.config.Conf
import dev.eternal.config.impl.ServerConfig
import dev.eternal.engine.Engine
import dev.eternal.injector.Injector
import dev.eternal.launcher.check.CheckStore
import dev.eternal.net.NetworkServer
import dev.eternal.util.Injectable
import dev.eternal.util.Server.logger
import io.netty.channel.ChannelFuture
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import java.net.InetSocketAddress

/**
 * Launches the server and required dependency injector.
 *
 * @author Cody Fullen
 */

object Launcher : Injectable {

    /**
     * Loaded values from the [Engine] instance.
     */
    private var serverName: String = ""
    private var revision: Int = -1
    private lateinit var address: InetSocketAddress

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

        this.startNetwork().addListener {
            if(it.isDone) {
                logger.info { "$serverName server startup completed. Running OSRS revision $revision." }
                logger.info { "Listening on ${address.hostString}:${address.port} for connections..." }
            }
        }
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
        val engine: Engine by inject()
        engine.init()

        this.serverName = engine.serverName
        this.revision = engine.revision
    }

    /**
     * Starts the network server.
     */
    private fun startNetwork(): ChannelFuture {
        val listenAddress = Conf.SERVER[ServerConfig.address]
        val listenPort = Conf.SERVER[ServerConfig.port]

        this.address = InetSocketAddress(listenAddress, listenPort)

        /**
         * Lazy injection of [NetworkServer]. Pipes the address as a parameter.
         */
        val networkServer: NetworkServer by inject { parametersOf(address) }

        networkServer.start()

        return networkServer.future
    }

}