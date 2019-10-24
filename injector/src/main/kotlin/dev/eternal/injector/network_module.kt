package dev.eternal.injector

import dev.eternal.net.NetworkServer
import dev.eternal.net.pipeline.ClientChannelBuilder
import dev.eternal.net.pipeline.ClientChannelDecoder
import dev.eternal.net.pipeline.ClientChannelEncoder
import dev.eternal.net.pipeline.ClientChannelHandler
import dev.eternal.net.protocol.ProtocolProvider
import dev.eternal.net.session.Session
import org.koin.dsl.module
import java.net.InetSocketAddress

/**
 * @author Cody Fullen
 */

val network_module = module {

    single { (address: InetSocketAddress) -> NetworkServer(address) }

    factory { ClientChannelBuilder() }
    factory { ClientChannelHandler(get()) }

    factory { (session: Session) -> ClientChannelEncoder(session) }
    factory { (session: Session) -> ClientChannelDecoder(session) }

    factory { (session: Session) -> ProtocolProvider(session) }

}