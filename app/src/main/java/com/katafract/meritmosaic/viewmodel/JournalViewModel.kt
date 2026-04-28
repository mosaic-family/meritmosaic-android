// JournalViewModel.kt — backs JournalScreen (the iOS JournalListView port).
package com.katafract.meritmosaic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.katafract.meritmosaic.BuildConfig
import com.katafract.meritmosaic.api.MeritMosaicApi
import com.katafract.meritmosaic.data.JournalEntry
import com.katafract.meritmosaic.data.MockDataSeeder
import com.katafract.meritmosaic.services.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JournalViewModel(app: Application) : AndroidViewModel(app) {

    private val auth = AuthService(app)
    private val api  = MeritMosaicApi(tokenProvider = { auth.currentToken() })

    private val _entries = MutableStateFlow<List<JournalEntry>>(emptyList())
    val entries: StateFlow<List<JournalEntry>> = _entries.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Screenshot mode: deterministic seed data, no network.
            if (BuildConfig.SCREENSHOT_MODE) {
                _entries.value = MockDataSeeder.seedJournalEntries
                _isLoading.value = false
                return@launch
            }

            // Live mode: try API; on failure fall back to seed so the
            // UI is never blank in dev builds.
            api.listJournal()
                .onSuccess { _entries.value = it }
                .onFailure {
                    _error.value = it.message
                    if (BuildConfig.DEBUG) {
                        // Dev builds show seeded content if the user isn't
                        // signed in or the API is unreachable; this keeps
                        // navigation testable without auth wired.
                        _entries.value = MockDataSeeder.seedJournalEntries
                    }
                }
            _isLoading.value = false
        }
    }

    /** Group entries by Today / Yesterday / This Week / Older — mirrors
     *  iOS JournalListView's `groupedEntries`. The Composable consumes
     *  this list of (sectionLabel, entries) pairs in display order. */
    fun grouped(): List<Pair<String, List<JournalEntry>>> {
        val today    = mutableListOf<JournalEntry>()
        val yesterday= mutableListOf<JournalEntry>()
        val week     = mutableListOf<JournalEntry>()
        val older    = mutableListOf<JournalEntry>()

        // Naive date bucketing — string-prefix on ISO timestamp. Works for
        // current entries; for production we'll use java.time once we wire
        // a proper date deserializer onto JournalEntry.createdAt.
        val now = java.time.OffsetDateTime.now()
        val todayStr     = now.toLocalDate().toString()
        val yesterdayStr = now.minusDays(1).toLocalDate().toString()
        val weekStart    = now.minusDays(7)

        for (entry in _entries.value) {
            val parsed = runCatching { java.time.OffsetDateTime.parse(entry.createdAt) }.getOrNull()
            val day    = parsed?.toLocalDate()?.toString()
            when {
                day == todayStr               -> today.add(entry)
                day == yesterdayStr           -> yesterday.add(entry)
                parsed != null && parsed.isAfter(weekStart) -> week.add(entry)
                else                          -> older.add(entry)
            }
        }
        return buildList {
            if (today.isNotEmpty())     add("Today"      to today)
            if (yesterday.isNotEmpty()) add("Yesterday"  to yesterday)
            if (week.isNotEmpty())      add("This Week"  to week)
            if (older.isNotEmpty())     add("Older"      to older)
        }
    }
}
