package dev.eternal.net.protocol.js5.codec

import dev.eternal.net.protocol.js5.packet.JS5RevisionRequest
import dev.eternal.net.session.Session
import io.netty.buffer.ByteBuf

/**
 * A static alias for the decode() method in JS5Protocol.
 *
 * @author Cody Fullen
 */
object JS5Decoder {

    /**
     * Decodes the sent client revision into a [JS5RevisionRequest] packet.
     */
    fun decodeRevisionRequest(buf: ByteBuf, out: MutableList<Any>) {
        if(buf.readableBytes() >= 4) {
            val revision = buf.readInt()
            val request = JS5RevisionRequest(revision)
            out.add(request)
        }
    }

    /**
     * Decodes the bytes into a [JS5CacheRequest] packet.
     */
    fun decodeCacheRequest(session: Session, buf: ByteBuf, out: MutableList<Any>) {

    }

}