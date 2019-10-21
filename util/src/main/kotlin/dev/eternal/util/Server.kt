package dev.eternal.util

import com.github.michaelbull.logging.InlineLogger

/**
 * The global kotlin logger.
 * This is resolved dynamically using JDK8+ MethodUsages().
 *
 * @author Cody Fullen
 */

object Server {

    /**
     * The global logger.
     */
    val logger = InlineLogger()

}