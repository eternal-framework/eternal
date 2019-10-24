package dev.eternal.net.pipeline

import dev.eternal.engine.net.Packet
import dev.eternal.net.session.Session
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

/**
 * The primary channel message encoder for the piplines.
 * Responsible for delegating encoding logic to the protocol.
 *
 * @author Cody Fullen
 */
class ClientChannelEncoder(private val session: Session) : MessageToByteEncoder<Packet>() {

    /**
     * Encodes the packet to be sent outbound.
     */
    override fun encode(ctx: ChannelHandlerContext, msg: Packet, out: ByteBuf) = session.provider.current.encode(session, msg, out)

}