package me.kocproz.regionedit

import kotlinx.serialization.encodeToByteArray
import net.benwoodworth.knbt.NbtCompound
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
    val nbtData: NbtCompound?
) {
    val lengthInBytesDecompressed: Int by lazy {
        RegionFile.NBT_METADATA_NO_COMPRESSION.encodeToByteArray(NbtCompound(mapOf("" to nbtData!!))).size
    }

    override fun toString(): String {
        return "Chunk(position=$position, chunkX=$chunkX, chunkZ=$chunkZ, firstSectorPosition=$firstSectorPosition, sizeInSectors=$sizeInSectors, lastModified=$lastModified, lengthInBytes=$lengthInBytes, compressionScheme=$compressionScheme)"
    }
}
