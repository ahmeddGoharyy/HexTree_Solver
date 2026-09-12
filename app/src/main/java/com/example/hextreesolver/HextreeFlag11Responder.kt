package com.example.hextreesolver

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class HextreeFlag11Responder : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Prepare the response packet containing the precise validation token
        val responseIntent = Intent().apply {
            putExtra("token", 1094795585)
        }

        // Set the result status as successful (RESULT_OK = -1) and attach payload
        setResult(RESULT_OK, responseIntent)

        // Return execution flow to Flag11Activity and terminate instantly
        finish()
    }
}
