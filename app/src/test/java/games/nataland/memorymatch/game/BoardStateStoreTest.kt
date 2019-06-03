package games.nataland.memorymatch.game

import com.groupon.grox.rxjava2.RxStores.states
import org.junit.Test

class BoardStateStoreTest {

    @Test
    fun `Calling newLevel updates the current level`() {
        val store = BoardStateStore().apply {
            newLevel(MockLevel(6), 4, false)
        }

        val observer = states(store.store).test()

        observer.assertValue { it.level == MockLevel(6) }
    }

    @Test
    fun `Calling newLevel updates the current board`() {
        val store = BoardStateStore().apply {
            newLevel(MockLevel(), 4, false)
        }

        val observer = states(store.store).test()

        observer.assertValue { it.board == listOf(Cell(false, false), Cell(true, true)) }
    }

    @Test
    fun `Calling newLevel updates the number of remaining lives`() {
        val store = BoardStateStore().apply {
            newLevel(MockLevel(), 4, false)
        }

        val observer = states(store.store).test()

        observer.assertValue { it.remainingLife == 4 }
    }

    @Test
    fun `Calling newLevel updates if it is currently a new game`() {
        val store = BoardStateStore().apply {
            newLevel(MockLevel(), 4, false)
        }

        val observer = states(store.store).test()

        observer.assertValue { it.isPlaying }
    }

    @Test
    fun `Calling cellClicked updates the current cell position`() {
        val store = BoardStateStore().apply {
            cellClicked(20)
        }

        val observer = states(store.store).test()

        observer.assertValue { it.cellPos == 20 }
    }

    @Test
    fun `Calling updateBoard updates the board to newBoard`() {
        val store = BoardStateStore().apply {
            updateBoard(listOf(Cell(true, true)), listOf(1), 2)
        }

        val observer = states(store.store).test()

        observer.assertValue { it.board == listOf(Cell(true, true)) }
    }

    @Test
    fun `Calling updateBoard updates the number of remaining lives`() {
        val store = BoardStateStore().apply {
            updateBoard(listOf(Cell(true, true)), listOf(1), 2)
        }

        val observer = states(store.store).test()

        observer.assertValue { it.remainingLife == 2 }
    }

    @Test
    fun `Calling updateBoard updates the list of total cells found`() {
        val store = BoardStateStore().apply {
            updateBoard(listOf(Cell(true, true)), listOf(1), 2)
        }

        val observer = states(store.store).test()

        observer.assertValue { it.totalCellsFound == listOf(1) }
    }

    @Test
    fun `Calling hintShown sets isFresh to false`() {
        val store = BoardStateStore().apply {
            hintShown()
        }

        val observer = states(store.store).test()

        observer.assertValue { !it.isFresh }
    }
}