package dev.eternal.util.test

/**
 * A collection of useful methods for testing.
 *
 * @author Cody Fullen
 */
object TestUtils {

    /**
     * Gets and returns a private field using reflection.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getField(other: Any, name: String): T {
        val clazz = other::class.java
        val field = clazz.getDeclaredField(name)
        field.isAccessible = true
        return field.get(other) as T
    }

}