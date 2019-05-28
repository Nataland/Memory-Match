package games.nataland.memorymatch.game

import java.util.*


fun Int.getRandoms(count: Int): List<Int> {
    val arr = (IntArray(this) { i -> i}).toList()
    Collections.shuffle(arr)
    return arr.take(count)
}