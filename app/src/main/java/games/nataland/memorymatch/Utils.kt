package games.nataland.memorymatch

import android.content.res.Resources
import android.os.Handler
import java.util.*
import android.util.TypedValue


fun Int.getRandoms(count: Int): List<Int> {
    val arr = (IntArray(this) { i -> i }).toList()
    Collections.shuffle(arr)
    return arr.take(count)
}

fun Int.dp(r: Resources) = this.toFloat().dp(r)

fun Float.dp(r: Resources) = Math.round(TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this, r.displayMetrics)
)

fun delay(timeDelayed: Long = 1000, action: () -> Unit) {
    val hideHint = Runnable {
        action.invoke()
    }

    val h = Handler()
    h.postDelayed(hideHint, timeDelayed)
}