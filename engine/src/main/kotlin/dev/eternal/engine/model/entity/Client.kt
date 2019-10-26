package dev.eternal.engine.model.entity

import io.netty.channel.Channel

/**
 * Represents a connected remote client in the game.
 *
 * @author Cody Fullen
 */
class Client(
    val channel: Channel,
    val username: String,
    val passwordHash: String
) : Player() {

    /**
     * Gets the [Player] object of this client.
     */
    val player: Player = this

}