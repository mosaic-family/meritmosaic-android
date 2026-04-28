// AppConfig.kt — central configuration. Mirrors iOS Core/Config/AppConfig.swift.
package com.katafract.meritmosaic.data

import com.katafract.meritmosaic.BuildConfig

object AppConfig {
    const val BUNDLE_ID         = "com.katafract.meritmosaic"
    const val APP_GROUP_ID      = "group.com.katafract.meritmosaic"
    const val DATASTORE_AUTH    = "auth"

    val apiBaseURL: String get() = BuildConfig.API_BASE_URL

    /** Play Billing product IDs — mirror iOS Storekit IDs in AppConfig.Prompts.
     *  Server treats these identically; Play SKUs and StoreKit SKUs share the
     *  same `com.katafract.meritmosaic.*` namespace per Katafract convention.
     */
    object Prompts {
        const val SIGNUP_BONUS = 30

        // Standard tier
        const val STANDARD_MICRO  = "com.katafract.meritmosaic.standard.micro"   //  80
        const val STANDARD_SPRINT = "com.katafract.meritmosaic.standard.sprint"  // 160 (best value)
        const val STANDARD_SEASON = "com.katafract.meritmosaic.standard.season"  // 320

        // Pro tier
        const val PRO_40  = "com.katafract.meritmosaic.pro.40"
        const val PRO_100 = "com.katafract.meritmosaic.pro.100"
        const val PRO_250 = "com.katafract.meritmosaic.pro.250"

        // Exec tier
        const val EXEC_10 = "com.katafract.meritmosaic.exec.10"
        const val EXEC_25 = "com.katafract.meritmosaic.exec.25"
        const val EXEC_50 = "com.katafract.meritmosaic.exec.50"

        /** Credit count per product ID. Server is canonical; this is for UI hints. */
        fun credits(productId: String): Int = when (productId) {
            STANDARD_MICRO  ->  80
            STANDARD_SPRINT -> 160
            STANDARD_SEASON -> 320
            PRO_40          ->  40
            PRO_100         -> 100
            PRO_250         -> 250
            EXEC_10         ->  10
            EXEC_25         ->  25
            EXEC_50         ->  50
            else            -> 0
        }
    }
}
