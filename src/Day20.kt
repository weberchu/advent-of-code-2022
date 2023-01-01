private fun mix(originalFile: List<Pair<Long, Int>>, mixedFile: MutableList<Pair<Long, Int>>): MutableList<Pair<Long, Int>> {
    val fileSizeMinusOne = originalFile.size - 1

    for (nextNum in originalFile) {
        val index = mixedFile.indexOf(nextNum)
        mixedFile.removeAt(index)

        var newIndex = index + nextNum.first
        if (newIndex > fileSizeMinusOne) {
            newIndex %= fileSizeMinusOne
        } else if (newIndex < 0) {
            newIndex = newIndex % fileSizeMinusOne + fileSizeMinusOne
        }
        mixedFile.add(newIndex.toInt(), nextNum)
    }

    return mixedFile
}

private fun part1(input: List<String>): Long {
    val originalFile = input.mapIndexed { i, num -> Pair(num.toLong(), i) }

    val mixedFile = mix(originalFile, originalFile.toMutableList())

    val indexZero = mixedFile.indexOfFirst { it.first == 0L }
    val index1000 = (indexZero + 1000) % originalFile.size
    val index2000 = (indexZero + 2000) % originalFile.size
    val index3000 = (indexZero + 3000) % originalFile.size

    return mixedFile[index1000].first + mixedFile[index2000].first + mixedFile[index3000].first
}

private fun part2(input: List<String>): Long {
    val originalFile = input
        .map { it.toLong() * 811589153 }
        .mapIndexed { i, num -> Pair(num, i) }

    var mixedFile = originalFile.toMutableList()
    for (i in 1..10) {
        mixedFile = mix(originalFile, mixedFile)
    }

    val indexZero = mixedFile.indexOfFirst { it.first == 0L }
    val index1000 = (indexZero + 1000) % originalFile.size
    val index2000 = (indexZero + 2000) % originalFile.size
    val index3000 = (indexZero + 3000) % originalFile.size

    return mixedFile[index1000].first + mixedFile[index2000].first + mixedFile[index3000].first
}

fun main() {
    val input = readInput("Day20")
//    val input = readInput("Test")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
