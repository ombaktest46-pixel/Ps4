package com.example

import android.app.Application
import android.content.Context
import android.view.KeyEvent
import androidx.test.core.app.ApplicationProvider
import com.example.model.BackgroundWaveStyle
import com.example.model.BgmPreset
import com.example.model.ThemeColorPreset
import com.example.viewmodel.Ps4LauncherViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("PS4 Launcher", appName)
    }

    @Test
    fun `test initial games list contains PES2017 and emulator titles`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = Ps4LauncherViewModel(app)

        val games = viewModel.games.value
        assertTrue(games.isNotEmpty())

        // Check required titles exist
        val hasPes2017 = games.any { it.title.contains("PES 2017") && it.platform == "Winlator PC" }
        val hasWitcher = games.any { it.title.contains("Witcher 3") && it.platform == "Winlator PC" }
        val hasGodOfWar = games.any { it.title.contains("God of War") && it.platform == "AetherSX2 PS2" }
        val hasGameHub = games.any { it.title.contains("GameHub") && it.platform == "GameHub" }
        val hasZelda = games.any { it.title.contains("Zelda") && it.platform == "Switch Yuzu" }
        val hasMobileLegends = games.any { it.title.contains("Mobile Legends") && it.platform == "Android Native" }

        assertTrue("Should have PES 2017 shortcut", hasPes2017)
        assertTrue("Should have Witcher 3", hasWitcher)
        assertTrue("Should have God of War PS2", hasGodOfWar)
        assertTrue("Should have GameHub", hasGameHub)
        assertTrue("Should have Zelda", hasZelda)
        assertTrue("Should have Mobile Legends", hasMobileLegends)

        // Selected index defaults to first game (index 0)
        assertEquals(0, viewModel.selectedIndex.value)
    }

    @Test
    fun `test gamepad navigation events`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = Ps4LauncherViewModel(app)

        assertEquals(0, viewModel.selectedIndex.value)

        // Press D-Pad Right
        val handledRight = viewModel.handleGamepadKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT, isActionDown = true)
        assertTrue(handledRight)
        assertEquals(1, viewModel.selectedIndex.value)

        // Press D-Pad Left
        val handledLeft = viewModel.handleGamepadKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT, isActionDown = true)
        assertTrue(handledLeft)
        assertEquals(0, viewModel.selectedIndex.value)

        // Press START/Options to toggle settings
        assertFalse(viewModel.isSettingsOpen.value)
        val handledStart = viewModel.handleGamepadKeyEvent(KeyEvent.KEYCODE_BUTTON_START, isActionDown = true)
        assertTrue(handledStart)
        assertTrue(viewModel.isSettingsOpen.value)

        // Press B to close settings
        val handledB = viewModel.handleGamepadKeyEvent(KeyEvent.KEYCODE_BUTTON_B, isActionDown = true)
        assertTrue(handledB)
        assertFalse(viewModel.isSettingsOpen.value)
    }

    @Test
    fun `test theme and audio settings update and persistence`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = Ps4LauncherViewModel(app)

        val current = viewModel.settings.value
        assertEquals(ThemeColorPreset.BLUE_PS4, current.themeColor)
        assertEquals(BackgroundWaveStyle.DYNAMIC_PS4, current.waveStyle)

        viewModel.updateSettings(
            current.copy(
                themeColor = ThemeColorPreset.CRIMSON,
                bgmPreset = BgmPreset.CUSTOM_AUDIO,
                customAudioTitle = "Lagu_Saya.mp3",
                customAudioUri = "/data/user/0/com.example/files/custom_audio/test.mp3",
                isBgmPlaying = true
            )
        )

        assertEquals(ThemeColorPreset.CRIMSON, viewModel.settings.value.themeColor)
        assertEquals(BgmPreset.CUSTOM_AUDIO, viewModel.settings.value.bgmPreset)
        assertEquals("Lagu_Saya.mp3", viewModel.settings.value.customAudioTitle)
        assertEquals(true, viewModel.settings.value.isBgmPlaying)

        // Verify swipeSfxEnabled is true by default so moving games plays PS audio
        assertTrue("swipeSfxEnabled should be true by default so moving games plays PS audio", current.swipeSfxEnabled)
        assertEquals(com.example.model.ButtonGuideStyle.NINTENDO_SWITCH, current.buttonGuideStyle)

        viewModel.updateSettings(
            viewModel.settings.value.copy(
                swipeSfxEnabled = false,
                buttonGuideStyle = com.example.model.ButtonGuideStyle.MINIMAL_PILL
            )
        )
        assertFalse(viewModel.settings.value.swipeSfxEnabled)
        assertEquals(com.example.model.ButtonGuideStyle.MINIMAL_PILL, viewModel.settings.value.buttonGuideStyle)
    }

    @Test
    fun `test gamepad bumper skip games`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = Ps4LauncherViewModel(app)
        assertEquals(0, viewModel.selectedIndex.value)

        // R1 skips 3 games
        val handledR1 = viewModel.handleGamepadKeyEvent(KeyEvent.KEYCODE_BUTTON_R1, isActionDown = true)
        assertTrue(handledR1)
        assertEquals(3, viewModel.selectedIndex.value)

        // L1 skips back 3 games
        val handledL1 = viewModel.handleGamepadKeyEvent(KeyEvent.KEYCODE_BUTTON_L1, isActionDown = true)
        assertTrue(handledL1)
        assertEquals(0, viewModel.selectedIndex.value)
    }
}
