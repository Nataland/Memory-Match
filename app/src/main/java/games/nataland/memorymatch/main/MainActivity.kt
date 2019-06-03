package games.nataland.memorymatch.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import com.groupon.grox.rxjava2.RxStores
import com.jakewharton.rxbinding2.view.RxView
import games.nataland.memorymatch.R
import games.nataland.memorymatch.app.MyApplication
import games.nataland.memorymatch.game.*
import games.nataland.memorymatch.utils.delay
import games.nataland.memorymatch.utils.dp
import games.nataland.memorymatch.info.InfoActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

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
        configureBoardSize(state.level.gridSize)

        if (state.isFresh) {
            newLevel(state)
            return
        }

        updateColors(state.board)

        if (state.cellPos != -1 && !state.board[state.cellPos].isFound) {
            val newBoard = state.board.mapIndexed { index, cell ->
                Cell(cell.isSpecial, if (index == state.cellPos) true else cell.isFound)
            }
            val isCellSpecial = state.board[state.cellPos].isSpecial
            if (!isCellSpecial && life.childCount != 0) life.removeViewAt(0)

            boardState.updateBoard(
                    newBoard,
                    if (isCellSpecial) state.totalCellsFound + state.cellPos else state.totalCellsFound,
                    state.remainingLife - (if (isCellSpecial) 0 else 1)
            )
        }

        // Level up
        if (state.totalCellsFound.size == state.level.numSpecialCells) {
            val hasBonusLife = state.level.isSpecialLevel && state.isFoundInOrder()
            board.isClickable = false
            level_up.visibility = View.VISIBLE
            bonus_life.visibility = if (hasBonusLife) View.VISIBLE else View.GONE
            val life = state.remainingLife + if (hasBonusLife) 1 else 0
            Handler().delay {
                level_up.visibility = View.GONE
                boardState.newLevel(state.level.levelUp(), if (life > 10) 10 else life)
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
            Handler().delay {
                game_over.visibility = View.GONE
                boardState.newLevel(Level(), TOTAL_LIFE_NUM, true)
            }
        }
    }

    private fun newLevel(state: BoardState) {
        initializeBoard(state.level.gridSize * state.level.gridSize)
        initializeLife(state.remainingLife)
        initializeLevel(state.level.level)

        if (!state.isPlaying) {
            start_button.visibility = View.VISIBLE
            level.visibility = View.GONE
            level_up.visibility = View.GONE
            life.visibility = View.GONE
            return
        }

        val handler = Handler()

        if (state.level.isSpecialLevel) {
            special_level.visibility = View.VISIBLE
            handler.delay { special_level.visibility = View.GONE }

            val specials = state.level.specials.map { board.getChildAt(it) as ImageView}
            for ((ind, grid) in specials.withIndex()) {
                handler.delay(1000 + 300 * (ind + 1)) {
                    grid.setImageDrawable(getDrawable(R.drawable.right_box))
                }
            }

            handler.delay(1900 + 300 * specials.size) { hindHints(state.board) }
        } else {
            for ((index, cell) in state.board.withIndex()) {
                val child = (board.getChildAt(index) as ImageView)
                if (cell.isSpecial) child.setImageDrawable(getDrawable(R.drawable.right_box))
            }

            handler.delay(900) { hindHints(state.board) }
        }
    }

    private fun hindHints(cells: List<Cell>) {
        cells.mapIndexed { index, _ -> (board.getChildAt(index) as ImageView) }
                .forEach { it.setImageDrawable(getDrawable(R.drawable.default_box)) }

        boardState.hintShown()
        board.isClickable = true
    }

    private fun startGame(level: ILevel) {
        boardState.newLevel(level, TOTAL_LIFE_NUM)
        start_button.visibility = View.GONE
    }

    private fun configureBoardSize(size: Int) {
        board.columnCount = size
        board.rowCount = size
    }

    private fun initializeLevel(currentLevel: Int) {
        level.visibility = View.VISIBLE
        level.text = String.format(getString(R.string.level), currentLevel)
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
        high_score.text = String.format(getString(R.string.high_score), sharedPrefs.getInt(HIGH_SCORE_KEY, 0))
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