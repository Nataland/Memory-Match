package games.nataland.memorymatch

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.widget.GridLayout
import android.widget.ImageView
import com.groupon.grox.rxjava2.RxStores
import com.jakewharton.rxbinding2.view.RxView
import dagger.Component
import dagger.Module
import dagger.Provides
import games.nataland.memorymatch.app.MyApplication
import games.nataland.memorymatch.game.BoardState
import games.nataland.memorymatch.game.BoardStateStore
import games.nataland.memorymatch.game.Cell
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import javax.inject.Singleton

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var boardState: BoardStateStore

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as MyApplication)
                .myComponent
                .inject(this@MainActivity)

        compositeDisposable.add(
                RxStores.states(boardState.store)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::updateUI, this::doLog)
        )
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    private fun updateUI(state: BoardState) {
        configureBoardSize(state.level.gridSize())

        if (state.isFresh) {
            initializeBoard(state.level.gridSize() * state.level.gridSize())

            for ((index, cell) in state.board.withIndex()) {
                val child = (board.getChildAt(index) as ImageView)
                if (cell.isSpecial) child.setImageDrawable(getDrawable(R.drawable.right_box))
            }

            delay {
                state.board
                        .mapIndexed { index, _ -> (board.getChildAt(index) as ImageView) }
                        .forEach { it.setImageDrawable(getDrawable(R.drawable.default_box)) }
                boardState.hintShown()
                board.isClickable = true
            }
        } else {
            updateColors(state.board)
        }

        if (state.cellPos != -1 && !state.board[state.cellPos].isFound) {
            val newBoard = state.board.mapIndexed { index, cell ->
                Cell(cell.isSpecial, if (index == state.cellPos) true else cell.isFound)
            }
            boardState.updateBoard(newBoard, state.totalCellsFound + (if (state.board[state.cellPos].isSpecial) 1 else 0))
        }

        if (state.totalCellsFound == state.level.numCellsToRemember()) {
            board.isClickable = false
            delay(100) {
                boardState.newLevel(state.level.levelUp())
            }
        }
    }

    private fun configureBoardSize(size: Int) {
        board.columnCount = size
        board.rowCount = size
    }

    private fun initializeBoard(cellsNum: Int) {
        board.removeAllViews()
        board.isClickable = false

        for (count in 0 until cellsNum) {
            board.addView(ImageView(this).apply {
                setImageDrawable(getDrawable(R.drawable.default_box))
                scaleType = ImageView.ScaleType.FIT_XY
                layoutParams = GridLayout.LayoutParams(
                        GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1.0F),
                        GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1.0F)
                ).apply {
                    setMargins(10, 10, 10, 10)
                    width = 0
                    height = 0
                }
            })
        }

        compositeDisposable.add(RxView.touches(board)
                .filter { it.actionMasked == MotionEvent.ACTION_DOWN && board.isClickable}
                .map { event -> getCell(event) }
                .distinctUntilChanged()
                .subscribe(boardState::cellClicked)
        )
    }

    private fun updateColors(cells: List<Cell>) {
        for ((index, cell) in cells.withIndex()) {
            when {
                cell.isFound && cell.isSpecial -> (board.getChildAt(index) as ImageView).setImageDrawable(getDrawable(R.drawable.right_box))
                cell.isFound && !cell.isSpecial -> (board.getChildAt(index) as ImageView).setImageDrawable(getDrawable(R.drawable.wrong_box))
                else -> (board.getChildAt(index) as ImageView).setImageDrawable(getDrawable(R.drawable.default_box))
            }
        }
    }

    private fun getCell(event: MotionEvent): Int {
        for (index in 0 until board.childCount) {
            val cell = board.getChildAt(index)
            if (event.x <= cell.right
                    && event.x >= cell.left
                    && event.y >= cell.top
                    && event.y <= cell.bottom) {
                return index
            }
        }
        return -1
    }

    private fun doLog(throwable: Throwable) {
        Log.d("Grox", "An error occurred in a Grox chain.", throwable)
    }

    private fun delay(timeDelayed: Long = 1000, action: () -> Unit) {
        val hideHint = Runnable {
            action.invoke()
        }

        val h = Handler()
        h.postDelayed(hideHint, timeDelayed)
    }
}

@Module
internal class GameModule {

    @Provides
    @Singleton
    fun provideState(): BoardStateStore {
        return BoardStateStore()
    }
}

@Singleton
@Component(modules = arrayOf(GameModule::class))
interface GameComponent {

    fun inject(mainActivity: MainActivity)
}