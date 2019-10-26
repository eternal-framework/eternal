package dev.eternal.net.protocol.login.codec

import dev.eternal.net.protocol.login.packet.LoginStatusResponse
import io.netty.buffer.ByteBuf

/**
 * @author Cody Fullen
 */
object LoginEncoder {

    /**
     * Encodes the [LoginStatusResponse] packet
     */
    fun encodeLoginState(packet: LoginStatusResponse, out: ByteBuf) {
        out.writeByte(packet.status.id)
    }

}