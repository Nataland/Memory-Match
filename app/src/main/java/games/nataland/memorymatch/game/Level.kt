package games.nataland.memorymatch.game

import games.nataland.memorymatch.getRandoms

data class Level(val level: Int = 0) {

    fun gridSize() = when (level) {
        in 0..12 -> 5
        in 13..28 -> 6
        in 29..47 -> 7
        else -> 8
    }

    fun numCellsToRemember() = when (level) {
        in 0..12 -> level + 3
        in 13..28 -> level - 6
        in 29..47 -> level - 20
        else -> if (level - 37 > 50) 50 else level - 37
    }

    fun newBoard(): List<Cell> {
        val numCells = gridSize() * gridSize()
        val specials = numCells.getRandoms(numCellsToRemember())
        return BooleanArray(numCells) { i -> (i in specials) }.map { Cell(it, false) }
    }

    fun levelUp() = Level(level + 1)
}