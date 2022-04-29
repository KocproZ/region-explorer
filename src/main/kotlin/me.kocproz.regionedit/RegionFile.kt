package me.kocproz.regionedit

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

            RandomAccessFile(fileName, "r").use {

            }


            Chunk(
                position = i,
                chunkX = i % 32,
                chunkZ = floor((i / 32).toDouble()).toInt(),
                firstSectorPosition = sectorPosition.first,
                sizeInSectors = sectorPosition.second,
                lastModified = Instant.ofEpochSecond(chunkTimestamps[i].toLong()),
                sizeInBytes = -1,
                nbtData = null
            )
        }

    /**
     * Returns where the chunk starts in file in bytes
     */
    private fun chunkPosToOffset(x: Int, z: Int) = 4 * (x + z * 32)

    private fun offsetToSector(offset: Int): Pair<Int, Int> {
        return Pair((offset shr 8) and 0xFFFFFF, offset and 0xFF)
    }

}