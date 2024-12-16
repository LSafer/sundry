package net.lsafer.sundry.compose.adaptive

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

interface AdaptiveGridScope {
    fun item(
        modifier: Modifier = Modifier,
        verticalArrangement: Arrangement.Vertical = Arrangement.Top,
        horizontalAlignment: Alignment.Horizontal? = null,
        columnSpan: Int = 1,
        disablePadding: Boolean = false,
        shrink: Boolean = false,
        content: @Composable ColumnScope.() -> Unit
    )
}

@Composable
fun AdaptiveVerticalGrid(
    minColumnWidth: Dp,
    modifier: Modifier = Modifier,
    nColumnIn: IntRange = 0..Int.MAX_VALUE,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    /** true, causes columns to merge with neighboring empty columns */
    mergeWithEmptyColumns: Boolean = true,
    content: AdaptiveGridScope.() -> Unit
) {
    BoxWithConstraints(modifier) {
        val items = mutableListOf<Pair<Int, @Composable RowScope.(Int) -> Unit>>()

        content(object : AdaptiveGridScope {
            override fun item(
                modifier: Modifier,
                verticalArrangement: Arrangement.Vertical,
                horizontalAlignment: Alignment.Horizontal?,
                columnSpan: Int,
                disablePadding: Boolean,
                shrink: Boolean,
                content: @Composable ColumnScope.() -> Unit
            ) {
                items += columnSpan to { count ->
                    Column(
                        modifier = Modifier
                            .let { if (disablePadding) it else it.padding(contentPadding) }
                            .let { if (shrink) it else it.weight(columnSpan.toFloat()) }
                            .let { if (count == 1) it.weight(1f) else it }
                            .then(modifier),
                        verticalArrangement = verticalArrangement,
                        horizontalAlignment =
                            when {
                                horizontalAlignment != null -> horizontalAlignment
                                count == 1 -> Alignment.CenterHorizontally
                                else -> Alignment.Start
                            },
                        content = content,
                    )
                }
            }
        })

        val iter = items.listIterator()

        val nColumn = (this.maxWidth / minColumnWidth)
            .roundToInt()
            .coerceAtLeast(1)
            .coerceIn(nColumnIn)

        Column(Modifier, verticalArrangement, horizontalAlignment) {
            while (iter.hasNext()) {
                val blocks = buildList<@Composable RowScope.(Int) -> Unit> {
                    var currentColumn = 0

                    while (currentColumn < nColumn) {
                        if (!iter.hasNext()) break

                        val (columnSpan, block) = iter.next()

                        if (currentColumn != 0 && columnSpan > nColumn - currentColumn) {
                            iter.previous()
                            break
                        }

                        currentColumn += columnSpan

                        add(block)
                    }
                }

                Row(
                    modifier = Modifier
                        .let {
                            if (blocks.size > 1)
                                it.height(IntrinsicSize.Min)
                            else
                                it
                        },
                    horizontalArrangement = horizontalArrangement
                ) {
                    blocks.forEach { it(blocks.size) }

                    if (mergeWithEmptyColumns) {
                        val remainingColumns = nColumn - blocks.size

                        Box(Modifier.weight(remainingColumns.toFloat()).padding(contentPadding))
                    }
                }
            }
        }
    }
}
