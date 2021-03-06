package games.nataland.memorymatch.game

import com.groupon.grox.Store

class BoardStateStore {
    val store = Store(BoardState())

    fun newLevel(newLevel: ILevel, newRemainingLife: Int, newGame: Boolean = false) {
        store.dispatch {
            BoardState(
                    level = newLevel,
                    board = newLevel.newBoard(),
                    isPlaying = !newGame,
                    remainingLife = newRemainingLife
            )
        }
    }

    fun cellClicked(cellPos: Int) {
        store.dispatch { oldState -> oldState.copy(cellPos = cellPos) }
    }

    fun updateBoard(newBoard: List<Cell>, newTotalCellsFound: List<Int>, newLifeCount: Int) {
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