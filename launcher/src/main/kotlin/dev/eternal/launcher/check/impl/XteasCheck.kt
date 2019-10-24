package dev.eternal.launcher.check.impl

import dev.eternal.launcher.check.Check
import dev.eternal.util.PathConstants
import java.io.File

/**
 * Checks if the xteas.json map decryption keys file exists.
 *
 * @author Cody Fullen
 */
class XteasCheck : Check {

    /**
     * Checks the xteas.json file and if it exists.
     */
    override fun check(): Boolean {
        val file = File(PathConstants.XTEAS_FILE_PATH)
        return file.exists()
    }

}