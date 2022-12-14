private fun parseRocks(input: List<String>): Set<Pair<Int, Int>> {
    val rocks = mutableSetOf<Pair<Int, Int>>()

    for (line in input) {
        val rockCorners = line.split(" -> ")
        for (i in 0..rockCorners.size - 2) {
            val corner1 = rockCorners[i].split(",").map { it.toInt() }
            val corner2 = rockCorners[i + 1].split(",").map { it.toInt() }

            if (corner1[0] == corner2[0]) {
                val smallY = if (corner1[1] < corner2[1]) corner1[1] else corner2[1]
                val largeY = if (corner1[1] < corner2[1]) corner2[1] else corner1[1]
                for (y in smallY..largeY) {
                    rocks.add(Pair(corner1[0], y))
                }
            } else {
                val smallX = if (corner1[0] < corner2[0]) corner1[0] else corner2[0]
                val largeX = if (corner1[0] < corner2[0]) corner2[0] else corner1[0]
                for (x in smallX..largeX) {
                    rocks.add(Pair(x, corner1[1]))
                }
            }
        }
    }

    return rocks
}

private fun part1(input: List<String>): Int {
    val rocks = parseRocks(input)
    val maxY = rocks.map { it.second }.reduce { rock1, rock2 ->
        if (rock1 < rock2) rock2 else rock1
    }

    val sandsAndRocks = rocks.toMutableSet()
    var sandCount = 0
    var isSandRest: Boolean

    do {
        var sand = Pair(500, 0)
        isSandRest = false

        do {
            if (!sandsAndRocks.contains(Pair(sand.first, sand.second + 1))) {
                sand = Pair(sand.first, sand.second + 1)
            } else if (!sandsAndRocks.contains(Pair(sand.first - 1, sand.second + 1))) {
                sand = Pair(sand.first - 1, sand.second + 1)
            } else if (!sandsAndRocks.contains(Pair(sand.first + 1, sand.second + 1))) {
                sand = Pair(sand.first + 1, sand.second + 1)
            } else {
                sandsAndRocks.add(sand)
                sandCount++
                isSandRest = true
            }

            if (sand.second == maxY || sand == Pair(500, 0)) {
                isSandRest = false
                break
            }
        } while (!isSandRest)
    } while (isSandRest)

    return sandCount
}

private fun part2(input: List<String>): Int {
    val rocks = parseRocks(input)
    val maxY = rocks.map { it.second }.reduce { rock1, rock2 ->
        if (rock1 < rock2) rock2 else rock1
    }

    val sandsAndRocks = rocks.toMutableSet()
    var sandCount = 0
    var isSandRest: Boolean

    do {
        var sand = Pair(500, 0)
        isSandRest = false

        do {
            if (sand.second == maxY + 1) {
                sandsAndRocks.add(sand)
                sandCount++
                isSandRest = true
            } else if (!sandsAndRocks.contains(Pair(sand.first, sand.second + 1))) {
                sand = Pair(sand.first, sand.second + 1)
            } else if (!sandsAndRocks.contains(Pair(sand.first - 1, sand.second + 1))) {
                sand = Pair(sand.first - 1, sand.second + 1)
            } else if (!sandsAndRocks.contains(Pair(sand.first + 1, sand.second + 1))) {
                sand = Pair(sand.first + 1, sand.second + 1)
            } else {
                sandsAndRocks.add(sand)
                sandCount++
                isSandRest = true
            }

            if (sand == Pair(500, 0)) {
                isSandRest = false
                break
            }
        } while (!isSandRest)
    } while (isSandRest)

    return sandCount
}

fun main() {
    val input = readInput("Day14")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
