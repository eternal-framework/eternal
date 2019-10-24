package dev.eternal.net.protocol.js5.codec

import dev.eternal.net.protocol.js5.packet.JS5CacheRequest
import dev.eternal.net.protocol.js5.packet.JS5RevisionRequest
import dev.eternal.util.Server.logger
import io.netty.buffer.ByteBuf

/**
 * A static alias for the decode() method in JS5Protocol.
 *
 * @author Cody Fullen
 */
object JS5Decoder {

    /**
     * The JS5 request opcode values.
     */
    private const val CACHE_REQUEST_PRIORITY = 0
    private const val CACHE_REQUEST_NORMAL = 1
    private const val CLIENT_INIT = 2
    private const val CLIENT_LOAD = 3
    private const val CLIENT_READY = 6

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
    fun decodeCacheRequest(buf: ByteBuf, out: MutableList<Any>) {
        if(!buf.isReadable) return

        buf.markReaderIndex()
        when(val opcode = buf.readByte().toInt()) {
            /**
             * These values tell the server what the client gameState is.
             * Since we do not care when the client is done with its loading screen,
             * we can skip the bytes.
             */
            CLIENT_INIT, CLIENT_LOAD, CLIENT_READY -> {
                buf.skipBytes(3)
            }

            CACHE_REQUEST_PRIORITY, CACHE_REQUEST_NORMAL -> {
                if(buf.readableBytes() >= 3) {
                    val index = buf.readUnsignedByte().toInt()
                    val archive = buf.readUnsignedShort()
                    val priority = (opcode == CACHE_REQUEST_PRIORITY)

                    val request = JS5CacheRequest(index, archive, priority)
                    out.add(request)
                } else {
                    buf.resetReaderIndex()
                }
            }

            else -> logger.error { "Unhandled JS5 request opcode $opcode." }
        }
    }


}