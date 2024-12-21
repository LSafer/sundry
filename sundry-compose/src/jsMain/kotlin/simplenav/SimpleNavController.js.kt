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

fun <T : Any> WindowSimpleNavController<T>.tryGlobalInstall() =
    if (gIsInstalled) false else run { globalInstall(); true }

fun <T : Any> WindowSimpleNavController<T>.globalInstall() {
    check(!gIsInstalled) { "A NavController was already globally installed" }

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

fun <T : Any> WindowSimpleNavController<T>.tryGlobalUninstall() =
    if (!isInstalled) false else run { globalUninstall(); true }

fun <T : Any> WindowSimpleNavController<T>.globalUninstall() {
    check(gIsInstalled) { "NavController is not globally installed" }

    val installedScope = gInstalledScope
    val originalListener = gOriginalListener

    gIsInstalled = false
    gInstalledScope = null
    gOriginalListener = null
    isInstalled = false

    installedScope?.cancel()
    window.onhashchange = originalListener
}
