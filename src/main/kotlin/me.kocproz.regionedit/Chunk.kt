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

    val lengthInBytes: Int,
    /** 0 = None, 1 = gzip, 2 = zlib **/
    val compressionScheme: Byte,
    val nbtData: NbtTag?
) {
    override fun toString(): String {
        return "Chunk(position=$position, chunkX=$chunkX, chunkZ=$chunkZ, firstSectorPosition=$firstSectorPosition, sizeInSectors=$sizeInSectors, lastModified=$lastModified, lengthInBytes=$lengthInBytes, compressionScheme=$compressionScheme)"
    }
}
