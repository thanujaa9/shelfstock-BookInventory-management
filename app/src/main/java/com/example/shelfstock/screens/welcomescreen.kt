// 9. Update WelcomeScreen.kt to remove the notification button
package com.example.shelfstock.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import com.example.shelfstock.R
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onNavigateToMain: () -> Unit
) {
    val logoPainter = painterResource(id = R.drawable.shelfstock)
    val backgroundColor = Color(0xFF5B429A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = logoPainter,
                contentDescription = "ShelfStock Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Title
            Text(
                text = "ShelfStock",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color.White
                ),
                modifier = Modifier.padding(16.dp)
            )

            LaunchedEffect(key1 = true) {
                delay(2000)
                onNavigateToMain()
            }
        }
    }
}