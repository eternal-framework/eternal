package dev.eternal.net.protocol.js5

import dev.eternal.engine.net.Packet
import dev.eternal.net.protocol.Protocol
import dev.eternal.net.protocol.js5.codec.JS5Decoder
import dev.eternal.net.protocol.js5.codec.JS5Encoder
import dev.eternal.net.protocol.js5.codec.JS5Handler
import dev.eternal.net.protocol.js5.packet.JS5CacheRequest
import dev.eternal.net.protocol.js5.packet.JS5CacheResponse
import dev.eternal.net.protocol.js5.packet.JS5RevisionRequest
import dev.eternal.net.protocol.js5.packet.JS5StatusResponse
import dev.eternal.net.session.Session
import dev.eternal.util.Server.logger
import io.netty.buffer.ByteBuf

/**
 * Responsible for transferring the server cache files over the network to the client.
 * This is how OSRS syncs cache files.
 *
 * @author Cody Fullen
 */
class JS5Protocol : Protocol() {

    /**
     * This protocol has a stateful decoding state that gets changed.
     * This is required because if the client / server fails to send data,
     * sometimes it may just close the channel and resend the revision.
     *
     * This throws off the buffer and causes cache downloads to take forever.
     */

    /**
     * The current decode state.
     */
    private var decoderState = DecodeState.REVISION

    /**
     * Encodes outbound response packets.
     */
    override fun encode(session: Session, packet: Packet, out: ByteBuf) {
        when(packet) {
            is JS5StatusResponse -> JS5Encoder.encodeStatusResponse(packet, out)
            is JS5CacheResponse -> JS5Encoder.encodeCacheResponse(packet, out)
            else -> logger.warn { "Unhandled JS5Protocol packet ${packet::class.java.simpleName}." }
        }
    }

    /**
     * Decodes inbound bytes
     */
    override fun decode(session: Session, buf: ByteBuf, out: MutableList<Any>) {
        when(decoderState) {
            DecodeState.REVISION -> JS5Decoder.decodeRevisionRequest(buf, out)
            DecodeState.CACHE -> JS5Decoder.decodeCacheRequest(buf, out)
        }
    }

    /**
     * Handles decoded packets
     */
    override fun handle(session: Session, packet: Packet) {
        when(packet) {
            is JS5RevisionRequest -> if(JS5Handler.handleRevisionRequest(session, packet)) decoderState = DecodeState.CACHE
            is JS5CacheRequest -> JS5Handler.handleCacheRequest(session, packet)
            else -> logger.warn { "Unhandled JS5Protocol packet ${packet::class.java.simpleName}." }
        }
    }

    /**
     * The Decoder state types.
     */
    private enum class DecodeState {
        REVISION,
        CACHE;
    }
}