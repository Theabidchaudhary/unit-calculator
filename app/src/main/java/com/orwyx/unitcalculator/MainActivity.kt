package com.orwyx.unitcalculator

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orwyx.unitcalculator.ui.RootViewModel
import com.orwyx.unitcalculator.ui.navigation.UnitCalculatorNavGraph
import com.orwyx.unitcalculator.ui.theme.AppBackground
import com.orwyx.unitcalculator.ui.theme.UnitCalculatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val rootViewModel: RootViewModel = hiltViewModel()
            val themeMode   by rootViewModel.themeMode.collectAsStateWithLifecycle()
            val accentColor by rootViewModel.accentColor.collectAsStateWithLifecycle()

            // Request POST_NOTIFICATIONS permission on Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val launcher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { /* permission result — alarm scheduling continues regardless */ }
                LaunchedEffect(Unit) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            UnitCalculatorTheme(themeMode = themeMode, accentColor = accentColor) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AppBackground()
                    UnitCalculatorNavGraph()
                }
            }
        }
    }
}
