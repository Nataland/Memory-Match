package games.nataland.memorymatch.game

import com.groupon.grox.Store

class BoardStateStore {
    val store = Store(BoardState())

    fun newLevel(newLevel: Level, newRemainingLife: Int) {
        store.dispatch {
            BoardState(
                    level = newLevel,
                    board = newLevel.newBoard(),
                    isPlaying = true,
                    remainingLife = newRemainingLife
            )
        }
    }

    fun cellClicked(cellPos: Int) {
        store.dispatch { oldState -> oldState.copy(cellPos = cellPos) }
    }

    fun updateBoard(newBoard: List<Cell>, newTotalCellsFound: Int, newLifeCount: Int) {
        store.dispatch { oldState ->
            oldState.copy(
                    board = newBoard,
                    cellPos = -1,
                    totalCellsFound = newTotalCellsFound,
                    remainingLife = newLifeCount
            )
        }
    }

    fun hintShown() {
        store.dispatch { oldState -> oldState.copy(isFresh = false) }
    }
}

data class BoardState(
        val level: Level = Level(0),
        val board: List<Cell> = level.newBoard(),
        val cellPos: Int = -1,
        val isFresh: Boolean = true,
        val totalCellsFound: Int = 0,
        val isPlaying: Boolean = false,
        val remainingLife: Int = 0
)