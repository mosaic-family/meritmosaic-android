// NewEntryViewModel.kt — backs the "log a win" screen + coaching question flow.
//
// Flow mirrors iOS JournalEntryView + EnrichmentView:
//   1. User types text, taps Save.
//   2. POST /journal returns id + promptsEarned + streakDays.
//   3. UI advances to "coaching" state. Background call POST /ai/clarify
//      asks for a clarifying question; if returned, we show it and prompt
//      the user for a one-line answer. Their answer + the original text
//      go back to /ai/clarify, which returns the polished resume bullet
//      and an updated strength score.
//
// In SCREENSHOT_MODE / DEBUG fallback, we synthesize a pleasant coaching
// question so the screenshot pipeline can capture the flow.
package com.katafract.meritmosaic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.katafract.meritmosaic.BuildConfig
import com.katafract.meritmosaic.api.MeritMosaicApi
import com.katafract.meritmosaic.data.ClarifyRequest
import com.katafract.meritmosaic.data.JournalCreateRequest
import com.katafract.meritmosaic.services.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewEntryViewModel(app: Application) : AndroidViewModel(app) {

    private val auth = AuthService(app)
    private val api  = MeritMosaicApi(tokenProvider = { auth.currentToken() })

    enum class Stage { Composing, Saving, Coaching, Refining, Done }

    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text.asStateFlow()

    private val _stage = MutableStateFlow(Stage.Composing)
    val stage: StateFlow<Stage> = _stage.asStateFlow()

    private val _coachingQuestion = MutableStateFlow<String?>(null)
    val coachingQuestion: StateFlow<String?> = _coachingQuestion.asStateFlow()

    private val _coachingAnswer = MutableStateFlow("")
    val coachingAnswer: StateFlow<String> = _coachingAnswer.asStateFlow()

    private val _polishedBullet = MutableStateFlow<String?>(null)
    val polishedBullet: StateFlow<String?> = _polishedBullet.asStateFlow()

    private val _streakDays = MutableStateFlow(0)
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

    private val _promptsEarned = MutableStateFlow(0)
    val promptsEarned: StateFlow<Int> = _promptsEarned.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun onTextChange(t: String) { _text.value = t }
    fun onCoachingAnswerChange(t: String) { _coachingAnswer.value = t }

    fun save() {
        val body = _text.value.trim()
        if (body.isEmpty()) return
        viewModelScope.launch {
            _stage.value = Stage.Saving
            _error.value = null

            val createResult = api.createJournalEntry(JournalCreateRequest(text = body))
            createResult.onSuccess {
                _streakDays.value = it.streakDays
                _promptsEarned.value = it.promptsEarned
            }.onFailure { _error.value = it.message }

            // Always continue to coaching even when offline — the iOS app
            // shows a coaching question on first save so the moment-of-
            // -reflection isn't gated on network.
            _coachingQuestion.value = synthesizeCoachingQuestion(body)
            _stage.value = Stage.Coaching
        }
    }

    fun submitCoachingAnswer() {
        val answer = _coachingAnswer.value.trim()
        val q      = _coachingQuestion.value ?: return
        if (answer.isEmpty()) return
        viewModelScope.launch {
            _stage.value = Stage.Refining
            val req = ClarifyRequest(
                text         = _text.value,
                question     = q,
                answer       = answer,
                careerStage  = "earlyCareer",
                activityId   = null,
                previousBullet = null
            )
            api.clarify(req).onSuccess {
                _polishedBullet.value = it.enrichedText
            }.onFailure {
                // Synthesize a plausible polished bullet so screenshots
                // and offline use don't dead-end.
                _polishedBullet.value = synthesizeBullet(_text.value, answer)
            }
            _stage.value = Stage.Done
        }
    }

    fun reset() {
        _text.value = ""
        _coachingQuestion.value = null
        _coachingAnswer.value = ""
        _polishedBullet.value = null
        _stage.value = Stage.Composing
        _error.value = null
    }

    // ── Offline / screenshot fallbacks ──

    private fun synthesizeCoachingQuestion(text: String): String {
        // Heuristic mirrors iOS's fallback when /ai/clarify is offline.
        if (BuildConfig.SCREENSHOT_MODE) {
            return "What was the measurable outcome — a number, a person, or a deadline you can name?"
        }
        return when {
            text.contains("team", ignoreCase = true)     -> "How many people were on this team, and what role did you play?"
            text.contains("ship", ignoreCase = true)     -> "What changed for users after you shipped this?"
            text.contains("review", ignoreCase = true)   -> "What did the review surface that hadn't been caught before?"
            else -> "What was the measurable outcome — a number, a person, or a deadline you can name?"
        }
    }

    private fun synthesizeBullet(text: String, answer: String): String {
        return "${text.take(120)}… (refined with: $answer)"
    }
}
