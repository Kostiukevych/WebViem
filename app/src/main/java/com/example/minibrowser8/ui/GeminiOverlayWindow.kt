package com.example.minibrowser8.ui

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.minibrowser8.model.AppLanguage
import com.example.minibrowser8.model.AppThemeStyle
import com.example.minibrowser8.model.GeminiOverlayState
import com.example.minibrowser8.model.StringsHelper

@Composable
fun GeminiOverlayWindow(
    geminiState: GeminiOverlayState,
    themeStyle: AppThemeStyle,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Floating Minimized Button (Pill badge in lower right corner)
    if (geminiState.isVisible && geminiState.isMinimized) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Surface(
                modifier = Modifier
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { geminiState.restore() }
                    .testTag("gemini_minimized_pill"),
                color = Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF4285F4), Color(0xFF9B51E0))
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Gemini AI",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = StringsHelper.get("gemini_ai", language),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Active Floating Window (85% height sheet with full Gemini assistant)
    AnimatedVisibility(
        visible = geminiState.isVisible && !geminiState.isMinimized,
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { geminiState.minimize() },
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .clickable(enabled = false) {}
                    .shadow(16.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .border(
                        1.dp,
                        themeStyle.border,
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .testTag("gemini_overlay_sheet"),
                color = themeStyle.surface
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                                )
                            )
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF4285F4), Color(0xFF9B51E0))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = StringsHelper.get("gemini_assistant", language),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "gemini.google.com",
                                    fontSize = 9.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { geminiState.reload() },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reload",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    try {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://gemini.google.com")
                                        )
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInBrowser,
                                    contentDescription = "Open in browser",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = { geminiState.minimize() },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = StringsHelper.get("minimize", language),
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = { geminiState.close() },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = StringsHelper.get("close", language),
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Progress bar
                    if (geminiState.isLoading && geminiState.progress in 1..99) {
                        LinearProgressIndicator(
                            progress = { geminiState.progress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp),
                            color = Color(0xFF4285F4),
                            trackColor = Color.Transparent
                        )
                    }

                    // Gemini WebView
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(Color.White)
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                val wv = geminiState.getOrCreateWebView(ctx)
                                (wv.parent as? ViewGroup)?.removeView(wv)
                                wv
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
