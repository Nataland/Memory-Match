package games.nataland.memorymatch.app

import android.app.Application
import games.nataland.memorymatch.main.DaggerMainComponent
import games.nataland.memorymatch.main.MainComponent
import games.nataland.memorymatch.main.MainModule

class MyApplication : Application() {
    lateinit var myComponent: MainComponent

    override fun onCreate() {
        super.onCreate()
        myComponent = createGameComponent()
    }

    private fun createGameComponent(): MainComponent {
        return DaggerMainComponent.builder()
                .mainModule(MainModule())
                .build()
    }

}