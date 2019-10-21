package dev.eternal.launcher.check

/**
 * Represents an action to be performed if a [Check] fails.
 *
 * @author Cody Fullen
 */
interface CheckAction {

    /**
     * The action logic that is invoked if a check fails.
     */
    fun action()

}