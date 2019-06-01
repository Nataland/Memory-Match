package games.nataland.memorymatch.game

import games.nataland.memorymatch.utils.getRandoms

data class Level(val level: Int = 0) {

    val isSpecialLevel = (level + 1) % 5 == 0

    val gridSize = when (level) {
        in 0..12 -> 5
        in 13..28 -> 6
        in 29..47 -> 7
        else -> 8
    }

    val numCellsToRemember = when (level) {
        in 0..12 -> level + 3
        in 13..28 -> level - 6
        in 29..47 -> level - 20
        else -> if (level - 37 > 50) 50 else level - 37
    } / if (isSpecialLevel) 2 else 1

    val specials = (gridSize * gridSize).getRandoms(numCellsToRemember)

    fun newBoard() = BooleanArray(gridSize * gridSize) { i -> (i in specials) }.map { Cell(it, false) }

    fun levelUp() = Level(level + 1)
}