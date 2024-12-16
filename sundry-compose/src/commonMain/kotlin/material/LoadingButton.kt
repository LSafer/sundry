package net.lsafer.sundry.compose.material

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun LoadingButton(
    onClick: () -> Unit,
    loading: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = {
            if (!loading) {
                onClick()
            }
        },
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    ) {
        Box {
            Crossfade(loading) {
                Row(
                    modifier = Modifier
                        .alpha(if (loading) 0f else 1f)
                        .defaultMinSize(
                            minWidth = ButtonDefaults.MinWidth -
                                    contentPadding.calculateLeftPadding(LayoutDirection.Ltr) -
                                    contentPadding.calculateRightPadding(LayoutDirection.Ltr),
                            minHeight = ButtonDefaults.MinHeight -
                                    contentPadding.calculateTopPadding() -
                                    contentPadding.calculateBottomPadding(),
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    content()
                }

                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(27.dp).align(Alignment.Center),
                        color = if (enabled)
                            contentColorFor(colors.containerColor)
                        else
                            contentColorFor(colors.disabledContainerColor)
                    )
                }
            }
        }
    }
}
