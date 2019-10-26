package dev.eternal.engine.service.login

import dev.eternal.util.Server.logger

/**
 * Responsible for waiting an processing a [LoginRequest] from the [LoginService] queue.
 *
 * @author Cody Fullen
 */
class LoginThread(private val service: LoginService) : Runnable {

    override fun run() {
        while(true) {
            val request = service.loginQueue.take()
            try {

                println("Received login request for username: ${request.username}")

            } catch(e : Exception) {
                logger.error(e) { "An error occurred when processing login request." }
            }
        }
    }

}