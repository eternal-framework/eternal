package dev.eternal.net.protocol.login.packet

import dev.eternal.engine.net.Packet
import dev.eternal.net.session.Session

/**
 * @author Cody Fullen
 */
data class LoginRequestPacket(
    val session: Session,
    val reconnecting: Boolean,
    val username: String,
    val password: String,
    val revision: Int,
    val authCode: Int,
    val mobile: Boolean,
    val clientResizable: Boolean,
    val clientWidth: Int,
    val clientHeight: Int
) : Packet