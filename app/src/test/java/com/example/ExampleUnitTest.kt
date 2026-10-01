package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.audio.SoundSystem
import com.example.game.TrafficEngine
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {
    @Test
    fun testLevelLoadingAndSignalToggle() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val soundSystem = SoundSystem(context).apply { isMuted = true }
        val engine = TrafficEngine(soundSystem)

        val level = DhakaLevels.levels.first()
        engine.startLevel(level)

        assertEquals(GameStatus.PLAYING, engine.gameState.status)
        assertEquals(SignalState.GREEN, engine.signal.verticalState)
        assertEquals(SignalState.RED, engine.signal.horizontalState)

        engine.toggleSignal()
        assertEquals(SignalState.RED, engine.signal.verticalState)
        assertEquals(SignalState.GREEN, engine.signal.horizontalState)
    }

    @Test
    fun testHonkMeterAndEmpBlast() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val soundSystem = SoundSystem(context).apply { isMuted = true }
        val engine = TrafficEngine(soundSystem)

        engine.startLevel(DhakaLevels.levels.first())
        engine.gameState.honkMeter = 80f

        engine.triggerEmpBlast()
        assertEquals(45f, engine.gameState.honkMeter, 0.1f)
    }
}
