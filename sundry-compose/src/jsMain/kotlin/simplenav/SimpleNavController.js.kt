package net.lsafer.sundry.compose.simplenav

import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import net.lsafer.sundry.compose.internal.decodeBase64UrlSafeToStringOrNull
import net.lsafer.sundry.compose.internal.deserializeJsonOrNull
import net.lsafer.sundry.compose.internal.encodeBase64UrlSafe
import net.lsafer.sundry.compose.internal.serializeToJsonString
import org.w3c.dom.HashChangeEvent

private var gIsInstalled = false
private var gInstalledScope: CoroutineScope? = null
private var gOriginalListener: ((HashChangeEvent) -> dynamic)? = null

fun <T : Any> WindowSimpleNavController<T>.install() {
    check(!gIsInstalled) { "A navigation controller was already installed" }

    val installedScope = CoroutineScope(Dispatchers.Default)

    gIsInstalled = true
    gInstalledScope = installedScope
    gOriginalListener = window.onhashchange
    isInstalled = true

    // initial [window.location.hash] => [navController]
    val initialState = window.location.hash
        .decodeBase64UrlSafeToStringOrNull()
        ?.deserializeJsonOrNull(stateSerializer)
    if (initialState != null)
        state.value = initialState
    // collect [navController] => [window.location.hash]
    installedScope.launch {
        state.collect {
            window.location.hash = it
                .serializeToJsonString(stateSerializer)
                .encodeBase64UrlSafe()
        }
    }
    // collect [window.location.hash] => [navController]
    window.onhashchange = {
        installedScope.launch {
            val newState = it.newURL.substringAfterLast("#")
                .decodeBase64UrlSafeToStringOrNull()
                ?.deserializeJsonOrNull(stateSerializer)

            if (newState != null)
                state.emit(newState)
        }
    }
}

fun <T : Any> WindowSimpleNavController<T>.uninstall(): Boolean {
    if (!gIsInstalled) return false

    val installedScope = gInstalledScope
    val originalListener = gOriginalListener

    gIsInstalled = false
    gInstalledScope = null
    gOriginalListener = null
    isInstalled = false

    installedScope?.cancel()
    window.onhashchange = originalListener
    return true
}
