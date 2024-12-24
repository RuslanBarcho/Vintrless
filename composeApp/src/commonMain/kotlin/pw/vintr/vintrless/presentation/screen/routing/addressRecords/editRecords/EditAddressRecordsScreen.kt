package pw.vintr.vintrless.presentation.screen.routing.addressRecords.editRecords

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pw.vintr.vintrless.LazyColumnScrollbar
import pw.vintr.vintrless.domain.routing.model.RuleAddressRecordType
import pw.vintr.vintrless.domain.routing.model.Ruleset
import pw.vintr.vintrless.domain.routing.model.Ruleset.Exclude.Type.BLACKLIST
import pw.vintr.vintrless.domain.routing.model.Ruleset.Exclude.Type.WHITELIST
import pw.vintr.vintrless.platform.model.PlatformType
import pw.vintr.vintrless.platformType
import pw.vintr.vintrless.presentation.base.BaseScreenState
import pw.vintr.vintrless.presentation.theme.Gilroy18
import pw.vintr.vintrless.presentation.theme.RubikMedium16
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.uikit.button.ButtonRegular
import pw.vintr.vintrless.presentation.uikit.button.ButtonSecondary
import pw.vintr.vintrless.presentation.uikit.button.ButtonSecondarySize
import pw.vintr.vintrless.presentation.uikit.container.RestrictedWidthLayout
import pw.vintr.vintrless.presentation.uikit.layout.ScreenStateLayout
import pw.vintr.vintrless.presentation.uikit.selector.SegmentControl
import pw.vintr.vintrless.presentation.uikit.separator.LineSeparator
import pw.vintr.vintrless.presentation.uikit.toolbar.ToolbarRegular
import pw.vintr.vintrless.tools.extensions.Empty
import vintrless.composeapp.generated.resources.*
import vintrless.composeapp.generated.resources.Res

@Composable
fun EditAddressRecordsScreen(
    rulesetId: String,
    viewModel: EditAddressRecordsViewModel = koinViewModel { parametersOf(rulesetId) }
) {
    val screenState = viewModel.screenState.collectAsState()

    Scaffold(
        topBar = {
            ToolbarRegular(
                title = when (val state = screenState.value) {
                    is BaseScreenState.Loaded -> {
                        when (state.payload.ruleset) {
                            is Ruleset.Exclude -> {
                                when (state.payload.ruleset.type) {
                                    BLACKLIST -> {
                                        stringResource(Res.string.ruleset_blacklist)
                                    }
                                    WHITELIST -> {
                                        stringResource(Res.string.ruleset_whitelist)
                                    }
                                }
                            }
                            is Ruleset.Custom -> state.payload.ruleset.description
                            else -> String.Empty
                        }
                    }
                    else -> String.Empty
                },
                onBackPressed = { viewModel.navigateBack() },
                trailing = {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .clickable { viewModel.openAddAddressRecords() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(24.dp),
                            painter = painterResource(Res.drawable.ic_add),
                            contentDescription = "Add new profile button icon",
                            tint = VintrlessExtendedTheme.colors.textRegular
                        )
                    }
                }
            )
        },
    ) { scaffoldPadding ->
        RestrictedWidthLayout(
            restrictionWidth = 800.dp
        ) {
            ScreenStateLayout(
                modifier = Modifier
                    .padding(scaffoldPadding)
                    .fillMaxSize(),
                state = screenState.value
            ) { state ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // Address item type select controller
                    val segmentControlItems = listOf(
                        RuleAddressRecordType.IP to stringResource(Res.string.address_record_type_ip),
                        RuleAddressRecordType.DOMAIN to stringResource(Res.string.address_record_type_domain)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    SegmentControl(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 28.dp)
                            .height(34.dp),
                        items = segmentControlItems.map { it.second },
                        selectedItemIndex = segmentControlItems.indexOfFirst {
                            it.first == state.payload.selectedAddressRecordType
                        },
                        onSelectedTab = {
                            viewModel.selectAddressRecordType(segmentControlItems[it].first)
                        }
                    )

                    // Content
                    AddressRecordList(
                        modifier = Modifier
                            .weight(1f),
                        records = when (state.payload.selectedAddressRecordType) {
                            RuleAddressRecordType.IP -> state.payload.ips
                            RuleAddressRecordType.DOMAIN -> state.payload.domains
                        },
                        onDeleteClick = {
                            viewModel.removeAddressRecord(
                                itemType = state.payload.selectedAddressRecordType,
                                itemIndex = it
                            )
                        }
                    )

                    // Save button
                    ButtonRegular(
                        modifier = Modifier
                            .padding(start = 28.dp, end = 28.dp, top = 12.dp, bottom = 20.dp),
                        text = stringResource(Res.string.common_save),
                        enabled = state.payload.hasChanges,
                    ) {
                        viewModel.sendEditResult()
                    }
                }
            }
        }
    }
}

@Composable
private fun AddressRecordList(
    modifier: Modifier = Modifier,
    records: List<String>,
    onDeleteClick: (Int) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        if (records.isNotEmpty()) {
            // Ruleset address items
            val listState = rememberLazyListState()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    vertical = 20.dp,
                    horizontal = 28.dp,
                ),
                state = listState,
            ) {
                itemsIndexed(records) { index, item ->
                    AddressRecordItem(
                        value = item,
                        onDeleteClick = {
                            onDeleteClick(index)
                        }
                    )
                    if (index != records.lastIndex) {
                        LineSeparator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            // Scrollbar for desktop
            if (platformType() == PlatformType.JVM) {
                LazyColumnScrollbar(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight(),
                    listState = listState
                )
            }
        } else {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(Res.drawable.illustration_no_addresses),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(Res.string.address_records_empty),
                    color = VintrlessExtendedTheme.colors.textRegular,
                    style = RubikMedium16(),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun AddressRecordItem(
    modifier: Modifier = Modifier,
    value: String,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .weight(1f),
            text = value,
            color = VintrlessExtendedTheme.colors.textRegular,
            style = Gilroy18(),
        )
        Spacer(modifier = Modifier.width(10.dp))
        ButtonSecondary(
            wrapContentWidth = true,
            size = ButtonSecondarySize.MEDIUM,
            content = {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    painter = painterResource(Res.drawable.ic_delete),
                    tint = VintrlessExtendedTheme.colors.secondaryButtonContent,
                    contentDescription = null
                )
            }
        ) { onDeleteClick() }
    }
}
