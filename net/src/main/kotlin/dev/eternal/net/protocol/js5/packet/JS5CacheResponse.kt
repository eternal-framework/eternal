package dev.eternal.net.protocol.js5.packet

import dev.eternal.engine.net.Packet

/**
 * @author Cody Fullen
 */
data class JS5CacheResponse(val index: Int, val archive: Int, val data: ByteArray) : Packet {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JS5CacheResponse

        if (index != other.index) return false
        if (archive != other.archive) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + archive
        result = 31 * result + data.contentHashCode()
        return result
    }
}