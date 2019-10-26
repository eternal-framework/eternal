package dev.eternal.engine.model.entity

/**
 * Represents a game player which is controlled by a connected [Client]
 *
 * @author Cody Fullen
 */
abstract class Player : HumanEntity() {

    /**
     * Gets the [Client] object of this player.
     */
    internal val client: Client get() = this as Client

    /**
     * The name displayed in game to other [Player]s
     */
    lateinit var displayName: String
}