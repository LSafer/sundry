package net.lsafer.sundry.compose.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.toSize

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun currentWindowDpSize(): DpSize {
    val density = LocalDensity.current
    val windowIntSize = LocalWindowInfo.current.containerSize
    return with(density) { windowIntSize.toSize().toDpSize() }
}
