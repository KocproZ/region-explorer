package me.kocproz.regionedit

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.launch
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
        val stateVertical = rememberScrollState(0)
        val coroutineScope = rememberCoroutineScope()

        fun ScrollState.applyScroll(scroll: Float) {
            coroutineScope.launch {
                this@applyScroll.scrollBy(scroll)
            }
        }

        MaterialTheme {
            Box {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .then(Modifier.verticalScroll(stateVertical))
                        .then(Modifier.draggable(rememberDraggableState { stateVertical.applyScroll(-it) }, Orientation.Vertical)),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    chunk.forEach {
                        tableRow(it)
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(stateVertical)
                )
            }
        }
    }
}

@Composable
fun ColumnScope.tableRow(chunk: Chunk) = Row(
    modifier = Modifier.align(Alignment.Start)
) {
    Box {
        Text(text = AnnotatedString(chunk.toString()), modifier = Modifier.align(Alignment.CenterStart))
    }
}
