package dev.eternal.net.protocol.login.codec

import dev.eternal.net.protocol.login.packet.LoginRequestPacket
import dev.eternal.net.session.Session
import dev.eternal.util.Injectable

/**
 * @author Cody Fullen
 */
object LoginHandler : Injectable {

    /**
     * Processes the login request from [LoginRequestPacket].
     * Uses the [Engine] login request builder to generate
     * everything.
     */
    fun handleLoginRequest(session: Session, request: LoginRequestPacket) {

    }

}