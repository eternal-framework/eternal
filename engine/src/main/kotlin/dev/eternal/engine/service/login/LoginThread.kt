package dev.eternal.engine.service.login

import dev.eternal.util.Server.logger
import io.netty.channel.ChannelFutureListener

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

                request.client.ctx.writeAndFlush(request.client.ctx.alloc().buffer(1).writeByte(8))
                    .addListener(ChannelFutureListener.CLOSE)

            } catch(e : Exception) {
                logger.error(e) { "An error occurred when processing login request." }
            }
        }
    }

}