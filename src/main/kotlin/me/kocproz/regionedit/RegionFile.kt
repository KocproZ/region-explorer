package me.kocproz.regionedit

import kotlinx.serialization.decodeFromByteArray
import net.benwoodworth.knbt.Nbt
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompression
import net.benwoodworth.knbt.NbtVariant
import java.io.File
import java.io.RandomAccessFile
import java.time.Instant
import kotlin.math.floor

class RegionFile(private val fileName: File) {

    companion object {
        val BYTES_IN_SECTOR = 4096
        val INTS_IN_SECTOR = BYTES_IN_SECTOR / 4
        val CHUNK_HEADER_SIZE = 5
    }

    val sectors: Int
    val sectorFree: BooleanArray
    val offsets: IntArray
    val chunkTimestamps: IntArray

    init {
        val file = RandomAccessFile(fileName, "r")

        sectors = (file.length() / BYTES_IN_SECTOR).toInt()
        offsets = IntArray(INTS_IN_SECTOR)
        chunkTimestamps = IntArray(INTS_IN_SECTOR)

        println("Sectors: $sectors")

        sectorFree = BooleanArray(sectors) { true }
        // First sector is header and Second sector is the timestamp table
        sectorFree[0] = false
        sectorFree[1] = false

        file.seek(0)
        for (i in 0 until INTS_IN_SECTOR) {
            val currentOffset = file.readInt()
            offsets[i] = currentOffset
            // IF sector location + chunk size in sectors <= sectors amount
            // Basically are there any sectors outside the file
            if (currentOffset != 0 && (currentOffset shr 8) + (currentOffset and 0xFF) <= sectors) {
                for (j in 0 until (currentOffset and 0xFF)) {
                    sectorFree[(currentOffset shr 8) + j] = false
                }
            }
        }
        for (i in 0 until INTS_IN_SECTOR) {
            chunkTimestamps[i] = file.readInt()
        }
    }

    fun buildChunks(): List<Chunk> = offsets
        .asList()
        .mapIndexedNotNull { i, offset ->
            if (offset == 0) return@mapIndexedNotNull null
            val sectorPosition = offsetToSector(offset)

            RandomAccessFile(fileName, "r").use { file ->
                file.seek((sectorPosition.first * BYTES_IN_SECTOR).toLong())
                val lengthInBytes = file.readInt()
                val compressionScheme = file.readByte()

                val nbtMetadata = Nbt {
                    variant = NbtVariant.Java
                    compression = compressionToEnum(compressionScheme)
                    ignoreUnknownKeys = false
                    encodeDefaults = false
                }

                val nbtData = ByteArray(lengthInBytes)
                file.read(nbtData)

                Chunk(
                    position = i,
                    chunkX = i % 32,
                    chunkZ = floor((i / 32).toDouble()).toInt(),
                    firstSectorPosition = sectorPosition.first,
                    sizeInSectors = sectorPosition.second,
                    lastModified = Instant.ofEpochSecond(chunkTimestamps[i].toLong()),
                    lengthInBytes = lengthInBytes,
                    compressionScheme = compressionScheme,
                    nbtData = tryOrNull { nbtMetadata.decodeFromByteArray<NbtCompound>(nbtData)[""] }
                )
            }
        }

    /**
     * Returns where the chunk starts in file in bytes
     */
    private fun chunkPosToOffset(x: Int, z: Int) = 4 * (x + z * 32)

    private fun offsetToSector(offset: Int): Pair<Int, Int> {
        return Pair((offset shr 8) and 0xFFFFFF, offset and 0xFF)
    }

    private fun compressionToEnum(flag: Byte) =
        when (flag) {
            0.toByte() -> NbtCompression.None
            1.toByte() -> NbtCompression.Gzip
            2.toByte() -> NbtCompression.Zlib
            else -> {
//                throw IllegalArgumentException("Flag $flag out of bounds <0-2>")
                println("WARN")
                NbtCompression.Zlib
            }
        }
}