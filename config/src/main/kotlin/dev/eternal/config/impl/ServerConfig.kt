package dev.eternal.config.impl

import com.uchuhimo.konf.ConfigSpec
import dev.eternal.config.AbstractConfig
import dev.eternal.config.FileFormat
import dev.eternal.util.PathConstants

/**
 * The main settings that controls various options on the server.
 *
 * @author Cody Fullen
 */
class ServerConfig : AbstractConfig<Any>(PathConstants.SERVER_CONFIG_PATH, Companion) {

    override val fileFormat: FileFormat get() = FileFormat.YAML

    override fun load(ctx: Any): Any { throw Exception("Loading contexts for ServerConfig not supported.") }
    override fun save(ctx: Any) { throw Exception ("Saving contexts for ServerConfig not supported.") }

    companion object : ConfigSpec("server") {
        val server_name by optional("Eternal RSPS", "server_name")
        val revision by optional(184, "revision")
        val debug by optional(true, "debug")
        val login_threads by optional(3, "login_threads")
        val auto_create_new_users by optional(true, "auto_create_new_users")

        /**
         * Network settings
         */
        val address by optional("0.0.0.0", "network.address")
        val port by optional(43594, "network.port")
        val network_threads by optional(2, "network.threads")
    }
}