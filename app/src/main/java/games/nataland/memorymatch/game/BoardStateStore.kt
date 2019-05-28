package games.nataland.memorymatch.game

import com.groupon.grox.Store

class BoardStateStore {
    val store = Store(BoardState())

    fun newGame() = newBoard(Level())

    fun newBoard(newLevel: Level) {
        store.dispatch {
            BoardState(
                    level = newLevel,
                    board = newLevel.newBoard()
            )
        }
    }
}

data class BoardState(
        val level: Level = Level(0),
        val board: List<Cell> = emptyList()
)