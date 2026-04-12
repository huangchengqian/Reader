package com.localreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.localreader.data.model.UserProfile
import com.localreader.ui.theme.LocalReaderTheme
import com.localreader.ui.navigation.AppNavigation
import com.localreader.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize user profile if not exists
        lifecycleScope.launch {
            val app = application as LocalReaderApp
            val existingProfile = app.database.userProfileDao().getUserProfileSync()
            if (existingProfile == null) {
                app.database.userProfileDao().insertProfile(UserProfile())
            }
        }

        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModel.factory(this@MainActivity)
            )
            val uiState by themeViewModel.uiState.collectAsState()

            LocalReaderTheme(
                darkTheme = uiState.isDarkTheme,
                dynamicColor = false
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    AppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        themeViewModel = themeViewModel
                    )
                }
            }
        }
    }
}
