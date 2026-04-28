// MockDataSeeder.kt — curated demo content for App Store / Play screenshots.
//
// Mirrors the iOS `ScreenshotMode` pattern: when BuildConfig.SCREENSHOT_MODE
// is true (set via env var SCREENSHOT_MODE=1 at build time), ViewModels read
// from this seeder instead of hitting the live API. Keeps screenshots
// deterministic across runs.
//
// Voice: "quiet achiever" — understated wins, real-feeling specifics, no
// humble-bragging. iOS bar is high here; mirror it.
package com.katafract.meritmosaic.data

object MockDataSeeder {

    val seedJournalEntries: List<JournalEntry> = listOf(
        JournalEntry(
            id          = "seed-1",
            text        = "Walked the new grad through our k8s deploy flow. She caught an env var I'd missed in the staging manifest before she pushed. Felt good to see the checklist working.",
            source      = "app",
            createdAt   = "2026-04-27T18:42:00Z",
            classifiedType = "mentorship",
            classificationConfidence = 0.92,
            coachedAt   = "2026-04-27T18:45:00Z",
            strengthScore = 0.78,
            resumeBullet = "Mentored a new graduate engineer through Kubernetes deployment workflow; her code review caught a staging-env misconfiguration before it reached production."
        ),
        JournalEntry(
            id          = "seed-2",
            text        = "Shipped the rate-limiter refactor. Down from 6 endpoints with custom logic to a single middleware. P99 dropped from 240ms to 110ms.",
            source      = "app",
            createdAt   = "2026-04-26T14:15:00Z",
            classifiedType = "engineering",
            classificationConfidence = 0.95,
            coachedAt   = "2026-04-26T14:18:00Z",
            strengthScore = 0.86,
            resumeBullet = "Refactored rate-limiting from 6 bespoke implementations into a single middleware layer; reduced p99 latency by 54% (240ms → 110ms) across all rate-limited endpoints."
        ),
        JournalEntry(
            id          = "seed-3",
            text        = "Helped my mom set up two-factor on her bank login. She'd been putting it off for a year.",
            source      = "siri",
            createdAt   = "2026-04-25T20:03:00Z",
            classifiedType = "personal",
            classificationConfidence = 0.81,
            coachingQuestion = "Did this affect anyone beyond your mom — siblings, a community, a workplace?",
            strengthScore = 0.42
        ),
        JournalEntry(
            id          = "seed-4",
            text        = "Pulled the Q1 retention numbers and noticed our 30-day curve flattened in February. Walked PMs through the cohort split — turns out it was the Android push fix.",
            source      = "app",
            createdAt   = "2026-04-24T11:30:00Z",
            classifiedType = "analytics",
            classificationConfidence = 0.89,
            coachedAt   = "2026-04-24T11:35:00Z",
            strengthScore = 0.71,
            resumeBullet = "Investigated Q1 retention curve; isolated a 30-day flattening to the February Android push-notification fix and presented findings to product team."
        ),
        JournalEntry(
            id          = "seed-5",
            text        = "Gave the welcome talk to 14 new hires this morning. Told the story about the 2024 outage instead of the slide deck. People stuck around to ask questions.",
            source      = "widget",
            createdAt   = "2026-04-22T09:00:00Z",
            classifiedType = "leadership",
            classificationConfidence = 0.87,
            coachedAt   = "2026-04-22T09:08:00Z",
            strengthScore = 0.68,
            resumeBullet = "Delivered new-hire orientation to 14 engineers; replaced standard slides with a postmortem narrative from the 2024 outage to demonstrate the company's incident-learning culture."
        ),
        JournalEntry(
            id          = "seed-6",
            text        = "Finished the volunteer reading hour at Lincoln. 22 kids this week. The librarian asked if I'd come back next month.",
            source      = "app",
            createdAt   = "2026-04-20T16:45:00Z",
            classifiedType = "volunteer",
            classificationConfidence = 0.93
        ),
        JournalEntry(
            id          = "seed-7",
            text        = "Reviewed three PRs from the platform team and left tight notes. One was blocking a partner integration so I prioritized it before lunch.",
            source      = "app",
            createdAt   = "2026-04-19T13:20:00Z",
            classifiedType = "engineering",
            classificationConfidence = 0.84
        )
    )

    val seedActivities: List<Activity> = listOf(
        Activity(
            id          = "act-1",
            userId      = "seed-user",
            title       = "Platform reliability improvements",
            description = "Reduced p99 latency, mentored junior engineers, owned the rate-limiter refactor across services.",
            category    = "engineering",
            strengthScore = 0.82,
            startDate   = "2025-10",
            isOngoing   = true,
            hoursPerWeek = 40,
            yearsParticipated = 1,
            createdAt   = "2026-04-26T14:18:00Z",
            updatedAt   = "2026-04-27T18:45:00Z"
        ),
        Activity(
            id          = "act-2",
            userId      = "seed-user",
            title       = "Lincoln Elementary reading volunteer",
            description = "Weekly reading hour for K-2 students; 20+ children per session.",
            category    = "volunteer",
            strengthScore = 0.55,
            startDate   = "2025-09",
            isOngoing   = true,
            hoursPerWeek = 2,
            yearsParticipated = 1,
            createdAt   = "2025-09-15T17:00:00Z",
            updatedAt   = "2026-04-20T16:45:00Z"
        ),
        Activity(
            id          = "act-3",
            userId      = "seed-user",
            title       = "New-hire onboarding lead",
            description = "Run engineering orientation for cohorts of 10-20 new hires; postmortem-driven curriculum.",
            category    = "leadership",
            strengthScore = 0.71,
            startDate   = "2024-06",
            isOngoing   = true,
            hoursPerWeek = 3,
            yearsParticipated = 2,
            createdAt   = "2024-06-12T09:00:00Z",
            updatedAt   = "2026-04-22T09:08:00Z"
        )
    )

    val seedBalance = PromptBalance(
        promptBalance   = 248,
        standardBalance = 180,
        proBalance      = 52,
        execBalance     = 16,
        earnedToday     = 4,
        streakDays      = 12,
        longestStreak   = 27,
        totalEntries    = 142
    )

    /** The 4 credit packs surfaced on CreditStoreScreen — Standard tier
     *  (the workhorses). Pro / Exec tiers exist server-side but iOS
     *  surfaces only Standard in the screenshot tier of the credit store.
     *  Best Value = Sprint (matches iOS isBestValue logic). */
    val seedCreditPacks: List<CreditPack> = listOf(
        CreditPack(AppConfig.Prompts.STANDARD_MICRO,  80,  "$5.00"),
        CreditPack(AppConfig.Prompts.STANDARD_SPRINT, 160, "$9.99",  isBestValue = true),
        CreditPack(AppConfig.Prompts.STANDARD_SEASON, 320, "$19.99"),
        CreditPack(AppConfig.Prompts.PRO_100,         100, "$9.99")
    )
}
