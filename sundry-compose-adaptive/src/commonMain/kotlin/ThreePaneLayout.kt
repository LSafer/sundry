package net.lsafer.sundry.compose.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.*
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SupportingPaneScaffoldCompat(
    pane: ThreePaneScaffoldRole,
    content: Any? = null,
    scaffoldDirective: PaneScaffoldDirective =
        calculatePaneScaffoldDirectiveCompat(),
    adaptStrategies: ThreePaneScaffoldAdaptStrategies =
        SupportingPaneScaffoldDefaults.adaptStrategies(),
    mainPane: @Composable ThreePaneScaffoldPaneScope.() -> Unit,
    supportingPane: @Composable ThreePaneScaffoldPaneScope.() -> Unit,
    extraPane: (@Composable ThreePaneScaffoldPaneScope.() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val navigator = rememberSupportingPaneScaffoldNavigator<Any>(
        scaffoldDirective = scaffoldDirective,
        adaptStrategies = adaptStrategies,
        isDestinationHistoryAware = false,
    )

    LaunchedEffect(pane, content) {
        navigator.navigateTo(pane, content)
    }

    SupportingPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        mainPane = {
            Box(contentAlignment = Alignment.TopCenter) {
                mainPane()
            }
        },
        supportingPane = {
            Box(contentAlignment = Alignment.TopCenter) {
                supportingPane()
            }
        },
        modifier = modifier,
        extraPane = extraPane?.let {
            { Box(contentAlignment = Alignment.TopCenter) { it() } }
        },
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ListDetailPaneScaffoldCompat(
    pane: ThreePaneScaffoldRole,
    content: Any? = null,
    scaffoldDirective: PaneScaffoldDirective =
        calculatePaneScaffoldDirectiveCompat(),
    adaptStrategies: ThreePaneScaffoldAdaptStrategies =
        ListDetailPaneScaffoldDefaults.adaptStrategies(),
    listPane: @Composable ThreePaneScaffoldPaneScope.() -> Unit,
    detailPane: @Composable ThreePaneScaffoldPaneScope.() -> Unit,
    extraPane: (@Composable ThreePaneScaffoldPaneScope.() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<Any>(
        scaffoldDirective = scaffoldDirective,
        adaptStrategies = adaptStrategies,
        isDestinationHistoryAware = false,
    )

    LaunchedEffect(pane, content) {
        navigator.navigateTo(pane, content)
    }

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            Box(contentAlignment = Alignment.TopCenter) {
                listPane()
            }
        },
        detailPane = {
            Box(contentAlignment = Alignment.TopCenter) {
                detailPane()
            }
        },
        modifier = modifier,
        extraPane = extraPane?.let {
            { Box(contentAlignment = Alignment.TopCenter) { it() } }
        },
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun calculatePaneScaffoldDirectiveCompat(): PaneScaffoldDirective {
    val default = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo())
    val windowDpSize = currentWindowDpSize()
    if (windowDpSize.width > /*Width.XXL*/ 1680.dp)
        return default.copy(maxHorizontalPartitions = 3)
    return default
}
