package dev.eternal.net

import dev.eternal.config.Conf
import dev.eternal.config.impl.ServerConfig
import dev.eternal.util.Injectable
import dev.eternal.util.Server.logger
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.net.InetSocketAddress

/**
 * Represents a network threaded server which binds to an [InetSocketAddress]
 * This is the backbone of how the network is constructed.
 *
 * @author Cody Fullen
 */
abstract class SocketServer(open val address: InetSocketAddress) : Injectable {

    /**
     * Network worker threads.
     */
    private val bossGroup = NioEventLoopGroup(Conf.SERVER[ServerConfig.network_threads])
    private val workerGroup = NioEventLoopGroup(1)

    /**
     * The server bootstrap instance.
     */
    internal val bootstrap = ServerBootstrap()

    /**
     * The [Channel] instance.
     * This is null until it is set after binding.
     */
    private var channel: Channel? = null

    /**
     * Initializes the server bootstrap.
     */
    private fun init() {
        bootstrap
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
    }

    /**
     * Binds the bootstrapped server to [address]
     */
    internal fun bind(before: (SocketServer) -> Unit): ChannelFuture {
        /**
         * Initialize the bootstrap.
         */
        this.init()

        /**
         * Run the [before] Unit.
         */
        before(this)

        /**
         * Bind the socket.
         */
        val future = bootstrap.bind(address).addListener { f ->
            if(f.isSuccess) {
                this.onBindSuccess()
            } else {
                this.onBindFailure(f.cause())
            }
        }

        channel = future.channel()

        return future
    }

    /**
     * Invoked after the socket is successfully bound to [address]
     */
    internal abstract fun onBindSuccess()

    /**
     * Invoked after the socket attempts to bind to [address] but fails.
     *
     * @param cause The [Throwable] cause of the bind failure.
     */
    internal abstract fun onBindFailure(cause: Throwable)

    /**
     * Attempts to shutdown the sockets gracefully.
     */
    internal fun shutdown() {
        logger.info { "Shutting down network sockets gracefully." }

        channel?.close()
        bootstrap.config().group().shutdownGracefully()
        bootstrap.config().childGroup().shutdownGracefully()

        try {
            bootstrap.config().group().terminationFuture().sync()
            bootstrap.config().childGroup().terminationFuture().sync()
        } catch(e : InterruptedException) {
            logger.error(e) { "Socket server shutdown process interrupted!" }
        }
    }
}