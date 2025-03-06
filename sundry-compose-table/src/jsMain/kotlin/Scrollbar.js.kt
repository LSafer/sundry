package net.lsafer.sundry.compose.table

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

internal actual fun isHorizontalScrollbarSupported(): Boolean {
    return true
}

@Composable
internal actual fun HorizontalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier,
    reverseLayout: Boolean,
) {
    HorizontalScrollbar(
        adapter = rememberScrollbarAdapter(scrollState),
        modifier = modifier,
        reverseLayout = reverseLayout,
    )
}
