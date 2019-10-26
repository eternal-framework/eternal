package dev.eternal.net.protocol.login.codec

import dev.eternal.engine.Engine
import dev.eternal.engine.module.impl.RSAModule
import dev.eternal.net.StatusType
import dev.eternal.net.protocol.login.LoginProtocol
import dev.eternal.net.protocol.login.LoginType
import dev.eternal.net.protocol.login.packet.LoginRequestPacket
import dev.eternal.net.protocol.login.packet.LoginStatusResponse
import dev.eternal.net.session.Session
import dev.eternal.util.*
import dev.eternal.util.Server.logger
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import org.koin.core.inject
import java.math.BigInteger

/**
 * @author Cody Fullen
 */
object LoginDecoder : Injectable {

    private val engine: Engine by inject()

    /**
     * The RSA module which holds the RSA modulus and exponent.
     */
    private val rsa = engine.modules[RSAModule::class]

    /**
     * Decodes the handshake for the login request.
     *
     * @param session The associated session
     * @param buf The inbound byte buf.
     */
    fun decodeHandshake(session: Session, buf: ByteBuf) {
        if(!buf.isReadable) return

        try {
            val opcode = buf.readByte().toInt()
            val loginType = LoginType.values.firstOrNull { it.opcode == opcode } ?: throw IllegalStateException("Unhandled login type opcode $opcode")

            (session.provider.current as LoginProtocol).loginType = loginType
            (session.provider.current as LoginProtocol).state = LoginProtocol.State.PAYLOAD
        } catch(e : Exception) {
            logger.warn(e) { "Unable to decode login request handshake." }

            session.writeAndFlush(LoginStatusResponse(StatusType.COULD_NOT_COMPLETE_LOGIN))
                .addListener(ChannelFutureListener.CLOSE)
        }
    }

    /**
     * Decodes the request packet payload.
     *
     * @param session The associated session
     * @param buf The inbound byte buf
     * @param out The set of decoded packets
     */
    fun decodePayload(session: Session, buf: ByteBuf, out: MutableList<Any>) {
        var payloadLength = 0

        var revision: Int = -1

        var mobile = false

        /**
         * Decode the payload header information
         */
        val length = buf.readUnsignedShort()
        if(buf.readableBytes() >= length) {
            revision = buf.readInt()

            buf.skipBytes(Int.SIZE_BYTES)

            mobile = (buf.readByte().toInt() == 2)

            /**
             * Check the clients revision.
             */
            if(revision != engine.revision) {
                session.writeAndFlush(LoginStatusResponse(StatusType.REVISION_MISMATCH))
                    .addListener(ChannelFutureListener.CLOSE)
                return
            }

            payloadLength = length - (Int.SIZE_BYTES + Int.SIZE_BYTES + Byte.SIZE_BYTES)
        } else {
            buf.resetReaderIndex()
        }

        /**
         * Decode the payload
         */
        if(buf.readableBytes() >= payloadLength) {
            buf.markReaderIndex()

            val secureBuf: ByteBuf = this.buildSecureBuffer(buf)

            /**
             * Test to see if the buffer was successfully decrypted.
             * This check determines if the Client and Server's RSA keys are correct.
             */
            val decryptCheck = secureBuf.readUnsignedByte().toInt() == 1
            if(!decryptCheck) {
                logger.info { "Login request rejected due to failed RSA buffer decryption. Client has a malformed or incorrect RSA modulus." }

                buf.resetReaderIndex()
                buf.skipBytes(payloadLength)

                session.writeAndFlush(LoginStatusResponse(StatusType.MALFORMED_PACKET))
                    .addListener(ChannelFutureListener.CLOSE)
                return
            }

            val xteaKeys = IntArray(4) { secureBuf.readInt() }
            val seed = secureBuf.readLong()

            /**
             * Check if the reported client seed matches the pre-generated
             * session seed.
             */
            if(session.seed != seed) {
                logger.info { "Login request rejected due to session seed and client seed mismatch." }

                secureBuf.resetReaderIndex()
                secureBuf.skipBytes(payloadLength)

                session.writeAndFlush(LoginStatusResponse(StatusType.COULD_NOT_COMPLETE_LOGIN))
                    .addListener(ChannelFutureListener.CLOSE)
                return
            }

            var authCode: Int = -1
            val password: String?
            val lastXteaKeys = IntArray(4)

            if((session.provider.current as LoginProtocol).isReconnecting()) {
                for(i in 0 until 4) {
                    lastXteaKeys[i] = secureBuf.readInt()
                }

                authCode = -1
                password = null
            } else {
                val type = secureBuf.readByte().toInt()
                when(type) {
                    1 -> {
                        authCode = secureBuf.readUnsignedMedium()
                        secureBuf.skipBytes(Byte.SIZE_BYTES)
                    }

                    2 -> secureBuf.skipBytes(Int.SIZE_BYTES)

                    else -> authCode = secureBuf.readInt()
                }

                secureBuf.skipBytes(Byte.SIZE_BYTES)
                password = secureBuf.readString()
            }

            val xteaBuf = buf.xteaDecipher(xteaKeys)

            val username = xteaBuf.readString()

            /**
             * Client settings definitions
             */
            val clientResizable = (xteaBuf.readByte().toInt() shr 1) == 1
            val clientWidth = xteaBuf.readUnsignedShort()
            val clientHeight = xteaBuf.readUnsignedShort()

            /**
             * Skip the random.dat data.
             */
            xteaBuf.skipBytes(24)

            xteaBuf.readString()
            xteaBuf.skipBytes(Int.SIZE_BYTES)

            /**
             * Skip PlatformInfo data
             * This is just information about the client's machine.
             */
            xteaBuf.skipBytes(Byte.SIZE_BYTES * 3)
            xteaBuf.skipBytes(Short.SIZE_BYTES)
            xteaBuf.skipBytes(Byte.SIZE_BYTES * 5)
            xteaBuf.skipBytes(Short.SIZE_BYTES)
            xteaBuf.skipBytes(Byte.SIZE_BYTES)
            xteaBuf.readUnsignedMedium()
            xteaBuf.skipBytes(Short.SIZE_BYTES)
            xteaBuf.readJagexString()
            xteaBuf.readJagexString()
            xteaBuf.readJagexString()
            xteaBuf.readJagexString()
            xteaBuf.skipBytes(Byte.SIZE_BYTES)
            xteaBuf.skipBytes(Short.SIZE_BYTES)
            xteaBuf.readJagexString()
            xteaBuf.readJagexString()
            xteaBuf.skipBytes(Byte.SIZE_BYTES * 2)
            xteaBuf.skipBytes(Int.SIZE_BYTES * 3)
            xteaBuf.skipBytes(Int.SIZE_BYTES)
            xteaBuf.readJagexString()

            xteaBuf.skipBytes(Int.SIZE_BYTES * 3)

            /**
             * Check the client's current cache CRC checksums with the server's to ensure nothing is either
             * outdated or corrupted.
             */
            val serverCacheCrcs = engine.cachestore.indexes.map { it.crc }.toIntArray()
            val clientCacheCrcs = IntArray(serverCacheCrcs.size) { xteaBuf.readInt() }

            for(i in 0 until serverCacheCrcs.size) {
                if(i == 16) continue
                if(clientCacheCrcs[i] != serverCacheCrcs[i]) {
                    logger.info { "Login request for username $username rejected due to cache CRC mismatch." }

                    buf.resetReaderIndex()
                    buf.skipBytes(payloadLength)

                    session.writeAndFlush(LoginStatusResponse(StatusType.REVISION_MISMATCH))
                        .addListener(ChannelFutureListener.CLOSE)
                    return
                }
            }

            /**
             * Login payload decode succeeded.
             */
            val request = LoginRequestPacket(
                session = session,
                reconnecting = (session.provider.current as LoginProtocol).isReconnecting(),
                username = username,
                password = password ?: "",
                revision = revision,
                authCode = authCode,
                mobile = mobile,
                clientResizable = clientResizable,
                clientWidth = clientWidth,
                clientHeight = clientHeight
            )

            out.add(request)
        }
    }

    /**
     * Builds secure buffer using the RSA encryption keys.
     *
     * @param buf The raw input buffer
     *
     * @return [ByteBuf] the decrypted secure buffer.
     */
    private fun buildSecureBuffer(buf: ByteBuf): ByteBuf {
        val length = buf.readUnsignedShort()
        val secureBuf = buf.readBytes(length)
        val decryptedBytes = BigInteger(secureBuf.arrayTypeSafe()).modPow(rsa.exponent, rsa.modulus)
        return Unpooled.wrappedBuffer(decryptedBytes.toByteArray())
    }

    /**
     * Decrypts a [ByteBuf] with four XTEA keys.
     * Uses the XTEA xor encryption standard.
     *
     * @param keys The xtea decryption keys
     *
     * @return [ByteBuf] The deciphered buffer
     */
    private fun ByteBuf.xteaDecipher(keys: IntArray): ByteBuf {
        val bytes = ByteArray(readableBytes())
        readBytes(bytes)
        return Unpooled.wrappedBuffer(Xtea.decipher(keys, bytes, 0, bytes.size))
    }

    /**
     * Provides a type safe way to wrap [ByteBuf] into a [ByteArray]
     */
    private fun ByteBuf.arrayTypeSafe(): ByteArray {
        if(this.hasArray()) return this.array()

        val bytes = ByteArray(this.readableBytes())
        this.getBytes(this.readerIndex(), bytes)
        return bytes
    }
}