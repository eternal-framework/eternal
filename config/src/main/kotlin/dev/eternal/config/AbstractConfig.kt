package dev.eternal.config

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.Item
import com.uchuhimo.konf.source.json.toJson
import com.uchuhimo.konf.source.properties.toProperties
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.source.yaml.toYaml
import dev.eternal.util.Injectable
import dev.eternal.util.Server.logger
import java.io.File

/**
 * Represents a Konf config element which contains helper
 * DSL builder methods for creating default configs.
 *
 * @author Cody Fullen
 */
abstract class AbstractConfig<T>(private val path: String, private val spec: ConfigSpec) : Injectable {

    private var config = Config { addSpec(spec) }

    /**
     * The file format of the file
     */
    abstract val fileFormat: FileFormat

    /**
     * An operator alias for fetching config spec items.
     *
     * @param item The config [Item] to get.
     *
     * @return T The [ConfigSpec] item value.
     */
    operator fun <T> get(item: Item<T>): T = config[item]

    /**
     * Loads an object to a context.
     * Used to map the config items to object params.
     *
     * @param ctx The generic context object.
     *
     * @return T Generic modified instance.
     */
    abstract fun load(ctx: T): T

    /**
     * Saves object vars from a context.
     * Used to save vars from an object to config items.
     *
     * @param ctx The object context.
     */
    abstract fun save(ctx: T)

    /**
     * Loads the data from the file located a [path].
     * If the file does not exist, The config attempts to create one from default
     * optional params.
     */
    fun loadFile(): Config {
        val file = File(path)

        if(!file.exists()) {
            logger.info { "Default config file $path does not exist. Saving default config." }
            this.saveFile()
        }

        config = when(fileFormat) {
            FileFormat.PROPERTIES -> Config { addSpec(spec) }.from.properties.file(file)
            FileFormat.JSON -> Config { addSpec(spec) }.from.json.file(file)
            FileFormat.YAML -> Config { addSpec(spec) }.from.yaml.file(file)
        }

        return config
    }

    /**
     * Saves the current config items to the file at [path]
     */
    private fun saveFile() {
        val file = File(path)

        when(fileFormat) {
            FileFormat.PROPERTIES -> config.toProperties.toFile(file)
            FileFormat.JSON -> config.toJson.toFile(file)
            FileFormat.YAML -> config.toYaml.toFile(file)
        }
    }
}