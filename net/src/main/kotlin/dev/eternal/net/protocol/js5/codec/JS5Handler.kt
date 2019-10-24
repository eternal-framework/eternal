package dev.eternal.net.protocol.js5.codec

import dev.eternal.engine.Engine
import dev.eternal.net.StatusType
import dev.eternal.net.protocol.js5.packet.JS5RevisionRequest
import dev.eternal.net.protocol.js5.packet.JS5StatusResponse
import dev.eternal.net.session.Session
import dev.eternal.util.Injectable
import dev.eternal.util.Server.logger
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
     * Processes the [JS5RevisionRequest] packet.
     */
    fun handleRevisionRequest(session: Session, request: JS5RevisionRequest): Boolean {
        /**
         * If the engine revision does NOT match the client, send the
         * StatusType with status of REVISION_MISMATCH.
         */
        return if(request.revision != engine.revision) {
            logger.info { "Session ${session.uuid} rejected JS5 request due to REVISION_MISMATCH." }

            val response = JS5StatusResponse(StatusType.REVISION_MISMATCH)
            session.writeAndFlush(response)

            false
        } else {
            val response = JS5StatusResponse(StatusType.ACCEPTABLE)
            session.writeAndFlush(response)

            true
        }
    }

}