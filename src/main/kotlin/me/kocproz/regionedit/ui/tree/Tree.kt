package me.kocproz.regionedit.ui.tree

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun tree(elements: List<TreeElement>) = Box {
    val listState = rememberLazyListState()
    val expanded = remember { mutableStateOf(emptySet<TreeElement>()) }
    val coroutineScope = rememberCoroutineScope()

    fun flatten(elements: List<TreeElement>, expanded: Set<TreeElement>, indent: Int): List<RenderedTreeElement> =
        elements.flatMap { element ->
            if (expanded.contains(element)) listOf(RenderedTreeElement(element, indent)) + flatten(
                element.children,
                expanded,
                indent + 1
            )
            else listOf(RenderedTreeElement(element, indent))
        }

    val flatElements = remember { mutableStateOf(flatten(elements, expanded.value, 0)) }

    fun setExpanded(treeElement: TreeElement, newNodeState: NodeState) = coroutineScope.launch {
        when (newNodeState) {
            NodeState.Collapsed -> expanded.value = expanded.value - treeElement
            NodeState.Expanded -> expanded.value = expanded.value + treeElement
        }
        flatElements.value = flatten(elements, expanded.value, 0)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        val startRender = System.currentTimeMillis()
        flatElements.value.map {
            item(it) { simpleNode(it, expanded.value.getNodeState(it.treeElement), ::setExpanded) }
        }
        println("tree render: ${System.currentTimeMillis() - startRender}ms")
    }
    VerticalScrollbar(
        modifier = Modifier.align(Alignment.CenterEnd),
        adapter = rememberScrollbarAdapter(listState)
    )
}

private fun Set<TreeElement>.getNodeState(treeElement: TreeElement): NodeState {
    return if (contains(treeElement)) NodeState.Expanded else NodeState.Collapsed
}