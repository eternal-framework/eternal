package dev.eternal.launcher.rsa

import dev.eternal.engine.module.ModuleStore
import dev.eternal.engine.module.impl.RSAModule
import dev.eternal.injector.Injector
import dev.eternal.util.Injectable
import dev.eternal.util.PathConstants
import org.koin.core.inject

/**
 * Generates a RSA key pair from gradle.
 *
 * @author Cody Fullen
 */
object RSAGenerator : Injectable {

    @JvmStatic
    fun main(args: Array<String>) {
        this.startInjector()

        val modules: ModuleStore by inject()
        val rsaModule = modules[RSAModule::class]

        println("Generating RSA public/private key pair...")
        rsaModule.generatePair()
        rsaModule.loadKeys()

        println("")
        println("Below are an output of the generated exponent and modulus.")
        println("You can get the modulus at a later data in the ${PathConstants.RSA_MODULUS_PATH} file.")
        println("")
        println("Modulus: ${rsaModule.modulus.toString(RSAModule.radix)}")
    }

    /**
     * Starts the dependency injector.
     */
    private fun startInjector() {
        val injector = Injector()
        injector.start()
    }

}