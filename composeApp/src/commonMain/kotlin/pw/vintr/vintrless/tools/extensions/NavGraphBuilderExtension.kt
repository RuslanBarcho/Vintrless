package pw.vintr.vintrless.tools.extensions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.*
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.compose.DialogNavigatorDestinationBuilder
import kotlin.reflect.KType

inline fun <reified T : Any> NavGraphBuilder.extendedDialog(
    typeMap: Map<KType, NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    dialogProperties: DialogProperties = DialogProperties(),
    noinline content: @Composable (NavBackStackEntry) -> Unit
) {
    destination(
        DialogNavigatorDestinationBuilder(
            provider[DialogNavigator::class],
            T::class,
            typeMap,
            dialogProperties,
        ) {
            BoxWithConstraints(
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidthRestricted(maxWidth = 400.dp, decreaseSize = 56.dp)
                ) {
                    content(it)
                }
            }
        }.apply { deepLinks.forEach { deepLink -> deepLink(deepLink) } }
    )
}
