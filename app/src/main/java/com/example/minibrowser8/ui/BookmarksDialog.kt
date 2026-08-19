package com.example.minibrowser8.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.minibrowser8.model.AppLanguage
import com.example.minibrowser8.model.AppThemeStyle
import com.example.minibrowser8.model.Bookmark
import com.example.minibrowser8.model.BookmarkManager
import com.example.minibrowser8.model.StringsHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookmarksDialog(
    language: AppLanguage,
    themeStyle: AppThemeStyle,
    activeSlotCount: Int,
    onOpenBookmarkInSlot: (url: String, slotIndex: Int) -> Unit,
    onOpenBookmarkInAll: (url: String) -> Unit,
    onDismiss: () -> Unit
) {
    var showAddForm by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newUrl by remember { mutableStateOf("") }
    var selectedBookmarkForSlot by remember { mutableStateOf<Bookmark?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("bookmarks_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = themeStyle.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = themeStyle.accent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = StringsHelper.get("bookmarks", language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeStyle.textPrimary
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { showAddForm = !showAddForm },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (showAddForm) Icons.Default.Close else Icons.Default.Add,
                                contentDescription = "Add",
                                tint = themeStyle.accent
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = themeStyle.textSecondary
                            )
                        }
                    }
                }

                // Add Form
                if (showAddForm) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(themeStyle.surfaceVariant)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = StringsHelper.get("add_bookmark", language),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = themeStyle.textPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            placeholder = { Text(StringsHelper.get("title", language), fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeStyle.accent,
                                unfocusedBorderColor = themeStyle.border,
                                focusedTextColor = themeStyle.textPrimary,
                                unfocusedTextColor = themeStyle.textPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = newUrl,
                            onValueChange = { newUrl = it },
                            placeholder = { Text("https://example.com", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeStyle.accent,
                                unfocusedBorderColor = themeStyle.border,
                                focusedTextColor = themeStyle.textPrimary,
                                unfocusedTextColor = themeStyle.textPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (newUrl.isNotBlank()) {
                                    BookmarkManager.addBookmark(newTitle, newUrl)
                                    newTitle = ""
                                    newUrl = ""
                                    showAddForm = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primary)
                        ) {
                            Text(StringsHelper.get("save", language), color = Color.White)
                        }
                    }
                }

                // Slot Picker for selected bookmark
                if (selectedBookmarkForSlot != null) {
                    val bm = selectedBookmarkForSlot!!
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp)),
                        color = themeStyle.surfaceVariant
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "${StringsHelper.get("open_in_slot", language)}: ${bm.title}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeStyle.textPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                for (i in 0 until activeSlotCount) {
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                onOpenBookmarkInSlot(bm.url, i)
                                                selectedBookmarkForSlot = null
                                                onDismiss()
                                            },
                                        color = themeStyle.primary
                                    ) {
                                        Text(
                                            text = "Slot #${i + 1}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            onOpenBookmarkInAll(bm.url)
                                            selectedBookmarkForSlot = null
                                            onDismiss()
                                        },
                                    color = themeStyle.accent
                                ) {
                                    Text(
                                        text = StringsHelper.get("open_in_all", language),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // List of Bookmarks
                if (BookmarkManager.bookmarks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = StringsHelper.get("no_bookmarks", language),
                            fontSize = 12.sp,
                            color = themeStyle.textSecondary
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(
                            items = BookmarkManager.bookmarks,
                            key = { it.id }
                        ) { bm ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(0.5.dp, themeStyle.border.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedBookmarkForSlot = if (selectedBookmarkForSlot?.id == bm.id) null else bm
                                    },
                                color = themeStyle.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(themeStyle.primary.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Language,
                                            contentDescription = null,
                                            tint = themeStyle.accent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = bm.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = themeStyle.textPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = bm.url,
                                            fontSize = 10.sp,
                                            color = themeStyle.textSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    IconButton(
                                        onClick = { BookmarkManager.removeBookmark(bm.id) },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = themeStyle.textSecondary.copy(alpha = 0.7f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = themeStyle.primary)
                ) {
                    Text(StringsHelper.get("done", language), color = Color.White)
                }
            }
        }
    }
}
