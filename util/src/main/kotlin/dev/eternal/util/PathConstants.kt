package dev.eternal.util

/**
 * A static list of [String] paths for files
 *
 * @author Cody Fullen
 */
object PathConstants {

    /**
     * The location of the main server config.
     */
    const val SERVER_CONFIG_PATH = "data/config/server.yml"

    /**
     * The location of the cache data directory.
     */
    const val CACHE_FOLDER_PATH = "data/cache/"

    /**
     * The location of the XTEA region decryption keys file.
     */
    const val XTEAS_FILE_PATH = "data/xteas/xteas.json"

    /**
     * The location of the RSA private key file.
     */
    const val RSA_PRIVATE_KEY_PATH = "data/rsa/private.key"

    /**
     * The location of the RSA modulus.tx file
     */
    const val RSA_MODULUS_PATH = "data/rsa/modulus.txt"
}