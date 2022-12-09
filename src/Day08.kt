private fun markColumnVisibility(
    columnRange: IntProgression,
    heightRange: IntProgression,
    map: List<List<Int>>,
    visibilityMap: List<MutableList<Boolean>>,
    visibleTreeCountSoFar: Int
): Int {
    var visibleTreeCount = visibleTreeCountSoFar
    for (c in columnRange) {
        var tallestTree = -1
        for (r in heightRange) {
            if (map[r][c] > tallestTree) {
                tallestTree = map[r][c]
                if (!visibilityMap[r][c]) {
                    visibilityMap[r][c] = true
                    visibleTreeCount++
                }

                if (tallestTree == 9) {
                    // it can't go taller
                    break
                }
            }
        }
    }

    return visibleTreeCount
}

private fun markRowVisibility(
    columnRange: IntProgression,
    heightRange: IntProgression,
    map: List<List<Int>>,
    visibilityMap: List<MutableList<Boolean>>,
    visibleTreeCountSoFar: Int
): Int {
    var visibleTreeCount = visibleTreeCountSoFar
    for (r in heightRange) {
        var tallestTree = -1
        for (c in columnRange) {
            if (map[r][c] > tallestTree) {
                tallestTree = map[r][c]
                if (!visibilityMap[r][c]) {
                    visibilityMap[r][c] = true
                    visibleTreeCount++
                }

                if (tallestTree == 9) {
                    // it can't go taller
                    break
                }
            }
        }
    }

    return visibleTreeCount
}

private fun part1(input: List<String>): Int {
    val map = input.map { row ->
        row.map { tree -> tree.digitToInt() }
    }

    val width = map[0].size
    val height = map.size

    var visibleTreeCount = 0
    val visibilityMap = List(height) {
        MutableList(width) {
            false
        }
    }

    // from top
    visibleTreeCount = markColumnVisibility(0 until width, 0 until height, map, visibilityMap, visibleTreeCount)

    // from bottom
    visibleTreeCount = markColumnVisibility(0 until width, height - 1 downTo 0, map, visibilityMap, visibleTreeCount)

    // from left
    visibleTreeCount = markRowVisibility(0 until width, 0 until height, map, visibilityMap, visibleTreeCount)

    // from right
    visibleTreeCount = markRowVisibility(width - 1 downTo 0, 0 until height, map, visibilityMap, visibleTreeCount)

    return visibleTreeCount
}

private fun part2(input: List<String>): Int {
    val map = input.map { row ->
        row.map { tree -> tree.digitToInt() }
    }

    val width = map[0].size
    val height = map.size

    var highestScore = Int.MIN_VALUE

    for (r in 1 until height - 1) {
        for (c in 1 until width - 1) {
            val currentHeight = map[r][c]

            var upScore = 1
            for (upR in r - 1 downTo 1) {
                if (map[upR][c] < currentHeight) {
                    upScore++
                } else {
                    break
                }
            }

            var downScore = 1
            for (downR in r + 1 until height - 1) {
                if (map[downR][c] < currentHeight) {
                    downScore++
                } else {
                    break
                }
            }

            var leftScore = 1
            for (leftC in c - 1 downTo 1) {
                if (map[r][leftC] < currentHeight) {
                    leftScore++
                } else {
                    break
                }
            }

            var rightScore = 1
            for (rightC in c + 1 until width - 1) {
                if (map[r][rightC] < currentHeight) {
                    rightScore++
                } else {
                    break
                }
            }

            val score = leftScore * rightScore * upScore * downScore
            if (score > highestScore) {
                highestScore = score
            }
        }
    }

    return highestScore
}

fun main() {
    val input = readInput("Day08")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
