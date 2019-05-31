package games.nataland.memorymatch.game

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import com.groupon.grox.rxjava2.RxStores
import com.jakewharton.rxbinding2.view.RxView
import dagger.Component
import dagger.Module
import dagger.Provides
import games.nataland.memorymatch.R
import games.nataland.memorymatch.app.MyApplication
import games.nataland.memorymatch.delay
import games.nataland.memorymatch.dp
import games.nataland.memorymatch.info.InfoActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import javax.inject.Singleton

private const val TOTAL_LIFE_NUM = 5
private const val HIGH_SCORE_KEY = "HIGH_SCORE_KEY"

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var boardState: BoardStateStore

    private val compositeDisposable = CompositeDisposable()
    private lateinit var sharedPrefs: SharedPreferences

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

        compositeDisposable.add(
                RxView.clicks(start_button)
                        .map { Level() }
                        .subscribe(this::startGame, this::doLog)
        )

        compositeDisposable.add(
                RxView.clicks(info)
                        .subscribe(
                                { startActivity(Intent(this, InfoActivity::class.java)) },
                                this::doLog
                        )
        )

        sharedPrefs = getSharedPreferences("games.nataland.memorymatch", Context.MODE_PRIVATE)
        updateHighScore()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    private fun updateUI(state: BoardState) {
        configureBoardSize(state.level.gridSize())

        if (state.isFresh) {

            initializeBoard(state.level.gridSize() * state.level.gridSize())
            initializeLife(state.remainingLife)
            initializeLevel(state.level.level)

            if (!state.isPlaying) {
                start_button.visibility = View.VISIBLE
                level.visibility = View.GONE
                level_up.visibility = View.GONE
                life.visibility = View.GONE
                return
            }

            for ((index, cell) in state.board.withIndex()) {
                val child = (board.getChildAt(index) as ImageView)
                if (cell.isSpecial) child.setImageDrawable(getDrawable(R.drawable.right_box))
            }

            delay(900) {
                if (state.level.isSpecialLevel()) {
                    // todo: show special level hint (Special level! gain an extra life if you get the order right)
                    // todo: show the cells in order
                } else {
                    // todo: show cells all together
                }

                state.board
                        .mapIndexed { index, _ -> (board.getChildAt(index) as ImageView) }
                        .forEach { it.setImageDrawable(getDrawable(R.drawable.default_box)) }

                boardState.hintShown()
                board.isClickable = true
            }
            return
        }

        updateColors(state.board)

        if (state.cellPos != -1 && !state.board[state.cellPos].isFound) {
            val newBoard = state.board.mapIndexed { index, cell ->
                Cell(cell.isSpecial, if (index == state.cellPos) true else cell.isFound)
            }
            val isCellSpecial = state.board[state.cellPos].isSpecial
            if (!isCellSpecial && life.childCount != 0) {
                life.removeViewAt(0)
            }
            val newTotalCellsFound = state.totalCellsFound + (if (isCellSpecial) 1 else 0)
            val newLifeCount = state.remainingLife - (if (isCellSpecial) 0 else 1)
            boardState.updateBoard(newBoard, newTotalCellsFound, newLifeCount)
        }

        // Level up
        if (state.totalCellsFound == state.level.numCellsToRemember()) {
            board.isClickable = false
            level_up.visibility = View.VISIBLE
            delay {
                level_up.visibility = View.GONE
                boardState.newLevel(state.level.levelUp(), state.remainingLife)
            }
        }

        // Game over
        if (state.remainingLife == 0) {
            board.isClickable = false
            game_over.visibility = View.VISIBLE
            val highScore = sharedPrefs.getInt(HIGH_SCORE_KEY, 0)
            if (state.level.level > highScore) {
                sharedPrefs.edit().putInt(HIGH_SCORE_KEY, state.level.level).apply()
                updateHighScore()
            }
            delay {
                game_over.visibility = View.GONE
                boardState.newLevel(Level(0), TOTAL_LIFE_NUM, true)
            }
        }
    }

    private fun startGame(level: Level) {
        boardState.newLevel(level, TOTAL_LIFE_NUM)
        start_button.visibility = View.GONE
    }

    private fun configureBoardSize(size: Int) {
        board.columnCount = size
        board.rowCount = size
    }

    private fun initializeLevel(currentLevel: Int) {
        level.visibility = View.VISIBLE
        level.text = String.format(getString(R.string.level), currentLevel + 1)
    }

    private fun initializeLife(lifeCount: Int) {
        life.visibility = View.VISIBLE
        life.removeAllViews()

        repeat(lifeCount) {
            life.addView(
                    ImageView(this).apply {
                        setImageDrawable(getDrawable(R.drawable.life_heart))
                        layoutParams = GridLayout.LayoutParams().apply {
                            setMargins(4.dp(resources), 4.dp(resources), 4.dp(resources), 4.dp(resources))
                        }
                    }
            )
        }
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
                    setMargins(4.dp(resources), 4.dp(resources), 4.dp(resources), 4.dp(resources))
                    width = 0
                    height = 0
                }
            })
        }

        compositeDisposable.add(RxView.touches(board)
                .filter { it.actionMasked == MotionEvent.ACTION_DOWN && board.isClickable }
                .map { event -> getCell(event) }
                .distinctUntilChanged()
                .subscribe(boardState::cellClicked, this::doLog)
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

    private fun updateHighScore() {
        high_score.text = String.format(getString(R.string.high_score), sharedPrefs.getInt(HIGH_SCORE_KEY, 0) + 1)
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