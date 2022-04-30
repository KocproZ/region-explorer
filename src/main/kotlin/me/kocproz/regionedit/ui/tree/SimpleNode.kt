package me.kocproz.regionedit.ui.tree

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp


enum class NodeState {
    Collapsed, Expanded;

    operator fun not(): NodeState = when (this) {
        Collapsed -> Expanded
        Expanded -> Collapsed
    }
}

@Composable
fun simpleNode(
    renderedElement: RenderedTreeElement,
    expanded: NodeState,
    setExpanded: (TreeElement, NodeState) -> Unit
): Unit =
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = (renderedElement.indent * 20).dp)
    ) {
        if (renderedElement.treeElement.children.isNotEmpty()) {
            val iconRotation = when (expanded) {
                NodeState.Collapsed -> 0f
                NodeState.Expanded -> 90f
            }
            IconButton(
                onClick = { setExpanded(renderedElement.treeElement, !expanded) },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    Icons.Filled.PlayArrow,
                    "",
                    modifier = Modifier.size(20.dp).rotate(iconRotation)
                )
            }
        } else {
            Icon(Icons.Filled.Check, contentDescription = "", modifier = Modifier.size(20.dp))
        }
        Text(renderedElement.treeElement.text)
    }