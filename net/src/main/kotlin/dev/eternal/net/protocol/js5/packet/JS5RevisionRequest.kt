package dev.eternal.net.protocol.js5.packet

import dev.eternal.engine.net.Packet

/**
 * @author Cody Fullen
 */
data class JS5RevisionRequest(val revision: Int) : Packet