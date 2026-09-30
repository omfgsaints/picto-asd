package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WordCard
import com.example.data.model.WordCategory

@Composable
fun AddCustomWordDialog(
    onDismiss: () -> Unit,
    onSaveWord: (WordCard) -> Unit
) {
    var wordText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(WordCategory.TOYS) }
    var selectedEmoji by remember { mutableStateOf("⭐") }

    val emojis = listOf(
        "⭐", "❤️", "🎈", "🐶", "🐱", "🧸", "🍎", "🍪", "🍕",
        "🚗", "🚲", "🏠", "🏫", "😊", "🎉", "🌈", "🍦", "✨"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (wordText.isNotBlank()) {
                        val card = WordCard(
                            word = wordText.trim().lowercase(),
                            label = wordText.trim().replaceFirstChar { it.uppercase() },
                            category = selectedCategory,
                            emoji = selectedEmoji,
                            syllables = wordText.trim().uppercase(),
                            description = descriptionText.ifBlank { "Custom visual card for ${wordText.trim()}" },
                            exampleSentence = "Look at the ${wordText.trim()}.",
                            isCustom = true
                        )
                        onSaveWord(card)
                        onDismiss()
                    }
                },
                enabled = wordText.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_custom_word_button")
            ) {
                Text("Save Card")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text(text = "✨ Create Picture Card", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = wordText,
                    onValueChange = { wordText = it },
                    label = { Text("Word (e.g. Fluffy, Nana)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_word_input")
                )

                Text(
                    text = "Pick an Icon:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(emojis) { emoji ->
                        val isSelected = selectedEmoji == emoji
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedEmoji = emoji }
                                .testTag("emoji_$emoji"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 24.sp)
                        }
                    }
                }

                Text(
                    text = "Category:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(WordCategory.entries.filter { it != WordCategory.GENERAL }) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedCategory = cat },
                            color = if (isSelected) cat.primaryColor else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(cat.emoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.size(4.dp))
                                Text(
                                    text = cat.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { descriptionText = it },
                    label = { Text("Note or sentence (Optional)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
