package dev.eternal.engine.service.login

import com.google.common.util.concurrent.ThreadFactoryBuilder
import dev.eternal.config.Conf
import dev.eternal.config.impl.ServerConfig
import dev.eternal.engine.service.Service
import dev.eternal.util.Server.logger
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

/**
 * Responsible for handling login request logic for the server.
 *
 * @author Cody Fullen
 */
class LoginService : Service {

    /**
     * The login request queue.
     */
    internal val loginQueue = LinkedBlockingQueue<LoginRequest>()

    /**
     * The number of login threads to use. Set in the server config.
     */
    private val loginThreads: Int = Conf.SERVER[ServerConfig.login_threads]

    /**
     * The executor service that runs the login threads.
     */
    private val executor = Executors.newFixedThreadPool(this.loginThreads,
        ThreadFactoryBuilder()
            .setNameFormat("login-queue")
            .setUncaughtExceptionHandler { t, e -> logger.error(e) { "An error occurred in thread $t." } }
            .build()
    )

    override fun start() {
        for(i in 0 until loginThreads) {
            executor.execute(LoginThread(this))
        }

        logger.info { "Login service has been started on $loginThreads threads." }
    }

    override fun stop() {
        executor.shutdown()

        logger.info { "Login service has been stopped." }
    }

    /**
     * Adds a [LoginRequest] to the queue.
     *
     * @param request The [LoginRequest] to queue.
     */
    fun queue(request: LoginRequest) = this.loginQueue.offer(request)
}