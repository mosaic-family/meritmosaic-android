// AuthService.kt — Sigil/Zitadel OAuth token persistence.
//
// STUB for the initial port: Sigil sign-in flow (browser-based OIDC against
// auth.katafract.com) lands in a follow-up PR. For now this exposes a
// simple "are we signed in?" surface backed by DataStore so the rest of
// the app can be wired without a hard auth dependency. The Compose UI
// surface a "Sign in with Sigil" button that, in stub mode, just stashes
// a placeholder token so the app behaves as authenticated.
//
// Production will replace `signInStub()` with the real OIDC flow:
//   - PKCE + custom-tab launch to https://auth.katafract.com/oauth/v2/authorize
//   - exchange code at /oauth/v2/token
//   - store access_token + refresh_token + user_id in DataStore
//   - rotate via refresh_token before expiry
package com.katafract.meritmosaic.services

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.katafract.meritmosaic.data.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authDataStore by preferencesDataStore(name = AppConfig.DATASTORE_AUTH)

class AuthService(private val context: Context) {

    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val USER_ID      = stringPreferencesKey("user_id")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val EMAIL        = stringPreferencesKey("email")
    }

    val tokenFlow: Flow<String?> = context.authDataStore.data
        .map { prefs: Preferences -> prefs[Keys.ACCESS_TOKEN] }

    val userIdFlow: Flow<String?> = context.authDataStore.data
        .map { it[Keys.USER_ID] }

    val displayNameFlow: Flow<String?> = context.authDataStore.data
        .map { it[Keys.DISPLAY_NAME] }

    val emailFlow: Flow<String?> = context.authDataStore.data
        .map { it[Keys.EMAIL] }

    suspend fun currentToken(): String? = tokenFlow.first()

    /** Stub — replaces the real Sigil OIDC flow until that PR lands.
     *  Stashes a placeholder so the app appears authenticated. */
    suspend fun signInStub(displayName: String = "Demo User", email: String = "demo@meritmosaic.io") {
        context.authDataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = "stub-token-pending-sigil-oauth"
            prefs[Keys.USER_ID]      = "00000000-0000-0000-0000-000000000001"
            prefs[Keys.DISPLAY_NAME] = displayName
            prefs[Keys.EMAIL]        = email
        }
    }

    suspend fun signOut() {
        context.authDataStore.edit { it.clear() }
    }
}
