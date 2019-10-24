package dev.eternal.engine.module

import dev.eternal.engine.module.impl.RSAModule
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import kotlin.reflect.KClass

/**
 * Stores a set of [Module]s
 *
 * @author Cody Fullen
 */
class ModuleStore {

    private val modules = Object2ObjectOpenHashMap<KClass<out Module>, Module>()

    /**
     * Add the modules to the store.
     */
    init {
        addModule(RSAModule::class)
    }

    /**
     * Adds a module to the store
     *
     * @param moduleClass The [Module] kotlin class.
     */
    private fun addModule(moduleClass: KClass<out Module>) {
        val inst = moduleClass.java.getDeclaredConstructor().newInstance()
        modules[moduleClass] = inst
    }

    /**
     * Retrieves a [Module] from the store.
     *
     * @param module The [Module] kotlin class.
     *
     * @return T The [Module] instance casted to its super type.
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <T : Module> get(module: KClass<T>): T {
        if(!modules.containsKey(module)) throw NullPointerException("Unable to find ${module.java.simpleName} in module store.")
        return modules[module] as T
    }

    /**
     * Executes a predicate for each module in the store.
     *
     * @param predicate The Unit logic to execute.
     */
    fun forEach(predicate: (Module) -> Unit) {
        modules.forEach { (_, module) ->
            predicate(module)
        }
    }

    /**
     * Get the number of modules in the store.
     */
    fun count(): Int = modules.size
}