// MainActivity.kt — Compose host with bottom-bar TabView nav.
//
// Tabs mirror iOS AppTabView:
//   • Journal      → JournalScreen    (logged wins)
//   • Activities   → ActivitiesScreen (rolled-up activities)
//   • Settings     → SettingsScreen   (profile, prefs, sign-in)
//
// Edge-to-edge enabled at the activity level via enableEdgeToEdge();
// individual screens use Scaffold + safeDrawing-aware insets so system
// bars stay transparent without content disappearing under them.
package com.katafract.meritmosaic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.katafract.meritmosaic.data.MockDataSeeder
import com.katafract.meritmosaic.ui.credits.CreditStoreScreen
import com.katafract.meritmosaic.ui.journal.ActivitiesScreen
import com.katafract.meritmosaic.ui.journal.JournalScreen
import com.katafract.meritmosaic.ui.newentry.NewEntryScreen
import com.katafract.meritmosaic.ui.settings.SettingsScreen
import com.katafract.meritmosaic.ui.theme.MeritMosaicTheme
import com.katafract.meritmosaic.ui.theme.MosaicAmberAccent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Touch the seeder so screenshot mode warm-up happens early
        // (no real cost; just helps debugging).
        @Suppress("UNUSED_EXPRESSION") MockDataSeeder.seedJournalEntries
        setContent {
            MeritMosaicTheme {
                MeritMosaicRoot()
            }
        }
    }
}

private enum class Tab(val label: String) {
    Journal("Journal"),
    Activities("Activities"),
    Settings("Settings")
}

@Composable
private fun MeritMosaicRoot() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = MaterialTheme.colorScheme.background
    ) { _ ->
        NavHost(
            navController    = navController,
            startDestination = "tabs",
            modifier         = Modifier.fillMaxSize()
        ) {
            composable("tabs") {
                TabsHost(
                    onAddEntry    = { navController.navigate("new-entry") },
                    onOpenCredits = { navController.navigate("credits") }
                )
            }
            composable("new-entry") {
                NewEntryScreen(onClose = { navController.popBackStack() })
            }
            composable("credits") {
                CreditStoreScreen(onClose = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun TabsHost(
    onAddEntry: () -> Unit,
    onOpenCredits: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(Tab.Journal) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    Tab.Journal    -> Icons.AutoMirrored.Filled.MenuBook
                                    Tab.Activities -> Icons.Default.AutoAwesome
                                    Tab.Settings   -> Icons.Default.Settings
                                },
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) },
                        selected = selectedTab == tab,
                        onClick  = { selectedTab = tab },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = MosaicAmberAccent,
                            selectedTextColor   = MosaicAmberAccent,
                            indicatorColor      = MosaicAmberAccent.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when (selectedTab) {
            Tab.Journal    -> JournalScreen(
                onAddEntry = onAddEntry,
                onEntryClick = { /* JournalEntryDetailView in next pass */ }
            )
            Tab.Activities -> ActivitiesScreen()
            Tab.Settings   -> SettingsScreen(onOpenCredits = onOpenCredits)
        }
        // innerPadding is consumed implicitly via Scaffold defaults — each
        // screen has its own TopAppBar so we let those handle vertical
        // spacing. Bottom-bar inset propagates through NavigationBar.
        @Suppress("UNUSED_EXPRESSION") innerPadding
    }
}
