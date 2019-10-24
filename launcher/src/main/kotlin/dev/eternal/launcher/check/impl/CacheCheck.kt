package dev.eternal.launcher.check.impl

import dev.eternal.launcher.check.Check
import dev.eternal.util.PathConstants
import net.runelite.cache.fs.Store
import java.io.File

/**
 * Checks to ensure there is valid cache files in the cache data directory.
 *
 * @author Cody Fullen
 */
class CacheCheck : Check {

    /**
     * Loads a weak reference [Store] from cache dir.
     * Checks to see if if any indexes exist.
     */
    override fun check(): Boolean {
        val store = Store(File(PathConstants.CACHE_FOLDER_PATH))
        store.load()

        val count = store.indexes.size

        store.close()

        return (count > 0)
    }

}