package dev.eternal.net

import dev.eternal.config.Conf
import dev.eternal.config.impl.ServerConfig
import dev.eternal.net.pipeline.ClientChannelBuilder
import dev.eternal.util.Server.logger
import io.netty.channel.ChannelFuture
import org.koin.core.inject
import java.net.InetSocketAddress

/**
 * Responsible for handling binding / shutdown of the network sockets.
 *
 * @author Cody Fullen
 */
class NetworkServer(override val address: InetSocketAddress) : SocketServer(address) {

    /**
     * The private state tracker.
     */
    private var state = Status.SHUTDOWN

    /**
     * Checks if the network server is running.
     */
    val isRunning: Boolean get() = (state == Status.RUNNING)

    /**
     * Checks if the network server is starting up.
     */
    val isStarting: Boolean get() = (state == Status.STARTUP)

    /**
     * The channel future after starting.
     */
    lateinit var future: ChannelFuture

    /**
     * Starts the network server.
     */
    fun start() {
        check(state == Status.SHUTDOWN) { "The server is already running." }
        state = Status.STARTUP

        logger.info { "Preparing to start the server network." }

        /**
         * Bind to the address.
         */
        val channelBuilder: ClientChannelBuilder by inject()
        this.future = this.bind { it.bootstrap.childHandler(channelBuilder) }
    }

    /**
     * Stops the network server.
     */
    fun stop() {
        check(state == Status.RUNNING) { "The server is not running." }
        state = Status.SHUTDOWN

        this.shutdown()
    }

    /**
     * Invoked when the socket is successfully bound.
     */
    override fun onBindSuccess() {
        state = Status.RUNNING
        logger.info { "Server network has been bound to address ${address.hostString}:${address.port}." }
    }

    /**
     * Invoked when the socket fails to bind.
     * If @link debug is true in the ServerConfig, the stacktrace is printed.
     */
    override fun onBindFailure(cause: Throwable) {
        state = Status.SHUTDOWN
        logger.error { "Unable to open socket on address ${address.hostString}:${address.port} : ${cause.message}" }

        if(Conf.SERVER[ServerConfig.debug]) {
            cause.printStackTrace()
        }
    }

    /**
     * Represents a status state of the [NetworkServer]
     */
    private enum class Status {
        SHUTDOWN,
        STARTUP,
        RUNNING
    }
}