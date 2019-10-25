package dev.eternal.launcher.check.impl

import dev.eternal.launcher.check.Check
import dev.eternal.util.PathConstants
import dev.eternal.util.Server.logger
import java.io.File

/**
 * Responsible for checking if you have an RSA key in the data directory.
 * @author Cody Fullen
 */
class RSACheck : Check {
    /**
     * Checks if the RSA private key file exists.
     */
    override fun check(): Boolean {
        val file = File(PathConstants.RSA_PRIVATE_KEY_PATH)
        if(!file.exists()) {
            logger.error { "MISSING RSA KEYS! Run the gradle task 'Tasks > rsa > generate' to generate a new one." }
            return false
        }
        return true
    }
}