package games.nataland.memorymatch.game

data class Level(val level: Int = 0) {

    fun gridSize() = when {
        level <= 10 -> 5
        level in 11..20 -> 6
        level in 21..30 -> 7
        else -> 8
    }

    fun newBoard(): List<Cell> {
        val numCells = gridSize()*gridSize()
        val numCellsToRemember = level + 8 - gridSize()
        val specials = numCells.getRandoms(numCellsToRemember)
        return BooleanArray(numCells) { i -> (i in specials)}.map { Cell(it, false) }
    }
}