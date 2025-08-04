package net.lsafer.sundry.compose.material

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private val SCRIM_COLOR = Color.Black.copy(alpha = 0.6f)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun DatePickerDialogCompat(
    visible: Boolean,
    onConfirm: (LocalDate) -> Unit,
    onCancel: () -> Unit,
    onReset: (() -> Unit)? = null,
    initialValue: LocalDate? = null,

    confirmLabel: String = "Confirm",
    cancelLabel: String = "Cancel",
    resetLabel: String = "Reset",
) {
    val state = remember { MutableTransitionState(false) }

    LaunchedEffect(visible) {
        state.targetState = visible
    }

    val opacity by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutLinearInEasing
        )
    )
    val scrimColor by animateColorAsState(
        targetValue = if (visible) SCRIM_COLOR else Color.Transparent,
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutLinearInEasing
        )
    )

    if (opacity != 0f) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialValue?.toEpochDays()?.times(86400000L),
            initialDisplayMode = DisplayMode.Picker,
        )

        DatePickerDialog(
            onDismissRequest = onCancel,
            modifier = Modifier.alpha(opacity),
            confirmButton = {
                TextButton(
                    onClick = it@{
                        val epocMillis = pickerState.selectedDateMillis ?: return@it
                        val datetime = Instant.fromEpochMilliseconds(epocMillis)
                            .toLocalDateTime(TimeZone.currentSystemDefault())

                        onConfirm(datetime.date)
                    },
                    enabled = pickerState.selectedDateMillis != null,
                    content = { Text(confirmLabel) }
                )
            },
            dismissButton = {
                TextButton(onCancel) {
                    Text(cancelLabel)
                }

                if (onReset != null) {
                    TextButton(
                        onClick = onReset,
                        enabled = pickerState.selectedDateMillis != null,
                        content = { Text(resetLabel) }
                    )
                }
            },
            properties = createDialogProperties(scrimColor),
            content = {
                DatePicker(
                    state = pickerState,
                )
            }
        )
    }
}

internal expect fun createDialogProperties(scrimColor: Color): DialogProperties
