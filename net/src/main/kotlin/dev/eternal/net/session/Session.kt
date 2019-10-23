package dev.eternal.net.session

import dev.eternal.net.NetworkServer
import dev.eternal.util.Injectable
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import org.koin.core.inject
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
     * Invoked when the session first connects.
     */
    internal fun onConnect() {

    }

    /**
     * Invoked when the session's channel closes.
     */
    internal fun onDisconnect() {
        networkServer.terminateSession(this)
    }

    /**
     * Invoked when a message is received from the client. (After being decoded)
     *
     * @param msg The decoded message.
     */
    internal fun onMessageReceived(msg: Any) {

    }

    /**
     * Invoked when an exception is thrown in the [Session] thread.
     *
     * @param cause The [Throwable] cause of the exception.
     */
    fun onError(cause: Throwable) {

    }

    /**
     * Closes the session channel.
     */
    fun close() {
        if(channel.isActive) {
            channel.close()
        }
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