package dev.eternal.net.protocol.js5.codec

import dev.eternal.net.protocol.js5.packet.JS5CacheResponse
import dev.eternal.net.protocol.js5.packet.JS5StatusResponse
import io.netty.buffer.ByteBuf

/**
 * A static alias for the protocol encode() method.
 * This pulled out of the protocol class for organization.
 *
 * @author Cody Fullen
 */
object JS5Encoder {

    /**
     * Encodes [JS5StatusResponse] packet into bytes.
     *
     * @param packet The [JS5StatusResponse] packet
     * @param out The outbound [ByteBuf] where the bytes are written.
     */
    fun encodeStatusResponse(packet: JS5StatusResponse, out: ByteBuf) {
        out.writeByte(packet.status.id)
    }

    /**
     * Encodes [JS5CacheResponse] packet into bytes.
     *
     * @param packet The [JS5CacheResponse] packet
     * @param out The outbound [ByteBuf] where the bytes are written
     */
    fun encodeCacheResponse(packet: JS5CacheResponse, out: ByteBuf) {
        /**
         * NOTE. Every 512 bytes when reading the data, the client reads a Byte value of (-1)
         * We use the writer index and write a (-1) every 512 bytes.
         */
        out.writeByte(packet.index)
        out.writeShort(packet.archive)

        packet.data.forEach { byte ->
            if(out.writerIndex() % 512 == 0) {
                out.writeByte(-1)
            }
            out.writeByte(byte.toInt())
        }
    }

}