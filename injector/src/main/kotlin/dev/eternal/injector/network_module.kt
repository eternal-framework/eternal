package dev.eternal.injector

import dev.eternal.net.NetworkServer
import dev.eternal.net.pipeline.ClientChannelBuilder
import org.koin.dsl.module
import java.net.InetSocketAddress

/**
 * @author Cody Fullen
 */

val network_module = module {

    single { (address: InetSocketAddress) -> NetworkServer(address) }

    factory { ClientChannelBuilder() }

}