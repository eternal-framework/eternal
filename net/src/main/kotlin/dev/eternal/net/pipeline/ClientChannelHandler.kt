package dev.eternal.net.pipeline

import dev.eternal.net.NetworkServer
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * Responsible for handling the initial connection from newly established clients.
 *
 * @author Cody Fullen
 */
class ClientChannelHandler(private val networkServer: NetworkServer) : ChannelInboundHandlerAdapter() {

    /**
     * Invoked when the channel becomes active.
     */
    override fun channelActive(ctx: ChannelHandlerContext) {

    }

    /**
     * Invoked when the channel becomes inactive.
     */
    override fun channelInactive(ctx: ChannelHandlerContext) {

    }

    /**
     * Invoked when the server receives in inbound message. (After its been decoded.)
     */
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {

    }

    /**
     * Invoked when an exception is thrown in the channel thread.
     */
    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {

    }

}