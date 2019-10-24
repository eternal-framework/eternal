package dev.eternal.net

import dev.eternal.net.pipeline.ClientChannelBuilder
import io.mockk.mockk
import io.netty.channel.ChannelFuture
import org.junit.After
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import java.net.InetSocketAddress

class NetworkServerTest : KoinTest {

    private val addr = InetSocketAddress("0.0.0.0", 12345)

    val server: NetworkServer by inject { parametersOf(addr) }

    val future = mockk<ChannelFuture>()

    @Before
    fun before() {
        startKoin { modules(module {
            single { (addr: InetSocketAddress) -> NetworkServer(addr)  }
            single { ClientChannelBuilder() }
        })}
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun isRunning() {
        assertFalse(server.isRunning)
    }

    @Test
    fun isStarting() {
        assertFalse(server.isStarting)
    }

    @Test
    fun getFuture() {
        assertNull(server.future)
    }

    @Test
    fun setFuture() {
        server.future = future
        assertEquals(server.future, future)
    }

    @Test
    fun start() {
        server.start()
        assertTrue(server.isStarting)
    }

    @Test(expected = IllegalStateException::class)
    fun stop() {
        server.stop()
    }

    @Test
    fun `onBindSuccess$net`() {
        server.onBindSuccess()
    }

    @Test
    fun `onBindFailure$net`() {
        val cause = Exception("This is a test throwable")
        server.onBindFailure(cause)
    }

    @Test
    fun `newSession$net`() {
    }

    @Test
    fun `terminateSession$net`() {
    }

    @Test
    fun getAddress() {
    }
}