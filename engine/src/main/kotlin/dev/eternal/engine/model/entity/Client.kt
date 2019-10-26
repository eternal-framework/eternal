package dev.eternal.engine.model.entity

import io.netty.channel.ChannelHandlerContext

/**
 * Represents a connected remote client in the game.
 *
 * @author Cody Fullen
 */
class Client(
    val ctx: ChannelHandlerContext,
    val username: String,
    val passwordHash: String
) : Player() {

    /**
     * Gets the [Player] object of this client.
     */
    val player: Player = this

}