package dev.eternal.net.protocol.handshake

/**
 * The different handshake and their opcode.
 * @author Cody Fullen
 */
enum class HandshakeType(val opcode: Int) {

    JS5(15),
    LOGIN(14);

    companion object {
        val values = enumValues<HandshakeType>()
    }
}