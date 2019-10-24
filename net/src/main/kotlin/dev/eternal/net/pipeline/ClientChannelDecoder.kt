package dev.eternal.net.pipeline

import dev.eternal.net.session.Session
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

/**
 * Responsible for decoding inbound packets from bytes into their respective object packets.
 * Delegates the logic to the session protocol.
 *
 * @author Cody Fullen
 */
class ClientChannelDecoder(private val session: Session) : ByteToMessageDecoder() {

    /**
     * Decodes the inbound packets.
     */
    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) = session.provider.current.decode(session, buf, out)

}