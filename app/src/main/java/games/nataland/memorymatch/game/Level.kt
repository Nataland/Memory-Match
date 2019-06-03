package games.nataland.memorymatch.game

import games.nataland.memorymatch.utils.getRandoms

interface ILevel {
    val level: Int
    val isSpecialLevel: Boolean
    val gridSize: Int
    val numSpecialCells: Int
    val specials: List<Int>
    fun newBoard(): List<Cell>
    fun levelUp(): ILevel
}

data class Level(override val level: Int = 1) : ILevel {

    override val isSpecialLevel = level % 5 == 0

    override val gridSize = when (level) {
        in 1..13 -> 5
        in 14..29 -> 6
        in 30..48 -> 7
        else -> 8
    }

    override val numSpecialCells = when (level) {
        in 1..13 -> level + 2
        in 14..29 -> level - 7
        in 30..48 -> level - 21
        else -> if (level - 38 > 50) 50 else level - 38
    } / if (isSpecialLevel) 2 else 1

    override val specials = (gridSize * gridSize).getRandoms(numSpecialCells)

    override fun newBoard() = BooleanArray(gridSize * gridSize) { i -> (i in specials) }.map { Cell(it, false) }

    override fun levelUp() = Level(level + 1)
}