package dev.eternal.net.protocol.handshake

import dev.eternal.engine.net.Packet
import dev.eternal.net.StatusType
import dev.eternal.net.protocol.Protocol
import dev.eternal.net.protocol.ProtocolProvider
import dev.eternal.net.session.Session
import dev.eternal.util.Server.logger
import io.netty.buffer.ByteBuf

/**
 * The handshake protocol is the first and default protocol in every connection.
 * Its purpose is for the client to tell the server which of two protocols is currently using.
 * JS5 or Login protocol.
 *
 * @author Cody Fullen
 */
class HandshakeProtocol : Protocol() {

    /**
     * The handshake protocol does not send any messages to the client.
     * Therefor its encoder logic is not needed.
     */
    override fun encode(session: Session, packet: Packet, out: ByteBuf) {}

    /**
     * Decodes the handshake into a [HandshakeRequest] packet.
     */
    override fun decode(session: Session, buf: ByteBuf, out: MutableList<Any>) {
        if(!buf.isReadable) return

        val opcode = buf.readByte().toInt()
        val handshake = HandshakeType.values.firstOrNull { it.opcode == opcode }

        if(handshake == null) {
            /**
             * If the handshake opcode does not match handled ones.
             * Log it and clear the buffer.
             */
            buf.readBytes(buf.readableBytes())

            logger.warn { "Unhandled handshake opcode $opcode." }
            return
        }

        val request = HandshakeRequest(handshake)
        out.add(request)
    }

    /**
     * Handles the [HandshakeRequest] packet.
     * Checks what the handshake is requesting based on the packet handshake type.
     * Sets the respective session protocol.
     */
    override fun handle(session: Session, packet: Packet) {
        if(packet is HandshakeRequest) {

            when(packet.type) {

                HandshakeType.JS5 -> {
                    session.provider.setProtocol(ProtocolProvider.Type.JS5)
                }

                /**
                 * When the handshake is Login,
                 * We need to change the curent session protocol as well as send two things.
                 * The status type acceptable
                 * The randomized client seed.
                 */
                HandshakeType.LOGIN -> {
                    session.provider.setProtocol(ProtocolProvider.Type.LOGIN)
                    session.ctx.writeAndFlush(session.ctx.alloc().buffer(1).writeByte(StatusType.ACCEPTABLE.id))
                    session.ctx.writeAndFlush(session.ctx.alloc().buffer(8).writeLong(session.seed))
                }

            }

        } else {
            logger.warn { "Received unknown packet ${packet::class.java.simpleName} in HandshakeProtocol." }
        }
    }
}