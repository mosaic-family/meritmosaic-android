// CreditsViewModel.kt — backs the Credit Store screen.
//
// In production this drives Play Billing (BillingClient) to query
// available products + launch purchase flow. Server reconciles via
// /credits/grant when a Play Billing entitlement is detected. iOS
// uses StoreKit 2; Android uses Play Billing v7. Both hit the same
// server-side product IDs so server logic is unified.
//
// SCREENSHOT_MODE: returns curated packs from MockDataSeeder. DEBUG
// fallback: same. Real billing is wired in `billing/` package, but
// kept stubbed pending Tek's review.
package com.katafract.meritmosaic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.katafract.meritmosaic.BuildConfig
import com.katafract.meritmosaic.api.MeritMosaicApi
import com.katafract.meritmosaic.data.CreditPack
import com.katafract.meritmosaic.data.MockDataSeeder
import com.katafract.meritmosaic.data.PromptBalance
import com.katafract.meritmosaic.services.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreditsViewModel(app: Application) : AndroidViewModel(app) {

    private val auth = AuthService(app)
    private val api  = MeritMosaicApi(tokenProvider = { auth.currentToken() })

    private val _balance = MutableStateFlow<PromptBalance?>(null)
    val balance: StateFlow<PromptBalance?> = _balance.asStateFlow()

    private val _packs = MutableStateFlow<List<CreditPack>>(emptyList())
    val packs: StateFlow<List<CreditPack>> = _packs.asStateFlow()

    private val _purchaseInProgress = MutableStateFlow<String?>(null)
    val purchaseInProgress: StateFlow<String?> = _purchaseInProgress.asStateFlow()

    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            // Packs — currently sourced from seed (no Play Billing wired).
            // When billing lands, swap to BillingClient.queryProductDetails().
            _packs.value = MockDataSeeder.seedCreditPacks

            if (BuildConfig.SCREENSHOT_MODE) {
                _balance.value = MockDataSeeder.seedBalance
                return@launch
            }

            api.getBalance()
                .onSuccess { _balance.value = it }
                .onFailure {
                    if (BuildConfig.DEBUG) _balance.value = MockDataSeeder.seedBalance
                    else _lastError.value = it.message
                }
        }
    }

    fun purchase(productId: String) {
        // Real billing flow lives in billing/PlayBilling.kt. Here we
        // simulate the in-progress state so the UI is wireable.
        viewModelScope.launch {
            _purchaseInProgress.value = productId
            kotlinx.coroutines.delay(800)
            _purchaseInProgress.value = null
            // No state mutation — server is canonical for balance.
            // Subsequent load() will reflect the new balance.
            load()
        }
    }

    fun clearError() { _lastError.value = null }
}
