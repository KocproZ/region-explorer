package me.kocproz.regionedit

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import me.kocproz.regionedit.ui.tree.TreeElement
import me.kocproz.regionedit.ui.tree.tree
import net.benwoodworth.knbt.NbtByte
import net.benwoodworth.knbt.NbtByteArray
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtDouble
import net.benwoodworth.knbt.NbtFloat
import net.benwoodworth.knbt.NbtInt
import net.benwoodworth.knbt.NbtIntArray
import net.benwoodworth.knbt.NbtList
import net.benwoodworth.knbt.NbtLong
import net.benwoodworth.knbt.NbtLongArray
import net.benwoodworth.knbt.NbtShort
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.NbtTag
import kotlin.io.path.Path

fun main(args: Array<String>) {
    val regionFile = Path("data/entities/r.-10.-9.mca")

    val region = RegionFile(regionFile.toFile())
    var chunks: List<Chunk> = mutableListOf()
    measureTimeMillisAndPrintTime("Building chunks: ") {
        chunks = region
            .buildChunks()
            .sortedBy { it.position }
    }

    runApplication(chunks)
}

fun runApplication(chunks: List<Chunk>) = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Region Viewer",
        state = rememberWindowState(width = 900.dp, height = 1200.dp)
    ) {
        MaterialTheme {
            val elements = remember {
                chunks.map { parent ->
                    TreeElement(
                        "$parent",
                        parseNbtTag(parent.nbtData?.get(""))
                    )
                }
            }
            tree(elements)
        }
    }
}

fun parseNbtTag(tag: NbtTag?): List<TreeElement> =
    when (tag) {
        is NbtInt           -> listOf(TreeElement("I: ${tag.value}"))
        is NbtByte          -> listOf(TreeElement("B: ${tag.value}"))
        is NbtLong          -> listOf(TreeElement("L: ${tag.value}"))
        is NbtFloat         -> listOf(TreeElement("F: ${tag.value}"))
        is NbtDouble        -> listOf(TreeElement("D: ${tag.value}"))
        is NbtShort         -> listOf(TreeElement("S: ${tag.value}"))
        is NbtString        -> listOf(TreeElement("T: ${tag.value}"))
        is NbtCompound      -> tag.entries.map { TreeElement(it.key, parseNbtTag(it.value)) }
        is NbtList<NbtTag>  -> tag.mapIndexed { index, it -> TreeElement("$index", parseNbtTag(it)) }
        is NbtIntArray      -> tag.mapIndexed { index, it -> TreeElement("I[$index]: $it") }
        is NbtByteArray     -> tag.mapIndexed { index, it -> TreeElement("B[$index]: $it") }
        is NbtLongArray     -> tag.mapIndexed { index, it -> TreeElement("L[$index]: $it") }
        else                -> listOf(TreeElement("Idk lol, ${tag?.javaClass}"))
    }
