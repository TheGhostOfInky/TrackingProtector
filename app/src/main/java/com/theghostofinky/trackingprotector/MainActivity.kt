@file:OptIn(ExperimentalMaterial3Api::class)

package com.theghostofinky.trackingprotector

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.theghostofinky.trackingprotector.theme.TrackingProtectorAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appContext = applicationContext

        when (intent?.action) {
            Intent.ACTION_SEND -> if (intent.type == "text/plain") {
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                    runBlocking {
                        withContext(Dispatchers.IO) {
                            handleShare(it)
                        }
                    }
                }
            }

            Intent.ACTION_VIEW -> runBlocking {
                withContext(Dispatchers.IO) {
                    handleOpen(intent.data)
                }
            }
        }

        setContent {
            PrimaryViewport()
        }
    }
}

@Composable
fun PrimaryViewport() {
    val navController = rememberNavController()
    TrackingProtectorAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    MainView(
                        onOpenSettings = { navController.navigate("settings") }
                    )
                }
                composable("settings") {
                    SettingsView(
                        onExitSettings = { navController.navigate("main") }
                    )
                }
            }
        }
    }
}
