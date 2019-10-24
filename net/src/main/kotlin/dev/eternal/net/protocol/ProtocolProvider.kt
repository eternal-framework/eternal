package dev.eternal.net.protocol

import dev.eternal.net.protocol.handshake.HandshakeProtocol
import dev.eternal.net.protocol.js5.JS5Protocol
import dev.eternal.net.session.Session
import dev.eternal.util.Injectable

/**
 * A session dependent store of [Protocol]s
 *
 * @author Cody Fullen
 */
class ProtocolProvider(val session: Session) : Injectable {


    /**
     * The current session protocol.
     */
    private var protocol: Type = Type.HANDSHAKE

    /**
     * Gets the current session protocol.
     */
    val current: Protocol get() = this.protocol.protocol

    /**
     * Changes the session protocol.
     *
     * @param type The [Type] to change to.
     */
    fun setProtocol(type: Type) {
        this.protocol = type
    }

    enum class Type(val protocol: Protocol) {

        HANDSHAKE(HandshakeProtocol()),
        JS5(JS5Protocol())

    }
}