package dev.eternal.engine.module

/**
 * Represents an object that is loaded / initialized during engine initialization.
 * These can also just be seperate logical systems required for the engine to function but
 * are too small to be their own gradle module.
 *
 * Example, RSA loading, XTEA key loading, etc.
 *
 * @author Cody Fullen
 */
interface Module {

    /**
     * Executed during engine initialization.
     */
    fun init()

}