package dev.eternal.net.protocol.handshake

import dev.eternal.engine.net.Packet

/**
 * @author Cody Fullen
 */
data class HandshakeRequest(val type: HandshakeType) : Packet