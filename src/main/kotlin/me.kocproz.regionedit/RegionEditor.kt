package me.kocproz.regionedit

import net.benwoodworth.knbt.Nbt
import net.benwoodworth.knbt.NbtCompression
import net.benwoodworth.knbt.NbtVariant


fun main(args: Array<String>) {
    val x = Nbt {
        variant = NbtVariant.Java
        compression = NbtCompression.None
    }
    println("Hello world!")
}
