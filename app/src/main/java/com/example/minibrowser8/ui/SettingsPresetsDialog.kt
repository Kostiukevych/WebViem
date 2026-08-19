package com.example.minibrowser8.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.minibrowser8.model.AppLanguage
import com.example.minibrowser8.model.AppThemeStyle
import com.example.minibrowser8.model.StringsHelper

@Composable
fun SettingsPresetsDialog(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    currentTheme: AppThemeStyle,
    onThemeChange: (AppThemeStyle) -> Unit,
    currentSlotCount: Int,
    onSlotCountChange: (Int) -> Unit,
    onApplyPreset: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val presets = listOf(
        PresetItem(
            name = "⚡ Essential Multitask",
            urls = listOf(
                "https://www.google.com",
                "https://m.youtube.com",
                "https://www.reddit.com",
                "https://en.m.wikipedia.org",
                "https://github.com",
                "https://duckduckgo.com",
                "https://news.ycombinator.com",
                "https://www.bing.com"
            )
        ),
        PresetItem(
            name = "🎬 Video & Streams",
            urls = listOf(
                "https://m.youtube.com",
                "https://m.twitch.tv",
                "https://www.dailymotion.com",
                "https://vimeo.com",
                "https://m.youtube.com/feed/trending",
                "https://m.twitch.tv/directory",
                "https://m.youtube.com/feed/subscriptions",
                "https://www.ted.com"
            )
        ),
        PresetItem(
            name = "💬 Social & Feeds",
            urls = listOf(
                "https://www.reddit.com",
                "https://x.com",
                "https://news.ycombinator.com",
                "https://www.threads.net",
                "https://mastodon.social",
                "https://m.facebook.com",
                "https://www.tumblr.com",
                "https://www.pinterest.com"
            )
        ),
        PresetItem(
            name = "💻 Dev & Tech",
            urls = listOf(
                "https://github.com",
                "https://stackoverflow.com",
                "https://news.ycombinator.com",
                "https://dev.to",
                "https://kotlinlang.org",
                "https://developer.android.com",
                "https://medium.com",
                "https://gitlab.com"
            )
        )
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("settings_presets_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = currentTheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = currentTheme.accent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = StringsHelper.get("settings", language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = currentTheme.textPrimary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = currentTheme.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SECTION 1: LANGUAGE SWITCHER
                Text(
                    text = "🌐 " + StringsHelper.get("language", language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = currentTheme.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.entries.forEach { lang ->
                        val isSelected = language == lang
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) currentTheme.accent else currentTheme.border,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onLanguageChange(lang) },
                            color = if (isSelected) currentTheme.primary else currentTheme.surfaceVariant
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lang.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else currentTheme.textPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SECTION 2: 5 THEME STYLES
                Text(
                    text = "🎨 " + StringsHelper.get("ui_style", language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = currentTheme.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppThemeStyle.entries.forEach { style ->
                        val isSelected = currentTheme == style
                        val themeTitle = when (language) {
                            AppLanguage.UKRAINIAN -> style.titleUa
                            AppLanguage.RUSSIAN -> style.titleRu
                            AppLanguage.ENGLISH -> style.titleEn
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) style.accent else currentTheme.border.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onThemeChange(style) },
                            color = style.surface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Color swatches preview
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(style.primary)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(style.accent)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = themeTitle,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = style.textPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = style.accent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SECTION 3: ACTIVE SLOTS COUNT (1, 2, 4, 6, 8)
                Text(
                    text = "📱 " + StringsHelper.get("active_slots", language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = currentTheme.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(1, 2, 4, 6, 8).forEach { count ->
                        val isSelected = currentSlotCount == count
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) currentTheme.accent else currentTheme.border,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onSlotCountChange(count) },
                            color = if (isSelected) currentTheme.primary else currentTheme.surfaceVariant
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$count",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else currentTheme.textPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SECTION 4: PRESET BUNDLES
                Text(
                    text = "📦 " + StringsHelper.get("presets", language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = currentTheme.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    presets.forEach { preset ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(0.5.dp, currentTheme.border.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable {
                                    onApplyPreset(preset.urls)
                                    onDismiss()
                                },
                            color = currentTheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = preset.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = currentTheme.textPrimary
                                )
                                Text(
                                    text = StringsHelper.get("load", language) + " ➔",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = currentTheme.accent
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primary)
                ) {
                    Text(
                        text = StringsHelper.get("done", language),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

data class PresetItem(
    val name: String,
    val urls: List<String>
)
