package dev.eternal.net.protocol.login.packet

import dev.eternal.engine.net.Packet
import dev.eternal.net.StatusType

/**
 * @author Cody Fullen
 */
data class LoginStatusResponse(val status: StatusType) : Packet