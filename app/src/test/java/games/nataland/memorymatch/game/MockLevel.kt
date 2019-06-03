package games.nataland.memorymatch.game

data class MockLevel(override val level: Int = 1) : ILevel {

    override val isSpecialLevel = level % 5 == 0

    override val gridSize = 5

    override val numSpecialCells = 3

    override val specials = listOf(1, 2, 3)

    override fun newBoard() = listOf(Cell(false, false), Cell(true, true))

    override fun levelUp() = MockLevel(level + 1)
}