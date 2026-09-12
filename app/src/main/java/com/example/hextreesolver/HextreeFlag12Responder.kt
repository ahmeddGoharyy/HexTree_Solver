package com.example.hextreesolver

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class HextreeFlag12Responder : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val responseIntent = Intent().apply {
            putExtra("token", 1094795585)
        }

        setResult(RESULT_OK, responseIntent)
        finish()
    }
}
