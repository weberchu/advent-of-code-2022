private enum class Shape(val score: Int) {
    Rock(1), Paper(2), Scissors(3)
}

private enum class Result(val score: Int) {
    Win(6), Draw(3), Lose(0)
}

private fun toOpponentShape(shape: String): Shape {
    return when (shape) {
        "A" -> Shape.Rock
        "B" -> Shape.Paper
        "C" -> Shape.Scissors
        else -> throw IllegalArgumentException("Unknown shape $shape")
    }
}

private fun toPlayerShape(shape: String): Shape {
    return when (shape) {
        "X" -> Shape.Rock
        "Y" -> Shape.Paper
        "Z" -> Shape.Scissors
        else -> throw IllegalArgumentException("Unknown shape $shape")
    }
}

private fun toResult(result: String): Result {
    return when (result) {
        "X" -> Result.Lose
        "Y" -> Result.Draw
        "Z" -> Result.Win
        else -> throw IllegalArgumentException("Unknown result $result")
    }
}

private fun play(playerShape: Shape, opponentShape: Shape): Result {
    return if (playerShape == opponentShape) {
        Result.Draw
    } else if (
        (playerShape == Shape.Rock && opponentShape == Shape.Scissors)
        || (playerShape == Shape.Scissors && opponentShape == Shape.Paper)
        || (playerShape == Shape.Paper && opponentShape == Shape.Rock)
    ) {
        Result.Win
    } else {
        Result.Lose
    }
}

private fun iShouldPlayWhen(opponentShape: Shape, result: Result): Shape {
    return when (result) {
        Result.Draw -> opponentShape
        Result.Win -> {
            when (opponentShape) {
                Shape.Rock -> Shape.Paper
                Shape.Paper -> Shape.Scissors
                Shape.Scissors -> Shape.Rock
            }
        }
        else -> {
            when (opponentShape) {
                Shape.Rock -> Shape.Scissors
                Shape.Paper -> Shape.Rock
                Shape.Scissors -> Shape.Paper
            }
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        var totalScore = 0

        for (line in input) {
            val shapes = line.split(" ")
            val opponentShape = toOpponentShape(shapes[0])
            val playerShape = toPlayerShape(shapes[1])

            totalScore += playerShape.score + play(playerShape, opponentShape).score
        }

        return totalScore
    }

    fun part2(input: List<String>): Int {
        var totalScore = 0

        for (line in input) {
            val shapes = line.split(" ")
            val opponentShape = toOpponentShape(shapes[0])
            val result = toResult(shapes[1])
            val playerShape = iShouldPlayWhen(opponentShape, result)

            totalScore += playerShape.score + result.score
        }

        return totalScore
    }

    val input = readInput("Day02")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
