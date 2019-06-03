package games.nataland.memorymatch.game

data class BoardState(
        val level: ILevel = Level(1),
        val board: List<Cell> = level.newBoard(),
        val cellPos: Int = -1,
        val isFresh: Boolean = true,
        val totalCellsFound: List<Int> = listOf(),
        val isPlaying: Boolean = false,
        val remainingLife: Int = 0
) {

    fun isFoundInOrder() = totalCellsFound
            .toIntArray()
            .contentEquals(level.specials.toIntArray())
}