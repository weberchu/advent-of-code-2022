private data class Cube(
    val x: Int,
    val y: Int,
    val z: Int
)

private fun droplet(input: List<String>): Set<Cube> {
    return input.map { line ->
        line.split(",")
            .map { it.toInt() }
    }.map {
        Cube(it[0], it[1], it[2])
    }.toSet()
}

private fun surfaceArea(droplet: Set<Cube>): Int {
    val xSurface = mutableMapOf<Triple<Int, Int, Int>, Int>()
    val ySurface = mutableMapOf<Triple<Int, Int, Int>, Int>()
    val zSurface = mutableMapOf<Triple<Int, Int, Int>, Int>()

    for (cube in droplet) {
        xSurface[Triple(cube.x, cube.y, cube.z)] = xSurface.getOrDefault(Triple(cube.x, cube.y, cube.z), 0) + 1
        xSurface[Triple(cube.x + 1, cube.y, cube.z)] = xSurface.getOrDefault(Triple(cube.x + 1, cube.y, cube.z), 0) + 1
        ySurface[Triple(cube.x, cube.y, cube.z)] = ySurface.getOrDefault(Triple(cube.x, cube.y, cube.z), 0) + 1
        ySurface[Triple(cube.x, cube.y + 1, cube.z)] = ySurface.getOrDefault(Triple(cube.x, cube.y + 1, cube.z), 0) + 1
        zSurface[Triple(cube.x, cube.y, cube.z)] = zSurface.getOrDefault(Triple(cube.x, cube.y, cube.z), 0) + 1
        zSurface[Triple(cube.x, cube.y, cube.z + 1)] = zSurface.getOrDefault(Triple(cube.x, cube.y, cube.z + 1), 0) + 1
    }

    return xSurface.count { it.value == 1 } + ySurface.count { it.value == 1 } + zSurface.count { it.value == 1 }
}

private fun part1(input: List<String>): Int {
    val droplet = droplet(input)

    return surfaceArea(droplet)
}

private class NeighbourCubeGenerator(
    private val xRange: IntRange,
    private val yRange: IntRange,
    private val zRange: IntRange
) {
    private val neighbourOffsets = listOf<Triple<Int, Int, Int>>(
        Triple(1, 0, 0),
        Triple(-1, 0, 0),
        Triple(0, 1, 0),
        Triple(0, -1, 0),
        Triple(0, 0, 1),
        Triple(0, 0, -1)
    )

    fun generate(cube: Cube): List<Cube> {
        val newCubes = mutableListOf<Cube>()
        for (offset in neighbourOffsets) {
            val newCube = Cube(cube.x + offset.first, cube.y + offset.second, cube.z + offset.third)
            if (xRange.contains(newCube.x) && yRange.contains(newCube.y) && zRange.contains(newCube.z)) {
                newCubes.add(newCube)
            }
        }
        return newCubes
    }
}

private fun part2(input: List<String>): Int {
    val droplet = droplet(input)

    // boundary is 1 unit more than the edge of the droplet, so we can flood the outside
    val boundaryMinX = droplet.minOf { it.x } - 1
    val boundaryMaxX = droplet.maxOf { it.x } + 1
    val boundaryMinY = droplet.minOf { it.y } - 1
    val boundaryMaxY = droplet.maxOf { it.y } + 1
    val boundaryMinZ = droplet.minOf { it.z } - 1
    val boundaryMaxZ = droplet.maxOf { it.z } + 1
    val neighbourCubeGenerator = NeighbourCubeGenerator(
        boundaryMinX..boundaryMaxX,
        boundaryMinY..boundaryMaxY,
        boundaryMinZ..boundaryMaxZ,
    )

    val stackToFlood = mutableSetOf(Cube(boundaryMinX, boundaryMinY, boundaryMinZ))
    val floodedPositions = mutableSetOf<Cube>()

    var count = 0
    do {
        val cube = stackToFlood.elementAt(0)
        stackToFlood.remove(cube)
        if (!droplet.contains(cube)) {
            floodedPositions.add(cube)

            for (neighbourCube in neighbourCubeGenerator.generate(cube)) {
                if (!floodedPositions.contains(neighbourCube) && !stackToFlood.contains(neighbourCube)) {
                    stackToFlood.add(neighbourCube)
                }
            }
        }
        count++
    } while (stackToFlood.isNotEmpty())

    val allCubes = mutableSetOf<Cube>()
    (boundaryMinX..boundaryMaxX).forEach { x ->
        (boundaryMinY..boundaryMaxY).forEach { y ->
            (boundaryMinZ..boundaryMaxZ).forEach { z ->
                allCubes.add(Cube(x, y, z))
            }
        }
    }
    val innerCubes = allCubes - floodedPositions - droplet

    return surfaceArea(droplet + innerCubes)
}

fun main() {
    val input = readInput("Day18")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
