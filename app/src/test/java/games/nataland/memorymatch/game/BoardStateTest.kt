package games.nataland.memorymatch.game

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BoardStateTest {

    private val level = MockLevel()

    @Test
    fun `Cells are found in order`() {
        val board = BoardState(totalCellsFound = listOf(1, 2, 3), level = level)

        assertTrue(board.isFoundInOrder())
    }

    @Test
    fun `The correct cells are found but not in order`() {
        val board = BoardState(totalCellsFound = listOf(3, 2, 1), level = level)

        assertFalse(board.isFoundInOrder())
    }

    @Test
    fun `Wrong cells`() {
        val board = BoardState(totalCellsFound = listOf(13, 14), level = level)

        assertFalse(board.isFoundInOrder())
    }
}