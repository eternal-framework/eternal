package dev.eternal.launcher.check.impl

import dev.eternal.engine.module.ModuleStore
import dev.eternal.engine.module.impl.RSAModule
import dev.eternal.launcher.check.Check
import dev.eternal.launcher.check.CheckAction
import dev.eternal.util.Injectable
import dev.eternal.util.PathConstants
import dev.eternal.util.Server.logger
import org.koin.core.inject
import java.io.File

/**
 * Responsible for checking if you have an RSA key in the data directory.
 * @author Cody Fullen
 */
class RSACheck : Check, CheckAction, Injectable {

    private val moduleStore: ModuleStore by inject()
    private val rsaModule = moduleStore[RSAModule::class]

    /**
     * Checks if the RSA private key file exists.
     */
    override fun check(): Boolean {
        val file = File(PathConstants.RSA_PRIVATE_KEY_PATH)
        return file.exists()
    }

    override fun action() {
        logger.warn { "RSA Private key was not found. Generating a new one." }
        rsaModule.generatePair()
    }
}