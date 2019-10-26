package dev.eternal.engine.service.login

import dev.eternal.engine.model.entity.Client
import dev.eternal.util.Server.logger
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import java.util.concurrent.atomic.AtomicReference

/**
 * Represents a client's login attempt request. Defined by a builder.
 *
 * @author Cody Fullen
 */
class LoginRequest private constructor(
    val client: Client,
    val username: String,
    val password: String,
    val revision: Int,
    val reconnecting: Boolean,
    val mobile: Boolean,
    val clientResizable: Boolean,
    val clientWidth: Int,
    val clientHeight: Int
) {

    /**
     * The DSL builder subclass
     */
    class Builder {
        private var client = AtomicReference<Client>(null)
        private var username: String = ""
        private var password: String = ""
        private var revision: Int = -1
        private var reconnecting: Boolean = false
        private var mobile: Boolean = false
        private var clientResizable: Boolean = false
        private var clientWidth: Int = -1
        private var clientHeight: Int = -1

        private var ctx: ChannelHandlerContext? = null

        /**
         * Builds the request.
         */
        fun build(): LoginRequest {
            this.buildClient()
            return LoginRequest(
                client = client.get(),
                username = username,
                password = password,
                revision = revision,
                reconnecting = reconnecting,
                mobile = mobile,
                clientResizable = clientResizable,
                clientWidth = clientWidth,
                clientHeight = clientHeight
            )
        }

        /**
         * Sets the login credentials.
         *
         * @param username The username
         * @param password The plaintext password
         */
        fun setCredentials(username: String, password: String): Builder {
            this.username = username
            this.password = password
            return this
        }

        /**
         * Sets the revision number
         *
         * @param revision The revision number
         */
        fun setRevision(revision: Int): Builder {
            this.revision = revision
            return this
        }

        /**
         * Sets the reconnecting status
         *
         * @param state Whether this state is enabled or disabled
         */
        fun reconnecting(state: Boolean): Builder {
            this.reconnecting = state
            return this
        }

        /**
         * Sets whether the client is on mobile.
         *
         * @param state Whether this state is enabled or disabled
         */
        fun mobile(state: Boolean): Builder {
            this.mobile = state
            return this
        }

        /**
         * Sets whether the client is in resizable mode or not.
         *
         * @param state Whether this state is enabled or disabled.
         */
        fun setResizable(state: Boolean): Builder {
            this.clientResizable = state
            return this
        }

        /**
         * Sets the client window dimensions
         *
         * @param width The client window width in pixels
         * @param height The client height in pixels
         */
        fun setClientSize(width: Int, height: Int): Builder {
            this.clientWidth = width
            this.clientHeight = height
            return this
        }

        /**
         * Sets the channel context.
         *
         * @param ctx The channel context
         */
        fun setChannelContext(ctx: ChannelHandlerContext): Builder {
            this.ctx = ctx
            return this
        }

        /**
         * Builds the [Client] instance from the current settings.
         */
        private fun buildClient() {
            val client = Client(this.ctx!!, this.username, "")
            if(!this.client.compareAndSet(null, client)) {
                logger.error { "Unable to generate a Client more than once for the login request builder." }
            }
        }
   }

    companion object {

        /**
         * Creates a login request.
         */
        fun create() = Builder()

    }
}