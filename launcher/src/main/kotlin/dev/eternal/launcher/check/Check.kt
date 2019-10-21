package dev.eternal.launcher.check

/**
 * Represents a pre-start check logic.
 *
 * @author Cody Fullen
 */
interface Check {

    /**
     * The logic preformed during the check.
     *
     * @return [Boolean] The check result.
     */
    fun check(): Boolean

}