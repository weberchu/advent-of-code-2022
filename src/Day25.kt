import java.lang.Math.pow

private fun decimalToSnafu(dec: Long): String {
    val quinary = dec.toString(5)
    val snafuReversed = mutableListOf<Char>()
    var hasCarry = false
    for (c in quinary.reversed()) {
        val decValue = c.digitToInt(10) + if (hasCarry) 1 else 0
        when (decValue) {
            0 -> {
                hasCarry = false
                snafuReversed.add('0')
            }
            1 -> {
                hasCarry = false
                snafuReversed.add('1')
            }
            2 -> {
                hasCarry = false
                snafuReversed.add('2')
            }
            3 -> {
                hasCarry = true
                snafuReversed.add('=')
            }
            4 -> {
                hasCarry = true
                snafuReversed.add('-')
            }
            5 -> {
                hasCarry = true
                snafuReversed.add('0')
            }
            else -> throw IllegalStateException("Not expecting $decValue")
        }
    }

    if (hasCarry) {
        snafuReversed.add('1')
    }

    return String(snafuReversed.reversed().toCharArray())
}

private fun snafuCharToDecimal(char: Char): Long {
    return when (char) {
        '0' -> 0
        '1' -> 1
        '2' -> 2
        '-' -> -1
        '=' -> -2
        else -> throw IllegalArgumentException("Unknown SNAFU char $char")
    }
}

private fun snafuToDecimal(snafu: String): Long {
    return snafu.mapIndexed { index, c ->
        val power = snafu.length - index - 1
        pow(5.0, power.toDouble()).toLong() * snafuCharToDecimal(c)
    }.sum()
}

private fun part1(input: List<String>): String {
    val decimalSum = input.map { snafuToDecimal(it) }.sum()
    return decimalToSnafu(decimalSum)
}

private fun part2(input: List<String>): Int {
    return input.size
}

fun main() {
    val input = readInput("Day25")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
