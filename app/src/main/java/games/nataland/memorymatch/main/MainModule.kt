package games.nataland.memorymatch.main

import dagger.Module
import dagger.Provides
import games.nataland.memorymatch.game.BoardStateStore
import javax.inject.Singleton

@Module
internal class MainModule {

    @Provides
    @Singleton
    fun provideState(): BoardStateStore {
        return BoardStateStore()
    }
}