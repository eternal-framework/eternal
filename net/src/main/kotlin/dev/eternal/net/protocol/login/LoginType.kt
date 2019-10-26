package dev.eternal.net.protocol.login

/**
 * @author Cody Fullen
 */
enum class LoginType(val opcode: Int) {

    NORMAL(16),
    RECONNECT(18);

    companion object {
        val values = enumValues<LoginType>()
    }
}