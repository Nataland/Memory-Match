package games.nataland.memorymatch

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import dagger.Component
import dagger.Module
import dagger.Provides
import games.nataland.memorymatch.app.MyApplication
import games.nataland.memorymatch.game.BoardStateStore
import javax.inject.Inject
import javax.inject.Singleton

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var boardState: BoardStateStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as MyApplication)
                .myComponent
                .inject(this@MainActivity)
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