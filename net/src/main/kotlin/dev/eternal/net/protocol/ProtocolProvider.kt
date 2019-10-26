package dev.eternal.net.protocol

import dev.eternal.net.protocol.handshake.HandshakeProtocol
import dev.eternal.net.protocol.js5.JS5Protocol
import dev.eternal.net.protocol.login.LoginProtocol
import dev.eternal.net.session.Session
import dev.eternal.util.Injectable
import org.koin.core.get
import org.koin.core.inject
import java.lang.IllegalArgumentException

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
    var current: Protocol = this.update()

    /**
     * Changes the session protocol.
     *
     * @param type The [Type] to change to.
     */
    fun setProtocol(type: Type) {
        this.protocol = type
        this.current = this.update()
    }

    /**
     * Updates and returns a new instance of the current protocol type.
     */
    private fun update(): Protocol {
        return when(protocol) {
            Type.HANDSHAKE -> get<HandshakeProtocol>()
            Type.JS5 -> get<JS5Protocol>()
            Type.LOGIN -> get<LoginProtocol>()
        }
    }

    enum class Type {

        HANDSHAKE,
        JS5,
        LOGIN;
    }
}