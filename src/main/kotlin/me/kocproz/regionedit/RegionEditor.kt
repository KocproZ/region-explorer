package me.kocproz.regionedit

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import me.kocproz.regionedit.ui.tree.TreeElement
import me.kocproz.regionedit.ui.tree.tree
import kotlin.io.path.Path

fun main(args: Array<String>) {
    val regionFile = Path("data/r.-10.-9.mca")

    val region = RegionFile(regionFile.toFile())
    var chunks: List<Chunk> = mutableListOf()
    measureTimeMillisAndPrintTime("Building chunks: ") {
        chunks = region
            .buildChunks()
            .sortedBy { it.position }
    }

    runApplication(chunks)
}

fun runApplication(chunk: List<Chunk>) = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Region Viewer",
        state = rememberWindowState(width = 900.dp, height = 1200.dp)
    ) {
        MaterialTheme {
            val elements = remember {
                (1..100).map { parent ->
                    TreeElement(
                        "$parent",
                        (1..2).map { TreeElement("$parent-$it", listOf(TreeElement("$parent-$it-1"))) })
                }
            }
            tree(elements)
        }
    }
}
