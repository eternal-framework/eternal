package dev.eternal.net.protocol.login

import dev.eternal.engine.net.Packet
import dev.eternal.net.protocol.Protocol
import dev.eternal.net.protocol.login.codec.LoginDecoder
import dev.eternal.net.protocol.login.codec.LoginEncoder
import dev.eternal.net.protocol.login.packet.LoginStatusResponse
import dev.eternal.net.session.Session
import dev.eternal.util.Server.logger
import io.netty.buffer.ByteBuf

/**
 * Responsible for handling login related requests.
 *
 * @author Cody Fullen
 */
class LoginProtocol : Protocol() {

    /**
     * The current state of the decoder in the protocol
     */
    internal var state: State = State.HANDSHAKE

    internal var loginType: LoginType = LoginType.NORMAL

    /**
     * Checks if the current protocol request is a reconnection
     */
    fun isReconnecting(): Boolean = (loginType == LoginType.RECONNECT)

    /**
     * Encodes outbound packets
     */
    override fun encode(session: Session, packet: Packet, out: ByteBuf) {
        when(packet) {
            is LoginStatusResponse -> LoginEncoder.encodeLoginState(packet, out)
            else -> logger.warn { "Unhandled [LoginProtocol] packet encoder for ${packet::class.java.simpleName}!" }
        }
    }

    /**
     * Decodes inbound bytes into packets.
     */
    override fun decode(session: Session, buf: ByteBuf, out: MutableList<Any>) {
        buf.markReaderIndex()
        when(state) {
            State.HANDSHAKE -> LoginDecoder.decodeHandshake(session, buf)
            State.PAYLOAD -> LoginDecoder.decodePayload(session, buf, out)
        }
    }

    /**
     * Handles decoded packets.
     */
    override fun handle(session: Session, packet: Packet) {

    }

    /**
     * The decoder state types
     */
    internal enum class State {
        HANDSHAKE,
        PAYLOAD
    }
}