package dev.eternal.net.pipeline

import dev.eternal.util.Injectable
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.timeout.IdleStateHandler
import io.netty.handler.traffic.ChannelTrafficShapingHandler
import io.netty.handler.traffic.GlobalTrafficShapingHandler
import org.koin.core.inject
import java.util.concurrent.Executors

/**
 * Responsible for building the initial channel pipeline for new
 * established connections.
 *
 * @author Cody Fullen
 */
class ClientChannelBuilder : ChannelInitializer<SocketChannel>(), Injectable {

    /**
     * The global traffic shaper is responsible for rate-limiting traffic across the entire
     * systems bandwidth.
     * BY DEFAULT: This is disabled, however if you happen to have other services running that need bandwidth should
     * the server's network link be maxed, you may want to set this.
     *
     * Checks for rates every 1000ms (1 second).
     * Set read + write limit in bits per seconds.
     */
    private val globalTraffic = GlobalTrafficShapingHandler(Executors.newSingleThreadScheduledExecutor(), 0, 0, 1000)

    /**
     * Initializes the channel.
     *
     * @param ch The socket channel that has just been established.
     */
    override fun initChannel(ch: SocketChannel) {

        /**
         * The channel traffic shaper is the same as the global expect it limits individual channel connections.
         * BY DEFAULT: Channels can only read 100mbps (1 MB / s)
         */
        val channelTraffic = ChannelTrafficShapingHandler(0, 1024 * 1024, 1000)

        /**
         * Defines how long before the pipeline is considered inactive.
         * If the channel has not received a Future after sending data for 30 seconds, the channel is closed forcibly.
         */
        val timeout = IdleStateHandler(0, 30, 1000)

        /**
         * The injector created by the dependency injector.
         */
        val handler: ClientChannelHandler by inject()

        val p = ch.pipeline()

        /**
         * Build the default pipeline.
         */
        p.addLast("global_traffic", globalTraffic)
        p.addLast("channel_traffic", channelTraffic)
        p.addLast("timeout", timeout)
        p.addLast("handler", handler)
    }

}