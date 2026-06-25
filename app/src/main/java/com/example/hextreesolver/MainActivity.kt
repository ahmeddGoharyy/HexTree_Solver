package com.example.hextreesolver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hextreesolver.ui.theme.HexTreeSolverTheme

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
        ButtonGrid()
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
fun ButtonGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // 3 per row as requested
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Button(onClick = { /* Button 1 */ }) { Text("flag1") } }
        item { Button(onClick = { /* Button 2 */ }) { Text("flag2") } }
        item { Button(onClick = { /* Button 3 */ }) { Text("flag3") } }
        item { Button(onClick = { /* Button 4 */ }) { Text("flag4") } }
        item { Button(onClick = { /* Button 5 */ }) { Text("flag5") } }
        item { Button(onClick = { /* Button 6 */ }) { Text("flag6") } }
        item { Button(onClick = { /* Button 7 */ }) { Text("flag7") } }
        item { Button(onClick = { /* Button 8 */ }) { Text("flag8") } }
        item { Button(onClick = { /* Button 9 */ }) { Text("flag9") } }
        item { Button(onClick = { /* Button 10 */ }) { Text("flag10") } }
        item { Button(onClick = { /* Button 11 */ }) { Text("flag11") } }
        item { Button(onClick = { /* Button 12 */ }) { Text("flag12") } }
        item { Button(onClick = { /* Button 13 */ }) { Text("flag13") } }
        item { Button(onClick = { /* Button 14 */ }) { Text("flag14") } }
        item { Button(onClick = { /* Button 15 */ }) { Text("flag15") } }
        item { Button(onClick = { /* Button 16 */ }) { Text("flag16") } }
        item { Button(onClick = { /* Button 17 */ }) { Text("flag17") } }
        item { Button(onClick = { /* Button 18 */ }) { Text("flag18") } }
        item { Button(onClick = { /* Button 19 */ }) { Text("flag19") } }
        item { Button(onClick = { /* Button 20 */ }) { Text("flag20") } }
        item { Button(onClick = { /* Button 21 */ }) { Text("flag21") } }
        item { Button(onClick = { /* Button 22 */ }) { Text("flag22") } }
        item { Button(onClick = { /* Button 23 */ }) { Text("flag23") } }
        item { Button(onClick = { /* Button 24 */ }) { Text("flag24") } }
        item { Button(onClick = { /* Button 25 */ }) { Text("flag25") } }
        item { Button(onClick = { /* Button 26 */ }) { Text("flag26") } }
        item { Button(onClick = { /* Button 27 */ }) { Text("flag27") } }
        item { Button(onClick = { /* Button 28 */ }) { Text("flag28") } }
        item { Button(onClick = { /* Button 29 */ }) { Text("flag29") } }
        item { Button(onClick = { /* Button 30 */ }) { Text("flag30") } }
        item { Button(onClick = { /* Button 31 */ }) { Text("flag31") } }
        item { Button(onClick = { /* Button 32 */ }) { Text("flag32") } }
        item { Button(onClick = { /* Button 33 */ }) { Text("flag33") } }
        item { Button(onClick = { /* Button 34 */ }) { Text("flag34") } }
        item { Button(onClick = { /* Button 35 */ }) { Text("flag35") } }
        item { Button(onClick = { /* Button 36 */ }) { Text("flag36") } }
        item { Button(onClick = { /* Button 37 */ }) { Text("flag37") } }
        item { Button(onClick = { /* Button 38 */ }) { Text("flag38") } }
        item { Button(onClick = { /* Button 39 */ }) { Text("flag39") } }
        item { Button(onClick = { /* Button 40 */ }) { Text("flag40") } }
        item { Button(onClick = { /* Button 41 */ }) { Text("flag41") } }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    HexTreeSolverTheme {
        MainContent()
    }
}
