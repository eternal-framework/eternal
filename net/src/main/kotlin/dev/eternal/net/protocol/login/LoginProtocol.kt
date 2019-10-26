package dev.eternal.net.protocol.login

import dev.eternal.engine.net.Packet
import dev.eternal.net.protocol.Protocol
import dev.eternal.net.session.Session
import io.netty.buffer.ByteBuf

/**
 * Responsible for handling login related requests.
 *
 * @author Cody Fullen
 */
class LoginProtocol : Protocol() {

    override fun encode(session: Session, packet: Packet, out: ByteBuf) {

    }

    override fun decode(session: Session, buf: ByteBuf, out: MutableList<Any>) {

    }

    override fun handle(session: Session, packet: Packet) {

    }

}