package net.lsafer.sundry.compose.simplenav

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SimpleNavController(vararg defaults: Any) {
    @PublishedApi internal var currentIndex by mutableStateOf(defaults.lastIndex)
    @PublishedApi internal val entries = mutableStateListOf(*defaults)

    val current get() = entries.getOrNull(currentIndex)

    fun navigate(route: Any) {
        val current = when (currentIndex) {
            -1 -> null
            else -> entries[currentIndex]
        }
        if (route == current)
            return
        if (entries.lastIndex > currentIndex)
            entries.removeRange(currentIndex + 1, entries.size)
        entries += route
        currentIndex++
    }

    fun navigateUp(): Boolean {
        if (currentIndex <= 0) return false
        currentIndex--
        return true
    }

    fun navigateDown(): Boolean {
        if (currentIndex >= entries.lastIndex) return false
        currentIndex++
        return true
    }
}
