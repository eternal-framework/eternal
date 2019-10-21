package dev.eternal.injector

import dev.eternal.engine.Engine
import org.koin.dsl.module

/**
 * @author Cody Fullen
 */
 
val engine_module = module {
    // Engine Singleton
    single { Engine() }
}