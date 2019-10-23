package dev.eternal.injector

import dev.eternal.net.NetworkServer
import dev.eternal.net.pipeline.ClientChannelBuilder
import dev.eternal.net.pipeline.ClientChannelHandler
import org.koin.dsl.module
import java.net.InetSocketAddress

/**
 * @author Cody Fullen
 */

val network_module = module {

    single { (address: InetSocketAddress) -> NetworkServer(address) }

    factory { ClientChannelBuilder() }
    factory { ClientChannelHandler(get()) }

}