// SettingsScreen.kt — port of iOS SettingsView in AppTabView.swift.
//
// Profile card (sign-in stub), notifications toggle, credits link,
// legal links, version, sign out.
package com.katafract.meritmosaic.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.katafract.meritmosaic.ui.theme.MosaicAmberAccent
import com.katafract.meritmosaic.ui.theme.MosaicCoral
import com.katafract.meritmosaic.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onOpenCredits: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val displayName            by viewModel.displayName.collectAsState()
    val email                  by viewModel.email.collectAsState()
    val signedIn               by viewModel.signedIn.collectAsState()
    val notificationsEnabled   by viewModel.notificationsEnabled.collectAsState()

    val context = LocalContext.current
    val versionName = androidx.compose.runtime.remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "1.0.0"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile / sign-in card
            item { ProfileCard(
                signedIn = signedIn,
                displayName = displayName,
                email = email,
                onSignIn = viewModel::signIn
            ) }

            // Notifications toggle
            item {
                SettingsRow(
                    icon = Icons.Default.Notifications,
                    title = "Daily nudge",
                    trailing = {
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = viewModel::setNotificationsEnabled,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MosaicAmberAccent
                            )
                        )
                    }
                )
            }

            // Credit balance link
            item {
                SettingsRow(
                    icon = Icons.Default.Bolt,
                    title = "Credit balance",
                    onClick = onOpenCredits
                )
            }

            // Legal section
            item {
                SectionLabel("Legal")
                Spacer(Modifier.size(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    SettingsRow(
                        icon = Icons.Default.Shield,
                        title = "Privacy Policy",
                        onClick = { /* open https://meritmosaic.io/privacy via Custom Tabs */ },
                        showSurface = false
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant,
                                      modifier = Modifier.padding(start = 48.dp))
                    SettingsRow(
                        icon = Icons.Default.Shield,
                        title = "Terms of Service",
                        onClick = { /* open https://meritmosaic.io/terms */ },
                        showSurface = false
                    )
                }
            }

            // About / version
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Version", style = MaterialTheme.typography.bodyMedium)
                        Text(versionName, style = MaterialTheme.typography.bodyMedium,
                             color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Published by", style = MaterialTheme.typography.bodyMedium)
                        Text("Katafract LLC", style = MaterialTheme.typography.bodyMedium,
                             color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Sign out (only if signed in)
            if (signedIn) {
                item {
                    SettingsRow(
                        icon  = Icons.AutoMirrored.Filled.ExitToApp,
                        title = "Sign out",
                        tint  = MosaicCoral,
                        onClick = viewModel::signOut
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(
    signedIn: Boolean,
    displayName: String?,
    email: String?,
    onSignIn: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(enabled = !signedIn) { onSignIn() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(MosaicAmberAccent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (displayName ?: "?").take(1).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = MosaicAmberAccent,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (signedIn) (displayName ?: "Your profile") else "Sign in with Sigil",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (signedIn) (email ?: "") else "katafract.com identity — one login across all apps.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
        if (!signedIn) {
            Icon(Icons.Default.ChevronRight,
                 contentDescription = null,
                 tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SectionLabel(label: String) {
    Text(
        text = label.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    tint: Color? = null,
    showSurface: Boolean = true
) {
    val rowMod = if (showSurface) {
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(16.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(16.dp)
    }

    Row(modifier = rowMod, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint ?: MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.size(16.dp))
        Text(title,
             style = MaterialTheme.typography.bodyLarge,
             color = tint ?: MaterialTheme.colorScheme.onSurface,
             modifier = Modifier.weight(1f))
        trailing?.invoke()
        if (trailing == null && onClick != null) {
            Icon(Icons.Default.ChevronRight,
                 contentDescription = null,
                 tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

