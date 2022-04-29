package me.kocproz.regionedit

import net.benwoodworth.knbt.NbtTag
import java.time.Instant

data class Chunk(
    /** Between 0 and 1023 **/
    val position: Int,
    /** Between 0 and 31 **/
    val chunkX: Int,
    /** Between 0 and 31 **/
    val chunkZ: Int,

    /** 2 or more **/
    val firstSectorPosition: Int,
    val sizeInSectors: Int,
    val lastModified: Instant,

    val sizeInBytes: Int,
    val nbtData: NbtTag?
)
