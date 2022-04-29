package me.kocproz.regionedit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlin.io.path.Path

fun main(args: Array<String>) {
    val regionFile = Path("data/r.-10.-9.mca")

    val region = RegionFile(regionFile.toFile())
    measureTimeMillisAndPrintTime("Building chunks: ") {
        region
            .buildChunks()
            .sortedByDescending { it.lengthInBytes }
            .forEach { chunk ->
                println(chunk)
            }
    }

//    runApplication()
}

fun runApplication() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Region Viewer",
        state = rememberWindowState(width = 480.dp, height = 560.dp)
    ) {
        val count = remember { mutableStateOf(0) }
        MaterialTheme {
            Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
                Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        count.value++
                    }) {
                    Text(if (count.value == 0) "Hello World" else "Clicked ${count.value}!")
                }
                Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        count.value = 0
                    }) {
                    Text("Reset")
                }
            }
        }
    }
}