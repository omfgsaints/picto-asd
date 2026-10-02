package com.example.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LetterBankTile
import com.example.ui.viewmodel.PictoWordViewModel

@Composable
fun PictureSpellingView(
    viewModel: PictoWordViewModel,
    modifier: Modifier = Modifier
) {
    val currentItem by viewModel.currentSpellingItem.collectAsStateWithLifecycle()
    val slots by viewModel.spellingSlots.collectAsStateWithLifecycle()
    val tiles by viewModel.spellingTiles.collectAsStateWithLifecycle()
    val isCelebration by viewModel.isSpellingCelebration.collectAsStateWithLifecycle()
    val feedback by viewModel.spellingFeedback.collectAsStateWithLifecycle()
    val stars by viewModel.spellingStars.collectAsStateWithLifecycle()
    val levelFilter by viewModel.spellingLevelFilter.collectAsStateWithLifecycle()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Level Selector & Score Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    1 to "3 Letters",
                    2 to "4 Letters",
                    3 to "5+ Letters"
                ).forEach { (lvl, title) ->
                    FilterChip(
                        selected = levelFilter == lvl,
                        onClick = { viewModel.setSpellingLevel(lvl) },
                        label = {
                            Text(
                                text = title,
                                fontWeight = if (levelFilter == lvl) FontWeight.Black else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFEF3C7)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$stars",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = Color(0xFFB45309)
                    )
                }
            }
        }

        if (isLandscape) {
            // Tablet Landscape Dual Pane
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column: Real Picture & Audio
                Column(
                    modifier = Modifier
                        .weight(0.45f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PictoSymbol(
                        card = currentItem.card,
                        size = 180.dp,
                        fontSize = 80.sp,
                        modifier = Modifier.testTag("spelling_real_picture")
                    )

                    FilledTonalButton(
                        onClick = { viewModel.speakCurrentSpellingWord() },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("spelling_speak_word_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Hear word",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hear Word 🔊",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = if (isCelebration) Color(0xFF059669) else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                // Right Column: Slots, Letter Bank, and Controls
                Column(
                    modifier = Modifier
                        .weight(0.55f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SpellingSlotsRow(
                        slots = slots,
                        isCelebration = isCelebration,
                        onRemoveSlot = { viewModel.removeSpellingSlot(it) }
                    )

                    SpellingLetterBank(
                        tiles = tiles,
                        onTileClick = { viewModel.onSpellingTileClick(it) }
                    )

                    SpellingControlsBar(
                        isCelebration = isCelebration,
                        onHint = { viewModel.provideSpellingHint() },
                        onReset = { viewModel.resetSpellingWord() },
                        onNext = { viewModel.nextSpellingWord() }
                    )
                }
            }
        } else {
            // Portrait Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(6.dp))

                // The Real Actual Photograph
                PictoSymbol(
                    card = currentItem.card,
                    size = 170.dp,
                    fontSize = 76.sp,
                    modifier = Modifier.testTag("spelling_real_picture")
                )

                Spacer(modifier = Modifier.height(10.dp))

                FilledTonalButton(
                    onClick = { viewModel.speakCurrentSpellingWord() },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("spelling_speak_word_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Hear word",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hear Word 🔊",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = feedback,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = if (isCelebration) Color(0xFF059669) else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Letter Slots
                SpellingSlotsRow(
                    slots = slots,
                    isCelebration = isCelebration,
                    onRemoveSlot = { viewModel.removeSpellingSlot(it) }
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Tap letters to spell:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Letter Bank
                SpellingLetterBank(
                    tiles = tiles,
                    onTileClick = { viewModel.onSpellingTileClick(it) }
                )

                Spacer(modifier = Modifier.height(18.dp))

                SpellingControlsBar(
                    isCelebration = isCelebration,
                    onHint = { viewModel.provideSpellingHint() },
                    onReset = { viewModel.resetSpellingWord() },
                    onNext = { viewModel.nextSpellingWord() }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Display the letter slots where letters are dropped
 */
@Composable
private fun SpellingSlotsRow(
    slots: List<Char?>,
    isCelebration: Boolean,
    onRemoveSlot: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("spelling_slots_row"),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        slots.forEachIndexed { index, char ->
            val scale by animateFloatAsState(
                targetValue = if (isCelebration) 1.08f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "slot_scale"
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isCelebration) Color(0xFFD1FAE5)
                else if (char != null) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (isCelebration) Color(0xFF10B981)
                        else if (char != null) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant
                    ),
                    width = if (char != null || isCelebration) 2.5.dp else 1.5.dp
                ),
                shadowElevation = if (char != null) 3.dp else 0.dp,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(56.dp)
                    .scale(scale)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(enabled = char != null) { onRemoveSlot(index) }
                    .testTag("spelling_slot_$index")
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char?.toString() ?: "_",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        color = if (isCelebration) Color(0xFF065F46)
                        else if (char != null) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Scrambled letters bank to tap
 */
@Composable
private fun SpellingLetterBank(
    tiles: List<LetterBankTile>,
    onTileClick: (LetterBankTile) -> Unit
) {
    val chunked = tiles.chunked(4)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("spelling_letter_bank"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        chunked.forEach { rowTiles ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowTiles.forEach { tile ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (tile.isUsed) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.surface,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (tile.isUsed) Color.Transparent else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            ),
                            width = 2.dp
                        ),
                        shadowElevation = if (tile.isUsed) 0.dp else 3.dp,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(enabled = !tile.isUsed) { onTileClick(tile) }
                            .testTag("spelling_tile_${tile.char}_${tile.id}")
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tile.char.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                fontSize = 26.sp,
                                color = if (tile.isUsed) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
                                else MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Bottom control buttons (Hint, Clear, Next Word)
 */
@Composable
private fun SpellingControlsBar(
    isCelebration: Boolean,
    onHint: () -> Unit,
    onReset: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onHint,
            enabled = !isCelebration,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .weight(0.4f)
                .height(50.dp)
                .testTag("spelling_hint_button")
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "Hint",
                tint = Color(0xFFD97706),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Hint",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFFD97706)
            )
        }

        OutlinedButton(
            onClick = onReset,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .weight(0.4f)
                .height(50.dp)
                .testTag("spelling_clear_button")
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Clear",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Clear",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Button(
            onClick = onNext,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCelebration) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .weight(0.6f)
                .height(50.dp)
                .testTag("spelling_next_button")
        ) {
            Text(
                text = "Next ➡️",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
        }
    }
}
