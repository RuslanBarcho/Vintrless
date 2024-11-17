package pw.vintr.vintrless.presentation.uikit.input

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import pw.vintr.vintrless.presentation.theme.*
import pw.vintr.vintrless.tools.extensions.Empty
import pw.vintr.vintrless.tools.extensions.cardBackground
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_expand_arrow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AppDropdownField(
    modifier: Modifier,
    label: String = String.Empty,
    items: List<DropdownPayload<T?>>,
    selectedItem: DropdownPayload<T?>,
    onItemSelected: (T?) -> Unit,
    dropdownOffset: DpOffset = DpOffset(0.dp, 8.dp),
    selectedMaxLines: Int = Int.MAX_VALUE,
    dropdownMaxLines: Int = Int.MAX_VALUE,
    dropdownMenuModifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    val rotation = remember { Animatable(initialValue = 0f) }

    fun onExpandedChange(newExpandedValue: Boolean) {
        expanded = newExpandedValue
        coroutineScope.launch {
            rotation.animateTo(if (newExpandedValue) 180f else 0f)
        }
    }

    Column(
        modifier = modifier,
    ) {
        if (label.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = label,
                style = Gilroy12(),
                color = LocalVintrColors.current.textLabel
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        ExposedDropdownMenuBox(
            modifier = Modifier
                .fillMaxWidth(),
            expanded = expanded,
            onExpandedChange = { onExpandedChange(!expanded) },
        ) {
            Row(
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
                    .heightIn(min = 40.dp)
                    .cardShadow(cornersRadius = 8.dp)
                    .cardBackground(cornerRadius = 8.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = selectedItem.title,
                    maxLines = selectedMaxLines,
                    style = RubikRegular16(),
                    color = if (selectedItem.payload != null) {
                        VintrlessExtendedTheme.colors.textRegular
                    } else {
                        VintrlessExtendedTheme.colors.textFieldHint
                    },
                )
                Spacer(Modifier.width(10.dp))
                Icon(
                    modifier = Modifier.rotate(rotation.value),
                    painter = painterResource(Res.drawable.ic_expand_arrow),
                    tint = VintrlessExtendedTheme.colors.textRegular,
                    contentDescription = null,
                )
            }
            DropdownMenu(
                modifier = dropdownMenuModifier
                    .exposedDropdownSize()
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
                onDismissRequest = { onExpandedChange(newExpandedValue = false) }
            ) {
                items.forEach { option ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onItemSelected(option.payload)
                                onExpandedChange(newExpandedValue = false)
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
}

data class DropdownPayload<T>(
    val payload: T,
    val title: String,
)
