package pw.vintr.vintrless.presentation.screen.logViewer

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pw.vintr.vintrless.domain.log.model.LogType
import pw.vintr.vintrless.presentation.theme.AppColor.RadicalRed
import pw.vintr.vintrless.presentation.theme.AppColor.Zest
import pw.vintr.vintrless.presentation.theme.JBMono12
import pw.vintr.vintrless.presentation.theme.VintrlessExtendedTheme
import pw.vintr.vintrless.presentation.uikit.toolbar.ToolbarRegular
import pw.vintr.vintrless.tools.extensions.isScrolledToEnd
import vintrless.composeapp.generated.resources.Res
import vintrless.composeapp.generated.resources.logs_title

@Composable
fun LogViewerScreen(
    viewModel: LogViewerViewModel = koinViewModel(),
) {
    val screenState = viewModel.screenState.collectAsState()

    val logListState = rememberLazyListState()

    val isListScrolling by remember {
        derivedStateOf { logListState.isScrollInProgress }
    }
    var isUserScrolling by remember {
        mutableStateOf(false)
    }
    var isUserEverScrolled by remember {
        mutableStateOf(false)
    }
    var isEndOfListReached by remember {
        mutableStateOf(true)
    }

    // Initially scroll to latest logs
    LaunchedEffect(screenState.value.logs.isNotEmpty()) {
        logListState.scrollToItem(logListState.layoutInfo.totalItemsCount)
    }

    // Listen to behaviour changes and apply to variable
    LaunchedEffect(
        key1 = isUserScrolling,
        key2 = isListScrolling
    ) {
        if (!isUserScrolling && !isListScrolling && isUserEverScrolled) {
            isEndOfListReached = logListState.isScrolledToEnd()
        }
    }

    // Scroll to end when new logs appears
    LaunchedEffect(screenState.value.logs.size) {
        if (!isUserScrolling && isEndOfListReached) {
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
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                awaitFirstDown()
                                var event = awaitPointerEvent()
                                while (event.type == PointerEventType.Move) {
                                    isUserEverScrolled = true
                                    isUserScrolling = true

                                    event = awaitPointerEvent()
                                }
                                isUserScrolling = false
                            }
                        },
                    state = logListState,
                    contentPadding = PaddingValues(20.dp)
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
        }
    }
}
