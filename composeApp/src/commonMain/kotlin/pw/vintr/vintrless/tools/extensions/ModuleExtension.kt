package pw.vintr.vintrless.tools.extensions

import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.onClose
import pw.vintr.vintrless.domain.base.BaseInteractor

inline fun <reified T : BaseInteractor> Module.interactor(
    qualifier: Qualifier? = null,
    createdAtStart: Boolean = false,
    noinline definition: Definition<T>,
): KoinDefinition<T> {
    return single(qualifier, createdAtStart, definition) onClose { it?.close() }
}
