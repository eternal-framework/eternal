package dev.eternal.engine.module.impl

import dev.eternal.engine.module.Module
import dev.eternal.util.PathConstants
import dev.eternal.util.Server.logger
import org.bouncycastle.asn1.pkcs.RSAPrivateKey
import org.bouncycastle.asn1.pkcs.RSAPublicKey
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemReader
import org.bouncycastle.util.io.pem.PemWriter
import java.io.FileNotFoundException
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.Security
import java.security.spec.PKCS8EncodedKeySpec

/**
 * Responsible for loading, creating, and saving the RSA Encryption key used
 * for encrypting packet communication between the server and client.
 *
 * @author Cody Fullen
 */
class RSAModule : Module {

    /**
     * The exponent of the RSA private key
     */
    lateinit var exponent: BigInteger

    /**
     * The modulus of the RSA private key
     */
    lateinit var modulus: BigInteger

    private val privateKeyPath = Paths.get(PathConstants.RSA_PRIVATE_KEY_PATH)
    private val modulusFilePath = Paths.get(PathConstants.RSA_MODULUS_PATH)

    /**
     * Load the RSA private key and extract the exponent and modulus.
     */
    override fun init() {
        this.loadKeys()
    }

    /**
     * Loads the exponent and modulus from the private key file.
     */
    fun loadKeys() {
        if(!Files.exists(privateKeyPath)) {
            throw FileNotFoundException("Unable to find RSA private key file.")
        }

        logger.info { "Loading RSA private keys..." }

        try {

            PemReader(Files.newBufferedReader(privateKeyPath)).use { reader ->
                val pem = reader.readPemObject()
                val keySpec = PKCS8EncodedKeySpec(pem.content)

                Security.addProvider(BouncyCastleProvider())

                val factory = KeyFactory.getInstance("RSA", "BC")
                val privateKey = factory.generatePrivate(keySpec) as RSAPrivateKey

                this.exponent = privateKey.privateExponent
                this.modulus = privateKey.modulus
            }

        } catch(e : Exception) {
            logger.error(e) { "Unable to load RSA private key from data folder." }
        }
    }

    /**
     * Generates a new RSA public / private key pair.
     * Saves them to the data folders.
     *
     * Also saves the modulus text as "modulus.txt" for easy later reference.
     */
    fun generatePair() {
        logger.info { "Generating new RSA public/private key pair..." }

        Security.addProvider(BouncyCastleProvider())

        val generator = KeyPairGenerator.getInstance("RSA", "BC")
        generator.initialize(bits)

        val keyPair = generator.generateKeyPair()

        val privateKey = keyPair.private as RSAPrivateKey
        val publicKey = keyPair.public as RSAPublicKey

        logger.info { "Finished generating RSA key pair. You can find the modulus in ${PathConstants.RSA_MODULUS_PATH}." }

        /**
         * Save the files
         */
        try {

            PemWriter(Files.newBufferedWriter(privateKeyPath)).use { writer ->
                writer.writeObject(PemObject("RSA PRIVATE KEY", privateKey.encoded))
            }

            Files.newBufferedWriter(modulusFilePath).use { writer ->
                writer.write(publicKey.modulus.toString(radix))
            }

        } catch(e : Exception) {
            logger.error(e) { "Failed to write files to data folder." }
        }
    }

    companion object {
        /**
         * SETTINGS. NOTE* These must match those on the OSRS client.
         */

        /**
         * The radix int size.
         * This is used to encode the BigInteger into a string for easy distribution.
         */
        const val radix: Int = 16

        /**
         * The total bits the key is.
         * The OSRS client uses 2048 bits which is industry standard. I suggest either using 2048 or 4096.
         * 1024 and below has been cracked by super computers in recent years. 2048 should be secure for the next 10+ years.
         */
        const val bits: Int = 2048
    }
}