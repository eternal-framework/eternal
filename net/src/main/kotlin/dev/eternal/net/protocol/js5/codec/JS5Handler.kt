package dev.eternal.net.protocol.js5.codec

import com.google.common.primitives.Ints
import dev.eternal.engine.Engine
import dev.eternal.net.StatusType
import dev.eternal.net.protocol.js5.packet.JS5CacheRequest
import dev.eternal.net.protocol.js5.packet.JS5CacheResponse
import dev.eternal.net.protocol.js5.packet.JS5RevisionRequest
import dev.eternal.net.protocol.js5.packet.JS5StatusResponse
import dev.eternal.net.session.Session
import dev.eternal.util.Injectable
import dev.eternal.util.Server.logger
import io.netty.channel.ChannelFutureListener
import net.runelite.cache.fs.Container
import net.runelite.cache.fs.Store
import net.runelite.cache.fs.jagex.CompressionType
import net.runelite.cache.fs.jagex.DiskStorage
import org.koin.core.inject

/**
 * A static alias for handle() in the JS5Protocol
 *
 * @author Cody Fullen
 */
object JS5Handler : Injectable {

    /**
     * The dependency injected [Engine] singleton.
     */
    private val engine: Engine by inject()

    /**
     * The [Engine] loaded cachestore.
     */
    private val cachestore: Store get() = engine.cachestore

    /**
     * The cached game cache index bytes in memory.
     * This is used to speed up downloading.
     */
    private var CACHED_INDEX_DATA: ByteArray? = null

    /**
     * Processes the [JS5RevisionRequest] packet.
     */
    fun handleRevisionRequest(session: Session, request: JS5RevisionRequest): Boolean {
        /**
         * If the engine revision does NOT match the client, send the
         * StatusType with status of REVISION_MISMATCH.
         * Close the channel upon sending the status.
         */
        return if(request.revision != engine.revision) {
            logger.info { "Session ${session.uuid} rejected JS5 request due to REVISION_MISMATCH." }

            val response = JS5StatusResponse(StatusType.REVISION_MISMATCH)
            session.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)

            false
        } else {
            val response = JS5StatusResponse(StatusType.ACCEPTABLE)
            session.writeAndFlush(response)

            true
        }
    }

    /**
     * Processes the [JS5CacheRequest] packet.
     *
     * When the requested index is 255, This is the start of an archive,
     * Send the index file bytes.
     * Otherwise send the archive file bytes.
     */
    fun handleCacheRequest(session: Session, request: JS5CacheRequest) {
        when(request.index) {
            255 -> this.encodeCacheIndexes(session, request)
            else -> this.encodeCacheArchives(session, request)
        }
    }

    /**
     * Encodes the game cache index values from [CACHED_INDEX_DATA] or from [cachestore]
     *
     * @param session The session sending the request.
     * @param request The cache request packet
     */
    private fun encodeCacheIndexes(session: Session, request: JS5CacheRequest) {
        val data: ByteArray

        if(request.archive == 255) {
            if(CACHED_INDEX_DATA == null) {
                val buf = session.ctx.alloc().heapBuffer(cachestore.indexes.size * 8)

                cachestore.indexes.forEach { index ->
                    buf.writeInt(index.crc)
                    buf.writeInt(index.revision)
                }

                val container = Container(CompressionType.NONE, -1)
                container.compress(buf.array().copyOf(buf.readableBytes()), null)

                CACHED_INDEX_DATA = container.data
                buf.release()
            }
            data = CACHED_INDEX_DATA!!
        } else {
            val storage = cachestore.storage as DiskStorage
            data = storage.readIndex(request.archive)
        }

        val response = JS5CacheResponse(request.index, request.archive, data)
        session.writeAndFlush(response)
    }

    /**
     * Encodes the game cache archives as bytes from [cachestore]
     *
     * @param session The session sending the request
     * @param request The cache request packet
     */
    private fun encodeCacheArchives(session: Session, request: JS5CacheRequest) {
        val index = cachestore.findIndex(request.index)!!
        val archive = index.getArchive(request.archive)!!
        var data = cachestore.storage.loadArchive(archive)

        if(data != null) {
            val compression = data[0]
            val length = Ints.fromBytes(data[1], data[2], data[3], data[4])
            val cacheLength = length + (if(compression.toInt() != CompressionType.NONE) 9 else 5)

            if(cacheLength != length && data.size - cacheLength == 2) {
                data = data.copyOf(data.size - 2)
            }

            val response = JS5CacheResponse(request.index, request.archive, data)
            session.writeAndFlush(response)
        } else {
            logger.warn { "Unable to encode cache data. Is it corrupt? index=${request.index} archive=${request.archive}." }
        }
    }

}