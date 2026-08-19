package com.example.minibrowser8.ui

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun BrowserFullscreenView(
    slot: BrowserSlotState,
    themeStyle: AppThemeStyle,
    language: AppLanguage,
    onExitFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val isBookmarked = BookmarkManager.isBookmarked(slot.currentUrl.ifBlank { slot.inputUrl })

    BackHandler {
        if (slot.customView != null) {
            slot.hideCustomVideoView()
        } else if (!slot.goBack()) {
            onExitFullscreen()
        }
    }

    // HTML5 Video custom view (e.g. YouTube fullscreen button)
    val customVideoView = slot.customView
    if (customVideoView != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AndroidView(
                factory = {
                    (customVideoView.parent as? ViewGroup)?.removeView(customVideoView)
                    customVideoView
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(themeStyle.background)
            .testTag("browser_fullscreen_view")
    ) {
        // Top Floating Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(themeStyle.cardHeader)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Exit Fullscreen
            IconButton(
                onClick = onExitFullscreen,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("exit_fullscreen_button")
            ) {
                Icon(
                    imageVector = Icons.Default.FullscreenExit,
                    contentDescription = StringsHelper.get("exit_fullscreen", language),
                    tint = themeStyle.accent,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Web Navigation: Back
            IconButton(
                onClick = { slot.goBack() },
                enabled = slot.canGoBack,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (slot.canGoBack) themeStyle.textPrimary else themeStyle.textSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(17.dp)
                )
            }

            // Web Navigation: Forward
            IconButton(
                onClick = { slot.goForward() },
                enabled = slot.canGoForward,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Forward",
                    tint = if (slot.canGoForward) themeStyle.textPrimary else themeStyle.textSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(17.dp)
                )
            }

            // URL input
            BasicTextField(
                value = slot.inputUrl,
                onValueChange = { slot.inputUrl = it },
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(themeStyle.surfaceVariant)
                    .border(0.5.dp, themeStyle.border.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("fullscreen_url_input"),
                singleLine = true,
                textStyle = TextStyle(
                    color = themeStyle.textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
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
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            innerTextField()
                        }
                        if (slot.inputUrl.isNotEmpty()) {
                            IconButton(
                                onClick = { slot.inputUrl = "" },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = StringsHelper.get("clear", language),
                                    tint = themeStyle.textSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.width(2.dp))

            // Bookmark in Fullscreen
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
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (isBookmarked) Color(0xFFFFD700) else themeStyle.textSecondary,
                    modifier = Modifier.size(17.dp)
                )
            }

            // Reload / Stop
            IconButton(
                onClick = {
                    if (slot.isLoading) {
                        slot.stop()
                    } else {
                        slot.reload()
                    }
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = if (slot.isLoading) Icons.Default.Close else Icons.Default.Refresh,
                    contentDescription = StringsHelper.get("reload", language),
                    tint = if (slot.isLoading) themeStyle.accent else themeStyle.textSecondary,
                    modifier = Modifier.size(17.dp)
                )
            }

            // Desktop Mode Toggle
            IconButton(
                onClick = { slot.toggleDesktopMode() },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = if (slot.isDesktopMode) Icons.Default.DesktopWindows else Icons.Default.PhoneAndroid,
                    contentDescription = StringsHelper.get("desktop_mode", language),
                    tint = if (slot.isDesktopMode) themeStyle.accent else themeStyle.textSecondary,
                    modifier = Modifier.size(17.dp)
                )
            }

            // Open Externally
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
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = StringsHelper.get("open_external", language),
                    tint = themeStyle.textSecondary,
                    modifier = Modifier.size(17.dp)
                )
            }
        }

        // Progress bar
        if (slot.isLoading && slot.progress in 1..99) {
            LinearProgressIndicator(
                progress = { slot.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
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

        // Fullscreen WebView Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White)
        ) {
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
