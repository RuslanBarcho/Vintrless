package pw.vintr.vintrless.presentation.uikit.lazy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pw.vintr.vintrless.tools.extensions.Empty

fun <T> LazyListScope.gridItems(
    columnsCount: Int,
    horizontalSpacer: Dp = 0.dp,
    items: List<T>,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable RowScope.(item: T) -> Unit,
) {
    val itemsCount = items.size
    val rowsCount = (itemsCount + 1) / columnsCount

    items(
        count = rowsCount,
        key = key?.let {
            { index ->
                // Row key is a composition of row's items uuids
                var rowKey = String.Empty

                for (j in 0 until columnsCount) {
                    val itemIndex = index * columnsCount + j
                    if (itemIndex < itemsCount) {
                        rowKey += key(items[itemIndex]).toString()
                    }
                }

                rowKey
            }
        }
    ) { index ->
        Row(Modifier.height(IntrinsicSize.Max)) {
            for (j in 0 until columnsCount) {
                val itemIndex = index * columnsCount + j

                if (itemIndex < itemsCount) {
                    val item = items[itemIndex]

                    itemContent(item)
                    if (j < (columnsCount - 1)) {
                        Spacer(Modifier.width(horizontalSpacer))
                    }
                } else {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
