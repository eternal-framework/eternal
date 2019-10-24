package dev.eternal.injector

import dev.eternal.engine.Engine
import dev.eternal.engine.module.ModuleStore
import dev.eternal.engine.service.ServiceManager
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * @author Cody Fullen
 */
 
val engine_module = module {
    // Engine Singleton
    single { Engine() } bind dev.eternal.api.Engine::class
    single { ServiceManager() }
    single { ModuleStore() }
}