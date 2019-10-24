package dev.eternal.net

import dev.eternal.net.pipeline.ClientChannelBuilder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.netty.channel.ChannelFuture
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import java.net.InetSocketAddress

class NetworkServerTest : KoinTest {

    val addr = InetSocketAddress("0.0.0.0", 12345)
    val server: NetworkServer by inject { parametersOf(addr) }
    val future = mockk<ChannelFuture>()
    val handler: ClientChannelBuilder by inject()

    @Before
    fun before() {
        startKoin {
            modules(module {
                single { (addr: InetSocketAddress) -> NetworkServer(addr) }
                single { ClientChannelBuilder() }
            })
        }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `START and STOP server`() {
        server.start()
        Assert.assertNotNull(server.future)
        Assert.assertTrue(server.isStarting)

        server.future?.addListener { f ->
            if(f.isDone) {
                Assert.assertTrue(server.isRunning)
                server.stop()
                Assert.assertFalse(server.isRunning)
            }
        }
    }
}