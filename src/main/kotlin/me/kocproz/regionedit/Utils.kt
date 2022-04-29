package me.kocproz.regionedit

inline fun <T> measureTimeMillisAndPrintTime(msg: String, block: () -> T): T {
    val start = System.currentTimeMillis()
    val result = block()
    println("$msg time: ${System.currentTimeMillis() - start}")
    return result
}

inline fun <T> tryOrNull(block: () -> T): T? {
    return try {
        block.invoke()
    } catch (ex: Exception) {
        println("Exception: ${ex.message}")
        null
    }
}
