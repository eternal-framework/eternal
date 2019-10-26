package dev.eternal.net.protocol.login.codec

import dev.eternal.engine.Engine
import dev.eternal.engine.service.login.LoginRequest
import dev.eternal.engine.service.login.LoginService
import dev.eternal.net.protocol.login.packet.LoginRequestPacket
import dev.eternal.net.session.Session
import dev.eternal.util.Injectable
import dev.eternal.util.Server.logger
import org.koin.core.inject

/**
 * @author Cody Fullen
 */
object LoginHandler : Injectable {

    private val engine: Engine by inject()

    /**
     * Processes the login request from [LoginRequestPacket].
     * Uses the [Engine] login request builder to generate
     * everything.
     */
    fun handleLoginRequest(session: Session, request: LoginRequestPacket) {
        logger.info { "Login request queued for username: [${request.username}]." }

        val loginRequest = LoginRequest.create()
            .setCredentials(request.username, request.password)
            .setRevision(request.revision)
            .setResizable(request.clientResizable)
            .setClientSize(request.clientWidth, request.clientHeight)
            .reconnecting(request.reconnecting)
            .mobile(request.mobile)
            .setChannelContext(session.ctx)
            .build()

        engine.services[LoginService::class].queue(loginRequest)
    }

}