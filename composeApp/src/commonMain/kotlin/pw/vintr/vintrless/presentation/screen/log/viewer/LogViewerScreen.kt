package pw.vintr.vintrless.presentation.screen.log.viewer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pw.vintr.vintrless.domain.log.model.LogType
import pw.vintr.vintrless.presentation.theme.AppColor.RadicalRed
import pw.vintr.vintrless.presentation.theme.AppColor.Zest
import pw.vintr.vintrless.presentation.theme.JBMono12
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.uikit.toolbar.ToolbarRegular
import pw.vintr.vintrless.tools.compose.LazyListInteractionState
import pw.vintr.vintrless.tools.compose.lazyListInteraction
import pw.vintr.vintrless.tools.extensions.cardBackground
import pw.vintr.vintrless.tools.extensions.isScrolledToEnd
import vintrless.composeapp.generated.resources.*
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.ic_delete
import vintrless.composeapp.generated.resources.ic_share
import vintrless.composeapp.generated.resources.logs_title

sealed class LogViewerAction {

    companion object {
        val availableActions = listOf(
            Filter,
            Share,
            Clear
        )
    }

    abstract val icon: DrawableResource

    data object Filter : LogViewerAction() {
        override val icon: DrawableResource = Res.drawable.ic_filter
    }

    data object Share : LogViewerAction() {
        override val icon: DrawableResource = Res.drawable.ic_share
    }

    data object Clear : LogViewerAction() {
        override val icon: DrawableResource = Res.drawable.ic_delete
    }
}

@Composable
fun LogViewerScreen(
    viewModel: LogViewerViewModel = koinViewModel(),
) {
    val screenState = viewModel.screenState.collectAsState()

    val logListState = rememberLazyListState()

    var listInteraction by remember {
        mutableStateOf<LazyListInteractionState?>(value = null)
    }
    var autoScrollEnable by remember {
        mutableStateOf(true)
    }

    // Initially scroll to latest logs
    LaunchedEffect(screenState.value.logs.isNotEmpty()) {
        logListState.scrollToItem(logListState.layoutInfo.totalItemsCount)
    }

    // Listen to behaviour changes and apply to variable
    LaunchedEffect(key1 = listInteraction) {
        autoScrollEnable = when (listInteraction) {
            LazyListInteractionState.IDLE -> {
                logListState.isScrolledToEnd()
            }
            LazyListInteractionState.INTERACTING -> {
                false
            }
            null -> {
                true
            }
        }
    }

    // Scroll to end when new logs appears
    LaunchedEffect(screenState.value.logs.size) {
        if (autoScrollEnable) {
            logListState.animateScrollToItem(logListState.layoutInfo.totalItemsCount)
        }
    }

    Scaffold(
        topBar = {
            ToolbarRegular(
                title = stringResource(Res.string.logs_title),
                onBackPressed = { viewModel.navigateBack() },
            )
        },
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {
            SelectionContainer {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .lazyListInteraction(logListState) {
                            listInteraction = it
                        },
                    state = logListState,
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 20.dp,
                        bottom = 80.dp
                    )
                ) {
                    items(
                        screenState.value.logs,
                    ) { log ->
                        Text(
                            text = log.payload,
                            style = JBMono12(),
                            color = when (log.type) {
                                LogType.INFORMATION -> VintrlessExtendedTheme.colors.textRegular
                                LogType.WARNING -> Zest
                                LogType.ERROR -> RadicalRed
                            }
                        )
                    }
                }
            }
            LogViewerMenu(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) { action ->
                when (action) {
                    LogViewerAction.Filter -> {
                        viewModel.openFilter()
                    }
                    LogViewerAction.Share -> {
                        viewModel.performShare()
                    }
                    LogViewerAction.Clear -> {
                        viewModel.performClear()
                    }
                }
            }
        }
    }
}

@Composable
private fun LogViewerMenu(
    modifier: Modifier = Modifier,
    actions: List<LogViewerAction> = LogViewerAction.availableActions,
    onActionTap: (LogViewerAction) -> Unit,
) {
    Row(
        modifier = modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
            .widthIn(max = 240.dp)
            .height(48.dp)
            .cardBackground()
    ) {
        actions.forEach { action ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onActionTap(action) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    painter = painterResource(action.icon),
                    contentDescription = null
                )
            }
        }
    }
}
