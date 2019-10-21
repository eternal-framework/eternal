package dev.eternal.launcher.check.impl

import dev.eternal.launcher.check.Check
import dev.eternal.launcher.check.CheckAction
import dev.eternal.util.Server.logger
import java.io.File

/**
 * Checks to make sure all the default required directories exist.
 * If not, [CheckAction] will create them.
 *
 * @author Cody Fullen
 */
class DirectoryCheck : Check, CheckAction {

    private val dirs = arrayOf(
        "data/",
        "data/cache/",
        "data/config/",
        "data/xteas/",
        "data/saves/",
        "data/rsa/",
        "data/plugins/",
        "data/logs/"
    )

    /**
     * Checks each directory string to see if it exists.
     */
    override fun check(): Boolean {
        dirs.forEach { path ->
            val dir = File(path)
            if(!dir.exists()) return false
        }
        return true
    }

    /**
     * Creates each directory string if it does NOT exist.
     */
    override fun action() {
        dirs.forEach { path ->
            val dir = File(path)
            if(!dir.exists()) {
                dir.mkdirs()
                logger.info { "Created default directory '$path' as it did not exist." }
            }
        }
    }
}