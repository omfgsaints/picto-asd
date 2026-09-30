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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuickWordKeyboard(
    onLetterClick: (Char) -> Unit,
    onSpaceClick: () -> Unit,
    onBackspaceClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val row1 = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G')
    val row2 = listOf('H', 'I', 'J', 'K', 'L', 'M', 'N')
    val row3 = listOf('O', 'P', 'Q', 'R', 'S', 'T', 'U')
    val row4 = listOf('V', 'W', 'X', 'Y', 'Z')

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("big_letter_keyboard"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Row 1
            LetterRow(letters = row1, onLetterClick = onLetterClick)
            // Row 2
            LetterRow(letters = row2, onLetterClick = onLetterClick)
            // Row 3
            LetterRow(letters = row3, onLetterClick = onLetterClick)

            // Row 4: V, W, X, Y, Z + Space & Backspace
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row4.forEach { char ->
                    LetterKey(
                        letter = char,
                        onClick = { onLetterClick(char) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Space Key
                Surface(
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSpaceClick() }
                        .testTag("key_space"),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SpaceBar,
                            contentDescription = "Space",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Backspace Key
                Surface(
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onBackspaceClick() }
                        .testTag("key_backspace"),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Backspace,
                            contentDescription = "Backspace",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LetterRow(
    letters: List<Char>,
    onLetterClick: (Char) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        letters.forEach { char ->
            LetterKey(
                letter = char,
                onClick = { onLetterClick(char) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun LetterKey(
    letter: Char,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("key_$letter"),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = letter.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
