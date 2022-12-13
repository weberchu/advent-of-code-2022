private val offsets = listOf(
    Pair(0, 1),
    Pair(0, -1),
    Pair(1, 0),
    Pair(-1, 0)
)

private fun part1(map: List<String>): Int {
    val mapWidth = map[0].length
    val mapHeight = map.size

    var start = Pair(0, 0)
    var end = Pair(0, 0)
    for ((r, line) in map.withIndex()) {
        val s = line.indexOf('S')
        if (s != -1) {
            start = Pair(r, s)
        }
        val e = line.indexOf('E')
        if (e != -1) {
            end = Pair(r, e)
        }
    }

    val costs = List(mapHeight) {
        MutableList(mapWidth) { Int.MAX_VALUE }
    }
    costs[start.first][start.second] = 0

    val pendingInvestigationLocation = ArrayDeque(listOf(start))

    while (pendingInvestigationLocation.isNotEmpty()) {
        val location = pendingInvestigationLocation.removeFirst()
        val cost = costs[location.first][location.second]
        var height = map[location.first][location.second]
        if (height == 'S') {
            height = 'a'
        }

        for (offset in offsets) {
            val nextR = location.first + offset.first
            val nextC = location.second + offset.second

            if (nextR in 0 until mapHeight && nextC in 0 until mapWidth) {
                var nextHeight = map[nextR][nextC]
                if (nextHeight == 'E') {
                    nextHeight = 'z'
                }

                if (nextHeight - height <= 1 && costs[nextR][nextC] > cost + 1) {
                    costs[nextR][nextC] = cost + 1
                    pendingInvestigationLocation.addLast(Pair(nextR, nextC))
                }
            }
        }
    }

    return costs[end.first][end.second]
}

private fun part2(map: List<String>): Int {
    // go from E until first a
    val mapWidth = map[0].length
    val mapHeight = map.size

    var end = Pair(0, 0)
    for ((r, line) in map.withIndex()) {
        val e = line.indexOf('E')
        if (e != -1) {
            end = Pair(r, e)
        }
    }

    val costs = List(mapHeight) {
        MutableList(mapWidth) { Int.MAX_VALUE }
    }
    costs[end.first][end.second] = 0

    val pendingInvestigationLocation = ArrayDeque(listOf(end))

    while (pendingInvestigationLocation.isNotEmpty()) {
        val location = pendingInvestigationLocation.removeFirst()
        val cost = costs[location.first][location.second]
        var height = map[location.first][location.second]
        if (height == 'E') {
            height = 'z'
        }

        for (offset in offsets) {
            val nextR = location.first + offset.first
            val nextC = location.second + offset.second

            if (nextR in 0 until mapHeight && nextC in 0 until mapWidth) {
                val nextHeight = map[nextR][nextC]

                if (height - nextHeight <= 1 && costs[nextR][nextC] > cost + 1) {
                    if (nextHeight == 'a' || nextHeight == 'S') {
                        return cost + 1
                    }

                    costs[nextR][nextC] = cost + 1
                    pendingInvestigationLocation.addLast(Pair(nextR, nextC))
                }
            }
        }
    }

    return costs[end.first][end.second]
}

fun main() {
    val input = readInput("Day12")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
