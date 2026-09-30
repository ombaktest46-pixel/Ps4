package com.example

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ui.screens.Ps4LauncherScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.Ps4LauncherViewModel

class MainActivity : ComponentActivity() {
    private val launcherViewModel: Ps4LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Explicitly enforce landscape orientation as requested
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        enableEdgeToEdge()

        // Hide system bars for authentic console immersive fullscreen experience
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            MyApplicationTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    Ps4LauncherScreen(viewModel = launcherViewModel)
                }
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (launcherViewModel.handleGamepadKeyEvent(event.keyCode, event.action == KeyEvent.ACTION_DOWN)) {
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onGenericMotionEvent(event: android.view.MotionEvent): Boolean {
        if (launcherViewModel.handleGenericMotionEvent(event)) {
            return true
        }
        return super.onGenericMotionEvent(event)
    }
}
