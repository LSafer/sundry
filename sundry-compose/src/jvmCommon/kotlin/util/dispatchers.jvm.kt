package net.lsafer.sundry.compose.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val platformIODispatcher: CoroutineDispatcher
    get() = Dispatchers.IO
