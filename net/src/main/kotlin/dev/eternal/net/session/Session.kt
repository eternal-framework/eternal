package dev.eternal.net.session

import dev.eternal.config.Conf
import dev.eternal.config.impl.ServerConfig
import dev.eternal.engine.net.Packet
import dev.eternal.net.NetworkServer
import dev.eternal.net.pipeline.ClientChannelDecoder
import dev.eternal.net.pipeline.ClientChannelEncoder
import dev.eternal.net.protocol.ProtocolProvider
import dev.eternal.util.Injectable
import dev.eternal.util.Server.logger
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import java.util.*

/**
 * Represents a connection which has an open channel connection.
 *
 * @author Cody Fullen
 */
class Session(val ctx: ChannelHandlerContext) : Injectable {

    /**
     * Dependency inject [NetworkServer] singleton.
     */
    private val networkServer: NetworkServer by inject()

    /**
     * The [Channel] of this session.
     */
    val channel: Channel get() = ctx.channel()

    /**
     * The [UUID] as a string of this session.
     */
    val uuid: String = UUID.randomUUID().toString()

    /**
     * The protocol provider for this session.
     */
    internal val provider: ProtocolProvider by inject { parametersOf(this) }

    /**
     * The current randomized seed of the session
     */
    internal var seed: Long = (Math.random() * Long.MAX_VALUE).toLong()

    /**
     * Invoked when the session first connects.
     * Builds the initial pipeline.
     */
    internal fun onConnect() {
        val p = ctx.pipeline()

        val encoder: ClientChannelEncoder by inject { parametersOf(this) }
        val decoder: ClientChannelDecoder by inject { parametersOf(this) }

        p.addBefore("handler", "rs_encoder", encoder)
        p.addAfter("rs_encoder", "rs_decoder", decoder)
    }

    /**
     * Invoked when the session's channel closes.
     */
    internal fun onDisconnect() {
        networkServer.terminateSession(this)
        this.close()
    }

    /**
     * Invoked when a message is received from the client. (After being decoded)
     *
     * @param packet The decoded message.
     */
    internal fun onMessageReceived(packet: Packet) = this.provider.current.handle(this, packet)

    /**
     * Invoked when an exception is thrown in the [Session] thread.
     *
     * @param cause The [Throwable] cause of the exception.
     */
    fun onError(cause: Throwable) {
        if(cause.stackTrace.isEmpty() || cause.stackTrace[0].methodName != "read0") {
            logger.warn { "An error occurred in session $uuid: ${cause.message}." }

            if(Conf.SERVER[ServerConfig.debug]) {
                cause.printStackTrace()
            }

            this.close()
        }
    }

    /**
     * Closes the session channel.
     */
    fun close() {
        ctx.channel().close()
    }

    /**
     * Sends a [Packet] and flushes the buffer to
     * instantly send it over the channel.
     *
     * @param packet The packet object to send.
     *
     * @return [ChannelFuture] The channel future listener.
     */
    fun writeAndFlush(packet: Packet): ChannelFuture {
        return ctx.writeAndFlush(packet)
    }

    /**
     * Sends a [Packet].
     *
     * @param packet The packet object to send.
     *
     * @return [ChannelFuture] The channel future listener.
     */
    fun write(packet: Packet): ChannelFuture {
        return ctx.write(packet)
    }

    /**
     * Flushes the session channel buffer.
     */
    fun flush() {
        channel.flush()
    }

    override fun equals(other: Any?): Boolean {
        if(other is Session) {
            return other.uuid == uuid
        }
        return false
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}