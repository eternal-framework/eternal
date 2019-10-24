package dev.eternal.net.protocol.js5.packet

import dev.eternal.engine.net.Packet
import dev.eternal.net.StatusType

/**
 * @author Cody Fullen
 */
data class JS5StatusResponse(val status: StatusType) : Packet