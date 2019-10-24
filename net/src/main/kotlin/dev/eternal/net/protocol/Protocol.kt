package dev.eternal.net.protocol

import dev.eternal.engine.net.Packet
import dev.eternal.net.session.Session
import dev.eternal.util.Injectable
import io.netty.buffer.ByteBuf

/**
 * Represents a base protocol that tells the pipeline how packets should be decoded.
 *
 * @author Cody Fullen
 */
abstract class Protocol : Injectable {

    /**
     * Encodes the outgoing packet to a [ByteBuf]
     */
    abstract fun encode(session: Session, packet: Packet, out: ByteBuf)

    /**
     * Decodes the inbound packet from a [ByteBuf] to a [Packet]
     */
    abstract fun decode(session: Session, buf: ByteBuf, out: MutableList<Any>)

    /**
     * The handler logic for the decoded [Packet]
     */
    abstract fun handle(session: Session, packet: Packet)

}