package com.example.minibrowser8.ui

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.minibrowser8.model.AppLanguage
import com.example.minibrowser8.model.AppThemeStyle
import com.example.minibrowser8.model.BookmarkManager
import com.example.minibrowser8.model.BrowserSlotState
import com.example.minibrowser8.model.StringsHelper

@Composable
fun BrowserCard(
    slot: BrowserSlotState,
    themeStyle: AppThemeStyle,
    language: AppLanguage,
    isFullscreenActive: Boolean,
    onEnterFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val isBookmarked = BookmarkManager.isBookmarked(slot.currentUrl.ifBlank { slot.inputUrl })

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (slot.isLoading) 1.5.dp else 1.dp,
                color = if (slot.isLoading) themeStyle.accent else themeStyle.border,
                shape = RoundedCornerShape(10.dp)
            )
            .testTag("browser_card_${slot.id}"),
        color = themeStyle.surface,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar (28dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .background(themeStyle.cardHeader)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Slot Number Badge
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(themeStyle.primary, themeStyle.accent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${slot.id + 1}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(3.dp))

                // Compact URL / Search Input Field
                BasicTextField(
                    value = slot.inputUrl,
                    onValueChange = { slot.inputUrl = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(22.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(themeStyle.surfaceVariant)
                        .border(0.5.dp, themeStyle.border.copy(alpha = 0.5f), RoundedCornerShape(5.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .testTag("url_input_${slot.id}"),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = themeStyle.textPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(themeStyle.accent),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(
                        onGo = {
                            slot.load(slot.inputUrl)
                            focusManager.clearFocus()
                        }
                    ),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                if (slot.inputUrl.isEmpty()) {
                                    Text(
                                        text = StringsHelper.get("url_hint", language),
                                        color = themeStyle.textSecondary.copy(alpha = 0.7f),
                                        fontSize = 9.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                innerTextField()
                            }
                            if (slot.inputUrl.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { slot.inputUrl = "" },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = themeStyle.textSecondary,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.width(1.dp))

                // Bookmark Button
                IconButton(
                    onClick = {
                        val activeUrl = slot.currentUrl.ifBlank { slot.inputUrl }
                        if (activeUrl.isNotBlank() && activeUrl != "about:blank") {
                            if (isBookmarked) {
                                val found = BookmarkManager.bookmarks.find {
                                    it.url.equals(BrowserSlotState.formatUrl(activeUrl), ignoreCase = true)
                                }
                                found?.let { BookmarkManager.removeBookmark(it.id) }
                                Toast.makeText(context, StringsHelper.get("bookmark_removed", language), Toast.LENGTH_SHORT).show()
                            } else {
                                BookmarkManager.addBookmark(slot.title.ifBlank { "Bookmark #${slot.id + 1}" }, activeUrl)
                                Toast.makeText(context, StringsHelper.get("bookmark_added", language), Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) Color(0xFFFFD700) else themeStyle.textSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                }

                // Reload Button
                IconButton(
                    onClick = {
                        if (slot.isLoading) {
                            slot.stop()
                        } else {
                            slot.reload()
                        }
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("reload_button_${slot.id}")
                ) {
                    Icon(
                        imageVector = if (slot.isLoading) Icons.Default.Close else Icons.Default.Refresh,
                        contentDescription = "Reload",
                        tint = if (slot.isLoading) themeStyle.accent else themeStyle.textSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                }

                // Open Externally Button
                IconButton(
                    onClick = {
                        val targetUrl = slot.currentUrl.ifBlank { slot.inputUrl }
                        if (targetUrl.isNotBlank() && targetUrl != "about:blank") {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BrowserSlotState.formatUrl(targetUrl)))
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("open_external_button_${slot.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = "Open externally",
                        tint = themeStyle.textSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                }

                // Fullscreen Button
                IconButton(
                    onClick = onEnterFullscreen,
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("fullscreen_button_${slot.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Fullscreen",
                        tint = themeStyle.accent,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            // Progress Indicator
            if (slot.isLoading && slot.progress in 1..99) {
                LinearProgressIndicator(
                    progress = { slot.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.5.dp),
                    color = themeStyle.accent,
                    trackColor = themeStyle.border.copy(alpha = 0.3f)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(themeStyle.border.copy(alpha = 0.4f))
                )
            }

            // WebView Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
            ) {
                if (isFullscreenActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(themeStyle.surfaceVariant)
                            .clickable { onEnterFullscreen() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📺 Slot #${slot.id + 1} (${StringsHelper.get("fullscreen", language)})",
                            color = themeStyle.accent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    AndroidView(
                        factory = { ctx ->
                            val wv = slot.getOrCreateWebView(ctx)
                            (wv.parent as? ViewGroup)?.removeView(wv)
                            wv
                        },
                        update = { wv ->
                            if (wv.parent == null) {
                                // Re-attached if needed
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
