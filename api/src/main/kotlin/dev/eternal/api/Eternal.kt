package dev.eternal.api

import dev.eternal.util.Injectable
import org.koin.core.inject

/**
 * A static reference to various API singleton instances.
 *
 * @author Cody Fullen
 */
object Eternal : Injectable {

    /**
     * The [Engine] singleton instance.
     */
    val engine: Engine by inject()

}