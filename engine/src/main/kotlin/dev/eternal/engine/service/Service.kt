package dev.eternal.engine.service

/**
 * Represents a service which is loading during engine initialization.
 * Typically used as a wrapper for thread based cycle logic.
 *
 * @author Cody Fullen
 */
interface Service {

    /**
     * Executed when starting the service
     */
    fun start()

    /**
     * Executed when stopping the service.
     */
    fun stop()

}