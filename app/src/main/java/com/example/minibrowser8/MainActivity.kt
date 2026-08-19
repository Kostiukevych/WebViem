package com.example.minibrowser8

import android.os.Bundle
import android.webkit.CookieManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minibrowser8.model.AppLanguage
import com.example.minibrowser8.model.AppThemeStyle
import com.example.minibrowser8.model.BrowserSlotState
import com.example.minibrowser8.model.StringsHelper
import com.example.minibrowser8.ui.BookmarksDialog
import com.example.minibrowser8.ui.BrowserCard
import com.example.minibrowser8.ui.BrowserFullscreenView
import com.example.minibrowser8.ui.SettingsPresetsDialog
import com.example.minibrowser8.ui.theme.MiniBrowserTheme

class MainActivity : ComponentActivity() {

    private val defaultUrls = listOf(
        "https://www.google.com",
        "https://m.youtube.com",
        "https://www.reddit.com",
        "https://en.m.wikipedia.org",
        "https://github.com",
        "https://duckduckgo.com",
        "https://news.ycombinator.com",
        "https://www.bing.com"
    )

    private val slots = List(8) { index ->
        BrowserSlotState(
            id = index,
            initialUrl = defaultUrls.getOrElse(index) { "" }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            CookieManager.getInstance().setAcceptCookie(true)
        } catch (_: Exception) {}

        setContent {
            MiniBrowserTheme {
                MainBrowserApp(slots = slots)
            }
        }
    }

    override fun onDestroy() {
        for (slot in slots) {
            slot.destroy()
        }
        super.onDestroy()
    }
}

@Composable
fun MainBrowserApp(
    slots: List<BrowserSlotState>
) {
    var language by remember { mutableStateOf(AppLanguage.UKRAINIAN) }
    var currentTheme by remember { mutableStateOf(AppThemeStyle.CYBERPUNK_NEON) }

    var slotCount by remember { mutableIntStateOf(8) }
    var columnCount by remember { mutableIntStateOf(2) }
    var fullscreenSlotId by remember { mutableStateOf<Int?>(null) }
    
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showBookmarksDialog by remember { mutableStateOf(false) }

    val activeSlots = slots.take(slotCount)
    val fullscreenSlot = fullscreenSlotId?.let { id -> slots.find { it.id == id } }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(currentTheme.background)
            .statusBarsPadding(),
        containerColor = currentTheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Non-Scrolling Screen Layout
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .background(currentTheme.cardHeader)
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo & App Title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(currentTheme.primary, currentTheme.accent)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⚡",
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = StringsHelper.get("app_title", language),
                            style = MaterialTheme.typography.titleMedium,
                            color = currentTheme.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Bookmarks Button
                    IconButton(
                        onClick = { showBookmarksDialog = true },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("bookmarks_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = StringsHelper.get("bookmarks", language),
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    // Reload All Button
                    Surface(
                        modifier = Modifier
                            .height(26.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .clickable {
                                for (slot in activeSlots) {
                                    slot.reload()
                                }
                            }
                            .testTag("reload_all_button"),
                        color = Color.Transparent,
                        shape = RoundedCornerShape(13.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(currentTheme.primary, currentTheme.accent)
                                    )
                                )
                                .padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = StringsHelper.get("reload_all", language),
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = StringsHelper.get("reload_all", language),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    // Settings & Styles Button
                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = StringsHelper.get("settings", language),
                            tint = currentTheme.accent,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // Non-scrolling Grid: 4 rows x 2 columns
                val rowCount = (activeSlots.size + columnCount - 1) / columnCount

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 3.dp, vertical = 2.dp)
                        .testTag("browser_no_scroll_grid"),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    for (rowIndex in 0 until rowCount) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            for (colIndex in 0 until columnCount) {
                                val itemIndex = rowIndex * columnCount + colIndex
                                if (itemIndex < activeSlots.size) {
                                    val slot = activeSlots[itemIndex]
                                    BrowserCard(
                                        slot = slot,
                                        themeStyle = currentTheme,
                                        language = language,
                                        isFullscreenActive = (fullscreenSlotId == slot.id),
                                        onEnterFullscreen = { fullscreenSlotId = slot.id },
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // Fullscreen View Overlay
            AnimatedVisibility(
                visible = (fullscreenSlot != null),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (fullscreenSlot != null) {
                    BrowserFullscreenView(
                        slot = fullscreenSlot,
                        themeStyle = currentTheme,
                        language = language,
                        onExitFullscreen = { fullscreenSlotId = null },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Bookmarks Dialog
            if (showBookmarksDialog) {
                BookmarksDialog(
                    language = language,
                    themeStyle = currentTheme,
                    activeSlotCount = slotCount,
                    onOpenBookmarkInSlot = { url, slotIdx ->
                        if (slotIdx < slots.size) {
                            slots[slotIdx].load(url)
                        }
                    },
                    onOpenBookmarkInAll = { url ->
                        for (s in activeSlots) {
                            s.load(url)
                        }
                    },
                    onDismiss = { showBookmarksDialog = false }
                )
            }

            // Settings & Presets Dialog
            if (showSettingsDialog) {
                SettingsPresetsDialog(
                    language = language,
                    onLanguageChange = { language = it },
                    currentTheme = currentTheme,
                    onThemeChange = { currentTheme = it },
                    currentSlotCount = slotCount,
                    onSlotCountChange = { slotCount = it },
                    currentColumnCount = columnCount,
                    onColumnCountChange = { columnCount = it },
                    onApplyPreset = { presetUrls ->
                        presetUrls.forEachIndexed { index, url ->
                            if (index < slots.size) {
                                slots[index].load(url)
                            }
                        }
                    },
                    onDismiss = { showSettingsDialog = false }
                )
            }
        }
    }
}
