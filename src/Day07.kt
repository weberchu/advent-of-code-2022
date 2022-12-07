private interface FileDescriptor {
    val name: String
}

private data class Directory(
    override val name: String,
    val parent: Directory?,
    val children: MutableMap<String, FileDescriptor>
): FileDescriptor

private data class File(
    override val name: String,
    val size: Long
): FileDescriptor

private class FileSystem {
    private val root = Directory("/", null, mutableMapOf())
    private var currentDir = root
    val currentPath = mutableListOf("/")

    fun cd(dir: String) {
        currentDir = when (dir) {
            "/" -> {
                currentPath.clear()
                currentPath.add("/")
                root
            }
            ".." -> {
                currentPath.removeLast()
                currentDir.parent!!
            }
            else -> {
                currentPath.add(dir)
                currentDir.children[dir]!! as Directory
            }
        }
    }

    fun createDir(name: String) {
        if (currentDir.children[name] == null) {
            currentDir.children[name] = Directory(name, currentDir, mutableMapOf())
        }
    }

    fun createFile(name: String, size: Long) {
        currentDir.children[name] = File(name, size)
    }

    fun get(path: List<String>): FileDescriptor {
        if (path[0] != "/") {
            throw IllegalArgumentException("Path must start with /")
        }

        var dir: FileDescriptor = root

        for (i in 1 until path.size) {
            dir = (dir as Directory).children[path[i]]!!
        }

        return dir
    }

    fun allDirSize(): Map<List<String>, Long> {
        val dirSize = mutableMapOf<List<String>, Long>()

        val pendingDir = mutableListOf(listOf("/"))

        while (pendingDir.isNotEmpty()) {
            val pathToProcess = pendingDir.removeLast()

            val dirToProcess = get(pathToProcess)
            if (dirToProcess is Directory) {
                val subDir = dirToProcess.children.values.filter {
                    it is Directory && !dirSize.containsKey(pathToProcess + it.name)
                }
                if (subDir.isEmpty()) {
                    // all children are either files or sub dir with known size
                    dirSize[pathToProcess] = dirToProcess.children.values.sumOf {
                        if (it is File) {
                            it.size
                        } else {
                            dirSize[pathToProcess + it.name]!!
                        }
                    }
                } else {
                    pendingDir.add(pathToProcess)
                    subDir.forEach {
                        pendingDir.add(pathToProcess + it.name)
                    }
                }
            }
        }

        return dirSize
    }
}

private fun createFileSystem(input: List<String>): FileSystem {
    val fs = FileSystem()

    for (line in input) {
        if (line.startsWith('$')) {
            val cmd = line.substring(2).split(" ")
            when (cmd[0]) {
                "cd" -> {
                    fs.cd(cmd[1])
                }
                "ls" -> {} // do nothing, will parse next line as list output
                else -> throw IllegalArgumentException("Unknown command $cmd")
            }
        } else {
            val list = line.split(" ")
            if (list[0] == "dir") {
                fs.createDir(list[1])
            } else {
                fs.createFile(list[1], list[0].toLong())
            }
        }
    }

    return fs
}

fun main() {
    val input = readInput("Day07")
//    val input = readInput("Test")

    val fs = createFileSystem(input)
    val allDirSize = fs.allDirSize()

    val part1 = allDirSize.values.filter { it <= 100000 }.sum()

    val currentFreeSpace = 70000000 - allDirSize[listOf("/")]!!
    val minSizeToFreeUp = 30000000 - currentFreeSpace

    var toBeDeletedDirSize = Long.MAX_VALUE
    allDirSize.forEach { (path, size) ->
        if (size > minSizeToFreeUp && size < toBeDeletedDirSize) {
            toBeDeletedDirSize = size
        }
    }

    println("Part 1: " + part1)
    println("Part 2: " + toBeDeletedDirSize)
}
