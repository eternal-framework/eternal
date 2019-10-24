package dev.eternal.engine.service

import dev.eternal.engine.service.login.LoginService
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import kotlin.reflect.KClass

/**
 * Responsible for storing a set of [Service]s and exposes methods to interact with them.
 *
 * @author Cody Fullen
 */
class ServiceManager {

    private val services = Object2ObjectOpenHashMap<KClass<out Service>, Service>()

    /**
     * Add the services to the store.
     */
    init {
        addService(LoginService::class)
    }

    /**
     * Adds a service kotlin class to the store.
     *
     * @param serviceClass The [Service] kotlin class.
     */
    private fun <T : Service> addService(serviceClass: KClass<T>) {
        val inst = serviceClass.java.getDeclaredConstructor().newInstance()
        services[serviceClass] = inst
    }

    /**
     * Gets a service from the store.
     *
     * @param service The [Service] kotlin class.
     *
     * @return T The [Service] instance casted to its super type.
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <T : Service> get(service: KClass<T>): T {
        if(!services.containsKey(service)) throw NullPointerException("Unable to find service ${service.java.simpleName} in store.")
        return services[service] as T
    }

    /**
     * Executes [predicate] for each [Service] in the store.
     *
     * @param predicate The Unit logic to execute.
     */
    fun forEach(predicate: (Service) -> Unit) {
        services.forEach { (_, value) ->
            predicate(value)
        }
    }

    /**
     * Gets the number services in this store.
     */
    fun count(): Int = services.size
}