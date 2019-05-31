package games.nataland.memorymatch.app

import android.app.Application
import games.nataland.memorymatch.game.DaggerGameComponent
import games.nataland.memorymatch.game.GameComponent
import games.nataland.memorymatch.game.GameModule

class MyApplication : Application() {
    lateinit var myComponent: GameComponent

    override fun onCreate() {
        super.onCreate()
        myComponent = createGameComponent()
    }

    private fun createGameComponent(): GameComponent {
        return DaggerGameComponent.builder()
                .gameModule(GameModule())
                .build()
    }

}