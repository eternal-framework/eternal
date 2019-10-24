package dev.eternal.engine.service.login

import dev.eternal.engine.service.Service
import dev.eternal.util.Server.logger

/**
 * Responsible for handling login request logic for the server.
 *
 * @author Cody Fullen
 */
class LoginService : Service {

    override fun start() {
        logger.info { "Login service has been started." }
    }

    override fun stop() {
        logger.info { "Login service has been stopped." }
    }

}