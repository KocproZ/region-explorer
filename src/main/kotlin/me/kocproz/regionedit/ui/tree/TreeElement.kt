package me.kocproz.regionedit.ui.tree

class TreeElement(
    val text: String,
    val children: List<TreeElement> = emptyList()
)

class RenderedTreeElement(
    val treeElement: TreeElement,
    val indent: Int
)