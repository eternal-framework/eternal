package dev.eternal.net.protocol.js5.packet

import dev.eternal.engine.net.Packet

/**
 * @author Cody Fullen
 */
data class JS5CacheRequest(val index: Int, val archive: Int, val priority: Boolean) : Packet