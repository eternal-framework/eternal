package dev.eternal.net.pipeline

import dev.eternal.engine.net.Packet
import dev.eternal.net.NetworkServer
import dev.eternal.net.session.Session
import dev.eternal.util.Server.logger
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import java.util.concurrent.atomic.AtomicReference

/**
 * Responsible for handling the initial connection from newly established clients.
 *
 * @author Cody Fullen
 */
class ClientChannelHandler(private val networkServer: NetworkServer) : ChannelInboundHandlerAdapter() {

    /**
     * The associated session to this handler instance.
     */
    private val session = AtomicReference<Session>(null)

    /**
     * Invoked when the channel becomes active.
     */
    override fun channelActive(ctx: ChannelHandlerContext) {
        val newSession = networkServer.newSession(ctx)

        if(!session.compareAndSet(null, newSession)) {
            logger.warn { "Unable to set handler session more than once." }
            return
        }

        newSession.onConnect()
    }

    /**
     * Invoked when the channel becomes inactive.
     */
    override fun channelInactive(ctx: ChannelHandlerContext) = session.get().onDisconnect()

    /**
     * Invoked when the server receives in inbound message. (After its been decoded.)
     */
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if(msg is Packet) {
            session.get().onMessageReceived(msg)
        }
    }

    /**
     * Invoked when an exception is thrown in the channel thread.
     */
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) = session.get().onError(cause)

}