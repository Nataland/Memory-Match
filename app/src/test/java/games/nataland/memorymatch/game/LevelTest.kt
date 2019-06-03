package games.nataland.memorymatch.game

import org.hamcrest.CoreMatchers
import org.junit.Assert.*
import org.junit.Test

class LevelTest {

    @Test
    fun `Levels that are multiples of 5 are special levels`() {
        assertTrue(Level(5).isSpecialLevel)
        assertTrue(Level(20).isSpecialLevel)
    }

    @Test
    fun `Levels that are not multiples of 5 are not special levels`() {
        assertFalse(Level(1).isSpecialLevel)
        assertFalse(Level(11).isSpecialLevel)
    }

    @Test
    fun `The grid is 5x5 for levels 1 to 13`() {
        assertThat(Level(1).gridSize, CoreMatchers.equalTo(5))
        assertThat(Level(2).gridSize, CoreMatchers.equalTo(5))
        assertThat(Level(13).gridSize, CoreMatchers.equalTo(5))
    }

    @Test
    fun `The grid is 6x6 for levels 14 to 29`() {
        assertThat(Level(14).gridSize, CoreMatchers.equalTo(6))
        assertThat(Level(28).gridSize, CoreMatchers.equalTo(6))
        assertThat(Level(29).gridSize, CoreMatchers.equalTo(6))
    }

    @Test
    fun `The grid is 7x7 for levels 30 to 48`() {
        assertThat(Level(30).gridSize, CoreMatchers.equalTo(7))
        assertThat(Level(40).gridSize, CoreMatchers.equalTo(7))
        assertThat(Level(48).gridSize, CoreMatchers.equalTo(7))
    }

    @Test
    fun `The grid is 8x8 for levels 49 and above`() {
        assertThat(Level(49).gridSize, CoreMatchers.equalTo(8))
        assertThat(Level(99).gridSize, CoreMatchers.equalTo(8))
    }

    @Test
    fun `The number of special cells is correct for levels 1 to 13`() {
        assertThat(Level(1).numSpecialCells, CoreMatchers.equalTo(3))
        assertThat(Level(5).numSpecialCells, CoreMatchers.equalTo(3))
        assertThat(Level(13).numSpecialCells, CoreMatchers.equalTo(15))
    }

    @Test
    fun `The number of special cells is correct for levels 14 to 29`() {
        assertThat(Level(14).numSpecialCells, CoreMatchers.equalTo(7))
        assertThat(Level(20).numSpecialCells, CoreMatchers.equalTo(6))
        assertThat(Level(29).numSpecialCells, CoreMatchers.equalTo(22))
    }

    @Test
    fun `The number of special cells is correct for levels 30 to 48`() {
        assertThat(Level(30).numSpecialCells, CoreMatchers.equalTo(4))
        assertThat(Level(31).numSpecialCells, CoreMatchers.equalTo(10))
        assertThat(Level(48).numSpecialCells, CoreMatchers.equalTo(27))
    }

    @Test
    fun `The number of special cells is correct for levels 49 and above`() {
        assertThat(Level(49).numSpecialCells, CoreMatchers.equalTo(11))
        assertThat(Level(100).numSpecialCells, CoreMatchers.equalTo(25))
        assertThat(Level(101).numSpecialCells, CoreMatchers.equalTo(50))
    }
}