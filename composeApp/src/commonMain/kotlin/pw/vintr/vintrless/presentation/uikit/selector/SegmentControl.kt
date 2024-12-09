package pw.vintr.vintrless.presentation.uikit.selector

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pw.vintr.vintrless.presentation.theme.Gilroy12
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme

@Composable
fun SegmentControl(
    items: List<String>,
    modifier: Modifier,
    indicatorPadding: Dp = 3.dp,
    selectedItemIndex: Int = 0,
    onSelectedTab: (index: Int) -> Unit
) {
    var tabWidth by remember { mutableStateOf(0.dp) }

    val indicatorOffset: Dp by animateDpAsState(
        if (selectedItemIndex == 0) {
            tabWidth * (selectedItemIndex / items.size.toFloat())
        } else {
            tabWidth * (selectedItemIndex / items.size.toFloat()) - indicatorPadding
        },
        label = "Indicator offset"
    )

    val density = LocalDensity.current

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                tabWidth = with(density) { coordinates.size.width.toDp() }
            }
            .clip(RoundedCornerShape(12.dp))
            .background(VintrlessExtendedTheme.colors.segmentBackground, RoundedCornerShape(12.dp))
            .border(1.dp, VintrlessExtendedTheme.colors.segmentStroke, RoundedCornerShape(12.dp))
    ) {

        SegmentIndicator(
            modifier = Modifier
                .padding(indicatorPadding)
                .fillMaxHeight()
                .width(width = tabWidth / items.size - indicatorPadding),
            indicatorOffset = indicatorOffset
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEachIndexed { index, title ->
                SegmentItem(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(tabWidth / items.size),
                    onClick = {
                        onSelectedTab(index)
                    },
                    title = title,
                )
            }
        }
    }
}

@Composable
private fun SegmentIndicator(
    modifier: Modifier,
    indicatorOffset: Dp,
) {
    Box(
        modifier = modifier
            .offset(x = indicatorOffset)
            .clip(RoundedCornerShape(10.dp))
            .background(VintrlessExtendedTheme.colors.segmentIndicatorBackground)
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(10.dp),
                color = VintrlessExtendedTheme.colors.segmentIndicatorStroke
            )
    )
}

@Composable
private fun SegmentItem(
    modifier: Modifier,
    onClick: () -> Unit,
    title: String,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = Gilroy12(),
            color = VintrlessExtendedTheme.colors.textRegular
        )
    }
}
