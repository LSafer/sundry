package net.lsafer.sundry.compose

import androidx.compose.ui.unit.dp

data object Height {
    /*
        <S     -> 99.78% of phones in landscape

        S..<L -> 96.56% of tablets in landscape,
                   97.59% of phones in portrait

        >L     -> 94.25% of tablets in portrait
    */
    val XXS = 240.dp
    val XS = 450.dp
    val S = 480.dp
    val L = 900.dp
    val XL = 960.dp
    val XXL = 1800.dp
}

data object Width {
    /*
        <S      -> 99.96% of phones in portrait

        S..<L   -> 93.73% of tablets in portrait,
                   most large unfolded inner displays in portrait

        >L      -> 97.22% of tablets in landscape
                   most large unfolded inner displays in landscape
    */
    val XXS = 300.dp
    val XS = 420.dp
    val S = 600.dp
    val L = 840.dp
    val XL = 1200.dp
    val XXL = 1680.dp
}
