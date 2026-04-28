// Models.kt — MeritMosaic Android port.
//
// Mirrors iOS `MeritMosaic/Core/Models/Models.swift`. Field names are
// camelCase in Kotlin and are mapped to/from the API's snake_case via
// kotlinx.serialization @SerialName below where they differ.
//
// API contract: https://api.meritmosaic.io/api/v1
package com.katafract.meritmosaic.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CareerStage {
    @SerialName("highSchool")   HIGH_SCHOOL,
    @SerialName("earlyCareer")  EARLY_CAREER,
    @SerialName("midCareer")    MID_CAREER,
    @SerialName("seniorCareer") SENIOR_CAREER;

    val displayName: String get() = when (this) {
        HIGH_SCHOOL   -> "High School"
        EARLY_CAREER  -> "Early Career"
        MID_CAREER    -> "Mid-Career"
        SENIOR_CAREER -> "Senior / Executive"
    }
}

@Serializable
enum class JournalSource {
    @SerialName("app")    APP,
    @SerialName("siri")   SIRI,
    @SerialName("widget") WIDGET,
    @SerialName("web")    WEB,
    @SerialName("email")  EMAIL
}

@Serializable
enum class CreditTier {
    @SerialName("standard") STANDARD,
    @SerialName("pro")      PRO,
    @SerialName("exec")     EXEC;

    val displayName: String get() = when (this) {
        STANDARD -> "Standard"
        PRO      -> "Pro"
        EXEC     -> "Executive"
    }

    val description: String get() = when (this) {
        STANDARD -> "Fast classification, everyday logging"
        PRO      -> "Higher-quality polish, resume bullets"
        EXEC     -> "Board-ready narrative, executive bios"
    }
}

@Serializable
data class Profile(
    @SerialName("user_id")      val userId: String,
    @SerialName("display_name") val displayName: String? = null,
                                val email: String? = null,
    @SerialName("career_stage") val careerStage: CareerStage? = null,
    @SerialName("coach_mode")   val coachMode: Boolean = true,
    @SerialName("created_at")   val createdAt: String
) {
    val displayNameOrFallback: String
        get() = displayName?.takeIf { it.isNotBlank() }
            ?: email?.substringBefore('@')
            ?: "there"
}

@Serializable
data class JournalEntry(
                                          val id: String,
                                          val text: String,
                                          val source: String,
    @SerialName("created_at")             val createdAt: String,
    @SerialName("classified_type")        val classifiedType: String? = null,
    @SerialName("classification_confidence") val classificationConfidence: Double? = null,
    @SerialName("coached_at")             val coachedAt: String? = null,
    @SerialName("coaching_question")      val coachingQuestion: String? = null,
    @SerialName("strength_score")         val strengthScore: Double? = null,
    @SerialName("activity_id")            val activityId: String? = null,
    @SerialName("resume_bullet")          val resumeBullet: String? = null
) {
    val isClassified: Boolean       get() = classifiedType != null
    val isCoached: Boolean          get() = coachedAt != null
    val hasCoachingQuestion: Boolean get() = !coachingQuestion.isNullOrBlank()

    val preview: String get() {
        val source = resumeBullet?.takeIf { it.isNotBlank() } ?: text
        return if (source.length > 140) source.take(140) + "…" else source
    }
}

@Serializable
data class JournalCreateRequest(
                                       val text: String,
                                       val source: String = "app",
    @SerialName("pre_classified_type") val preClassifiedType: String? = null
)

@Serializable
data class JournalCreateResponse(
                                  val id: String,
                                  val text: String,
                                  val source: String,
    @SerialName("created_at")     val createdAt: String,
    @SerialName("prompts_earned") val promptsEarned: Int,
    @SerialName("streak_days")    val streakDays: Int
)

@Serializable
data class Activity(
                                       val id: String,
    @SerialName("user_id")             val userId: String,
                                       val title: String,
                                       val description: String? = null,
                                       val category: String? = null,
    @SerialName("strength_score")      val strengthScore: Double = 0.0,
    @SerialName("start_date")          val startDate: String? = null,
    @SerialName("end_date")            val endDate: String? = null,
    @SerialName("is_ongoing")          val isOngoing: Boolean = false,
    @SerialName("years_participated")  val yearsParticipated: Int? = null,
    @SerialName("hours_per_week")      val hoursPerWeek: Int? = null,
    @SerialName("created_at")          val createdAt: String,
    @SerialName("updated_at")          val updatedAt: String
) {
    val strengthLabel: String get() = when {
        strengthScore >= 0.8 -> "Standout"
        strengthScore >= 0.6 -> "Strong"
        strengthScore >= 0.4 -> "Solid"
        else                 -> "Early"
    }
}

@Serializable
data class PromptBalance(
    @SerialName("prompt_balance")   val promptBalance: Int,
    @SerialName("standard_balance") val standardBalance: Int,
    @SerialName("pro_balance")      val proBalance: Int,
    @SerialName("exec_balance")     val execBalance: Int,
    @SerialName("earned_today")     val earnedToday: Int,
    @SerialName("streak_days")      val streakDays: Int,
    @SerialName("longest_streak")   val longestStreak: Int,
    @SerialName("total_entries")    val totalEntries: Int
) {
    val isLow: Boolean get() = promptBalance < 10
}

@Serializable
data class ClarifyRequest(
                                  val text: String,
                                  val question: String,
                                  val answer: String,
    @SerialName("career_stage")   val careerStage: String,
    @SerialName("activity_id")    val activityId: String? = null,
    @SerialName("previous_bullet") val previousBullet: String? = null
)

@Serializable
data class ClarifyResponse(
    @SerialName("enriched_text")        val enrichedText: String,
    @SerialName("previous_bullet")      val previousBullet: String? = null,
    @SerialName("strength_score")       val strengthScore: Double,
    @SerialName("prompts_spent")        val promptsSpent: Int,
    @SerialName("coaching_question")    val coachingQuestion: String? = null,
    @SerialName("suggested_responses")  val suggestedResponses: List<String>? = null,
    @SerialName("prompt_balance")       val promptBalance: Int? = null
)

/** Credit pack — Play Billing product mapping mirrors iOS AppConfig.Prompts. */
data class CreditPack(
    val productId: String,
    val credits:   Int,
    val priceLabel: String,
    val isBestValue: Boolean = false
)
