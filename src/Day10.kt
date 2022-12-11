private fun part1(input: List<String>): Int {
    var x = 1
    var currentCycle = 0
    val measureCycle = ArrayDeque(listOf(20, 60, 100, 140, 180, 220))
    var signalStrength = 0

    for (line in input) {
        val instruction = line.split(" ")

        when (instruction[0]) {
            "noop" -> currentCycle++
            "addx" -> currentCycle += 2
            else -> throw IllegalArgumentException("Unknown instruction $line")
        }

        if (currentCycle >= measureCycle.first()) {
            signalStrength += measureCycle.first() * x
            measureCycle.removeFirst()

            if (measureCycle.isEmpty()) {
                break
            }
        }

        if (instruction[0] == "addx") {
            x += instruction[1].toInt()
        }
    }

    return signalStrength
}

private class CRT {
    var cycle = 0
    var lines = mutableListOf<String>()

    fun draw(spritePosition: Int) {
        val row = cycle / 40
        val column = cycle % 40

        if (column  == 0) {
            lines.add("")
        }

        val pixel = if (column >= spritePosition - 1 && column <= spritePosition + 1) '#' else '.'
        lines[row] = lines[row] + pixel

        cycle++
    }

    fun print() {
        for (line in lines) {
            println(line)
        }
    }
}

private fun part2(input: List<String>) {
    var x = 1
    val crt = CRT()

    for (line in input) {
        val instruction = line.split(" ")

        when (instruction[0]) {
            "noop" -> crt.draw(x)
            "addx" -> {
                crt.draw(x)
                crt.draw(x)
            }
            else -> throw IllegalArgumentException("Unknown instruction $line")
        }

        if (instruction[0] == "addx") {
            x += instruction[1].toInt()
        }
    }

    crt.print()
}

fun main() {
    val input = readInput("Day10")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: ")
    part2(input)
}
