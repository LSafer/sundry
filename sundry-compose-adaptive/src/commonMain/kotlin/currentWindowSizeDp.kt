package net.lsafer.sundry.compose.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize

@Composable
expect fun currentWindowDpSize(): DpSize
