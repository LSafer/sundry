package net.lsafer.sundry.compose.adaptive

import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.toSize

@Composable
actual fun currentWindowDpSize(): DpSize {
    val density = LocalDensity.current
    val windowIntSize = currentWindowSize()
    return with(density) { windowIntSize.toSize().toDpSize() }
}
