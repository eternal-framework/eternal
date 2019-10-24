package dev.eternal.net.protocol.js5.codec

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

}