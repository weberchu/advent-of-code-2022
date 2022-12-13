private data class Packet(
    val list: List<Packet>?,
    val integer: Int?
) : Comparable<Packet> {
    override fun toString(): String {
        return list?.toString() ?: integer.toString()
    }

    // negative if self is right order, positive if not, 0 if equal
    override fun compareTo(other: Packet): Int {
        if (integer != null && other.integer != null) {
            return integer.compareTo(other.integer)
        }

        val selfList = list ?: listOf(Packet(null, integer))
        val otherList = other.list ?: listOf(Packet(null, other.integer))

        for (i in selfList.indices) {
            if (i >= otherList.size) {
                return 1
            }

            val itemCompare = selfList[i].compareTo(otherList[i])
            if (itemCompare != 0) {
                return itemCompare
            }
        }

        return if (selfList.size < otherList.size) {
            -1
        } else {
            0
        }
    }
}

private fun parsePacket(string: String): Packet {
    if (string.startsWith('[')) {
        val list = mutableListOf<Packet>()
        var start = 1
        var bracketCount = 0
        for (i in 1 until string.length) {
            when (string[i]) {
                '[' -> bracketCount++
                ']' -> bracketCount--
                ',' -> if (bracketCount == 0) {
                    list.add(parsePacket(string.substring(start, i)))
                    start = i + 1
                }
            }
        }
        if (start < string.length - 1) {
            list.add(parsePacket(string.substring(start, string.length - 1)))
        }

        return Packet(list, null)
    } else {
        return Packet(null, string.toInt())
    }
}

private fun part1(input: List<String>): Int {
    var sumOfRightIndices = 0

    for (i in 0..input.size / 3) {
        val left = parsePacket(input[i * 3])
        val right = parsePacket(input[i * 3 + 1])

        if (left < right) {
            sumOfRightIndices += i + 1
        }
    }

    return sumOfRightIndices
}

private fun part2(input: List<String>): Int {
    val packetTwo = parsePacket("[[2]]")
    val packetSix = parsePacket("[[6]]")

    val allPackets = mutableListOf(packetTwo, packetSix)
    for (i in 0..input.size / 3) {
        allPackets.add(parsePacket(input[i * 3]))
        allPackets.add(parsePacket(input[i * 3 + 1]))
    }

    val sortedPackets = allPackets.sorted()

    return (sortedPackets.indexOf(packetTwo) + 1) * (sortedPackets.indexOf(packetSix) + 1)
}

fun main() {
    val input = readInput("Day13")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
