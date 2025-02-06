package pw.vintr.vintrless.presentation.uikit.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.tools.extensions.Empty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AppDropdownEditableField(
    modifier: Modifier,
    value: String = String.Empty,
    label: String = String.Empty,
    hint: String = String.Empty,
    singleLine: Boolean = true,
    items: List<DropdownPayload<T?>>,
    onValueChange: (String) -> Unit = {},
    onItemSelected: (T?) -> Unit,
    dropdownOffset: DpOffset = DpOffset(0.dp, 8.dp),
    dropdownMaxLines: Int = Int.MAX_VALUE,
    dropdownMenuModifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    var interactionExpanded by remember { mutableStateOf(false) }
    val expanded = interactionExpanded && items.isNotEmpty()

    ExposedDropdownMenuBox(
        modifier = modifier
            .fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = { interactionExpanded = it },
    ) {
        AppTextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth(),
            textFieldModifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    interactionExpanded = it.isFocused
                },
            value = value,
            label = label,
            hint = hint,
            singleLine = singleLine,
            onValueChange = onValueChange,
        )
        DropdownMenu(
            modifier = dropdownMenuModifier
                .exposedDropdownSize()
                .heightIn(max = 300.dp)
                .background(VintrlessExtendedTheme.colors.cardBackgroundColor)
                .border(
                    width = 1.dp,
                    color = VintrlessExtendedTheme.colors.cardStrokeColor,
                    shape = RoundedCornerShape(8.dp)
                ),
            offset = dropdownOffset,
            expanded = expanded,
            containerColor = Color.Transparent,
            shape = RoundedCornerShape(8.dp),
            properties = PopupProperties(
                focusable = false,
            ),
            onDismissRequest = { interactionExpanded = false }
        ) {
            items.forEach { option ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onItemSelected(option.payload)
                            interactionExpanded = false
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp, vertical = 12.dp),
                        text = option.title,
                        maxLines = dropdownMaxLines,
                        color = VintrlessExtendedTheme.colors.textRegular,
                    )
                }
            }
        }
    }
}
