private enum class FaceDirection(val score: Int) {
    Up(3), Down(1), Left(2), Right(0)
}

private interface Instruction

private data class Move(
    val steps: Int
) : Instruction

private data class Turn(
    val direction: String
) : Instruction

private fun path(input: String): List<Instruction> {
    val instructions = mutableListOf<Instruction>()

    var startIndex = 0
    do {
        val turnIndex = input.indexOfAny("LR".toCharArray(), startIndex)
        if (turnIndex == -1) {
            instructions.add(Move(input.substring(startIndex).toInt()))
            break
        } else {
            instructions.add(Move(input.substring(startIndex, turnIndex).toInt()))
            instructions.add(Turn(input.substring(turnIndex, turnIndex + 1)))
            startIndex = turnIndex + 1
        }
    } while (true)

    return instructions
}

private fun parseInput(input: List<String>): Pair<MutableList<String>, List<Instruction>> {
    val mapWidth = input.maxOf { it.length }
    val map = mutableListOf<String>()
    var i = 0
    while (input[i].isNotEmpty()) {
        map.add(input[i].padEnd(mapWidth, ' '))
        i++
    }
    val path = path(input[i + 1])
    return Pair(map, path)
}

private fun turn(direction: FaceDirection, instruction: Turn): FaceDirection {
    return when (direction) {
        FaceDirection.Right -> if (instruction.direction == "L") FaceDirection.Up else FaceDirection.Down
        FaceDirection.Down -> if (instruction.direction == "L") FaceDirection.Right else FaceDirection.Left
        FaceDirection.Left -> if (instruction.direction == "L") FaceDirection.Down else FaceDirection.Up
        FaceDirection.Up -> if (instruction.direction == "L") FaceDirection.Left else FaceDirection.Right
    }
}

private fun moveOffset(direction: FaceDirection): Pair<Int, Int> {
    return when (direction) {
        FaceDirection.Right -> Pair(0, 1)
        FaceDirection.Down -> Pair(1, 0)
        FaceDirection.Left -> Pair(0, -1)
        FaceDirection.Up -> Pair(-1, 0)
    }
}

private fun part1(input: List<String>): Int {
    val (map, path) = parseInput(input)
    val mapHeight = map.size
    val mapWidth = map[0].length

    var coordinate = Pair(0, map[0].indexOfFirst { it != ' ' })
    var direction = FaceDirection.Right

    for (instruction in path) {
        if (instruction is Turn) {
            direction = turn(direction, instruction)
        } else if (instruction is Move) {
            val offset = moveOffset(direction)

            for (step in 1..instruction.steps) {
                var nextCoordinate = Pair(
                    (coordinate.first + offset.first + mapHeight) % mapHeight,
                    (coordinate.second + offset.second + mapWidth) % mapWidth
                )
                var nextTile = map[nextCoordinate.first][nextCoordinate.second]

                while (nextTile == ' ') {
                    nextCoordinate = Pair(
                        (nextCoordinate.first + offset.first + mapHeight) % mapHeight,
                        (nextCoordinate.second + offset.second + mapWidth) % mapWidth
                    )
                    nextTile = map[nextCoordinate.first][nextCoordinate.second]
                }

                if (nextTile == '.') {
                    coordinate = nextCoordinate
                } else if (nextTile == '#') {
                    break
                } else {
                    throw IllegalArgumentException("Unknown tile $nextTile")
                }
            }
        }
    }

    return 1000 * (coordinate.first + 1) + 4 * (coordinate.second + 1) + direction.score
}

private fun part2(input: List<String>, faceWidth: Int): Int {
    val (map, path) = parseInput(input)

    var coordinate = Pair(0, map[0].indexOfFirst { it != ' ' })
    var direction = FaceDirection.Right

    val warps = mutableMapOf<Triple<FaceDirection, Int, Int>, Triple<FaceDirection, Int, Int>>()
    for (i in 0 until faceWidth) {
        warps[Triple(FaceDirection.Left, i, faceWidth)] = Triple(FaceDirection.Right, 3 * faceWidth - i - 1, 0)
        warps[Triple(FaceDirection.Left, faceWidth + i, faceWidth)] = Triple(FaceDirection.Down, faceWidth * 2, i)
        warps[Triple(FaceDirection.Up, faceWidth * 2, i)] = Triple(FaceDirection.Right, faceWidth + i, faceWidth)
        warps[Triple(FaceDirection.Left, 3 * faceWidth - i - 1, 0)] = Triple(FaceDirection.Right, i, faceWidth)
        warps[Triple(FaceDirection.Left, 3 * faceWidth + i, 0)] = Triple(FaceDirection.Down, 0, faceWidth + i)
        warps[Triple(FaceDirection.Down, 4 * faceWidth - 1, i)] = Triple(FaceDirection.Down, 0, faceWidth * 2 + i)
        warps[Triple(FaceDirection.Right, 3 * faceWidth + i, faceWidth - 1)] = Triple(FaceDirection.Up, 3 * faceWidth - 1, faceWidth + i)
        warps[Triple(FaceDirection.Down, 3 * faceWidth - 1, faceWidth + i)] = Triple(FaceDirection.Left, 3 * faceWidth + i, faceWidth - 1)
        warps[Triple(FaceDirection.Right, 2 * faceWidth + i, 2 * faceWidth - 1)] = Triple(FaceDirection.Left, faceWidth - i - 1, 3 * faceWidth - 1)
        warps[Triple(FaceDirection.Right, faceWidth + i, 2 * faceWidth - 1)] = Triple(FaceDirection.Up, faceWidth - 1, 2 * faceWidth + i)
        warps[Triple(FaceDirection.Down, faceWidth - 1, 2 * faceWidth + i)] = Triple(FaceDirection.Left, faceWidth + i, 2 * faceWidth - 1)
        warps[Triple(FaceDirection.Right, faceWidth - i - 1, 3 * faceWidth - 1)] = Triple(FaceDirection.Left, 2 * faceWidth + i, 2 * faceWidth - 1)
        warps[Triple(FaceDirection.Up, 0, faceWidth * 2 + i)] = Triple(FaceDirection.Up, 4 * faceWidth - 1, i)
        warps[Triple(FaceDirection.Up, 0, faceWidth + i)] = Triple(FaceDirection.Right, 3 * faceWidth + i, 0)
    }

    for (instruction in path) {
        if (instruction is Turn) {
            direction = turn(direction, instruction)
        } else if (instruction is Move) {
            for (step in 1..instruction.steps) {
                if (warps.containsKey(Triple(direction, coordinate.first, coordinate.second))) {
                    val wrap = warps[Triple(direction, coordinate.first, coordinate.second)]!!
                    val nextCoordinate = Pair(wrap.second, wrap.third)
                    val nextTile = map[nextCoordinate.first][nextCoordinate.second]
                    if (nextTile == '.') {
                        coordinate = nextCoordinate
                        direction = wrap.first
                    } else if (nextTile == '#') {
                        break
                    } else {
                        throw IllegalArgumentException("Unknown tile $nextTile")
                    }
                } else {
                    val offset = moveOffset(direction)
                    val nextCoordinate = Pair(
                        coordinate.first + offset.first,
                        coordinate.second + offset.second
                    )
                    val nextTile = map[nextCoordinate.first][nextCoordinate.second]
                    if (nextTile == '.') {
                        coordinate = nextCoordinate
                    } else if (nextTile == '#') {
                        break
                    } else {
                        throw IllegalArgumentException("Unknown tile $nextTile")
                    }
                }
            }
        }
    }

    return 1000 * (coordinate.first + 1) + 4 * (coordinate.second + 1) + direction.score
}

fun main() {
    val input = readInput("Day22")
    val faceWidth = 50
//    val input = readInput("Test")
//    val faceWidth = 4

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input, faceWidth))
}
