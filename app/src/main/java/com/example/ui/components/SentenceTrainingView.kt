package com.example.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SentenceExercise
import com.example.data.model.WordCard
import com.example.data.repository.SentenceTrainingCatalog
import com.example.ui.viewmodel.PictoWordViewModel

@Composable
fun SentenceTrainingView(
    viewModel: PictoWordViewModel,
    modifier: Modifier = Modifier
) {
    val currentLevelNumber by viewModel.currentTrainingLevel.collectAsStateWithLifecycle()
    val currentExerciseIndex by viewModel.currentExerciseIndex.collectAsStateWithLifecycle()
    val currentExercise by viewModel.currentExercise.collectAsStateWithLifecycle()
    val placedCards by viewModel.trainingPlacedCards.collectAsStateWithLifecycle()
    val availableChoices by viewModel.trainingAvailableChoices.collectAsStateWithLifecycle()
    val feedback by viewModel.trainingFeedback.collectAsStateWithLifecycle()
    val isCompleted by viewModel.isTrainingCompleted.collectAsStateWithLifecycle()
    val isCelebration by viewModel.isTrainingCelebration.collectAsStateWithLifecycle()
    val stars by viewModel.trainingStars.collectAsStateWithLifecycle()

    val currentLevel = SentenceTrainingCatalog.levels.find { it.levelNumber == currentLevelNumber }
        ?: SentenceTrainingCatalog.levels[0]

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Level Tabs Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(SentenceTrainingCatalog.levels) { level ->
                val isSelected = level.levelNumber == currentLevelNumber
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        ),
                        width = if (isSelected) 2.dp else 1.dp
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.selectTrainingLevel(level.levelNumber) }
                        .testTag("training_level_${level.levelNumber}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = level.icon, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Lvl ${level.levelNumber}",
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Header Bar: Exercise Progress & Star Counter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        if (currentExerciseIndex > 0) {
                            viewModel.selectExercise(currentExerciseIndex - 1)
                        }
                    },
                    enabled = currentExerciseIndex > 0,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous exercise",
                        tint = if (currentExerciseIndex > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }

                Text(
                    text = "${currentExerciseIndex + 1} / ${currentLevel.exercises.size}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                IconButton(
                    onClick = {
                        if (currentExerciseIndex < currentLevel.exercises.size - 1) {
                            viewModel.selectExercise(currentExerciseIndex + 1)
                        }
                    },
                    enabled = currentExerciseIndex < currentLevel.exercises.size - 1,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next exercise",
                        tint = if (currentExerciseIndex < currentLevel.exercises.size - 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = currentExercise.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Star Counter
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
                        fontSize = 14.sp,
                        color = Color(0xFFB45309)
                    )
                }
            }
        }

        if (isLandscape) {
            // Tablet Landscape Dual-Pane Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column: Prompt, Slots & Celebration Controls
                Column(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TrainingPromptCard(
                        exercise = currentExercise,
                        onReplayPrompt = { viewModel.replayTrainingPrompt() }
                    )

                    SentenceSlotsRow(
                        exercise = currentExercise,
                        placedCards = placedCards,
                        isCompleted = isCompleted,
                        onSlotClick = { index -> viewModel.removePlacedTrainingCard(index) }
                    )

                    FeedbackAndActionArea(
                        feedback = feedback,
                        isCompleted = isCompleted,
                        isCelebration = isCelebration,
                        onReplayFullSentence = { viewModel.speakTrainingFullSentence() },
                        onNextExercise = { viewModel.nextTrainingExercise() },
                        onReset = { viewModel.resetTrainingExercise() }
                    )
                }

                // Right Column: Choice Bank Grid
                Column(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Touch a word to add:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    ChoiceBankGrid(
                        choices = availableChoices,
                        onCardTapped = { viewModel.onTrainingCardTapped(it) }
                    )
                }
            }
        } else {
            // Portrait Vertical Flow
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TrainingPromptCard(
                    exercise = currentExercise,
                    onReplayPrompt = { viewModel.replayTrainingPrompt() }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SentenceSlotsRow(
                    exercise = currentExercise,
                    placedCards = placedCards,
                    isCompleted = isCompleted,
                    onSlotClick = { index -> viewModel.removePlacedTrainingCard(index) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                FeedbackAndActionArea(
                    feedback = feedback,
                    isCompleted = isCompleted,
                    isCelebration = isCelebration,
                    onReplayFullSentence = { viewModel.speakTrainingFullSentence() },
                    onNextExercise = { viewModel.nextTrainingExercise() },
                    onReset = { viewModel.resetTrainingExercise() }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Touch a word to add:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                ChoiceBankGrid(
                    choices = availableChoices,
                    onCardTapped = { viewModel.onTrainingCardTapped(it) }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TrainingPromptCard(
    exercise: SentenceExercise,
    onReplayPrompt: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("training_prompt_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.emoji,
                    fontSize = 38.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )

                Column {
                    Text(
                        text = "Sentence Goal:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = exercise.promptInstruction,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            FilledTonalButton(
                onClick = onReplayPrompt,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("training_hear_prompt_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Hear target sentence",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Listen", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun SentenceSlotsRow(
    exercise: SentenceExercise,
    placedCards: List<WordCard?>,
    isCompleted: Boolean,
    onSlotClick: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("training_sentence_strip"),
        shape = RoundedCornerShape(20.dp),
        color = if (isCompleted) Color(0xFFECFDF5) else MaterialTheme.colorScheme.surface,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            ),
            width = if (isCompleted) 2.dp else 1.5.dp
        ),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                exercise.targetWords.indices.forEach { index ->
                    val placed = placedCards.getOrNull(index)
                    val hint = exercise.slotHints.getOrNull(index) ?: "Word ${index + 1}"
                    val isNextToFill = placedCards.indexOfFirst { it == null } == index

                    TrainingSlotItem(
                        placedCard = placed,
                        slotHint = hint,
                        isNextToFill = isNextToFill,
                        isCompleted = isCompleted,
                        onSlotClick = { onSlotClick(index) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TrainingSlotItem(
    placedCard: WordCard?,
    slotHint: String,
    isNextToFill: Boolean,
    isCompleted: Boolean,
    onSlotClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (placedCard != null) 1.02f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "slot_scale"
    )

    if (placedCard != null) {
        // Filled Slot Card
        Card(
            modifier = modifier
                .scale(scale)
                .height(115.dp)
                .clip(RoundedCornerShape(18.dp))
                .clickable { onSlotClick() }
                .testTag("filled_slot_${placedCard.word}"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isCompleted) Color(0xFFD1FAE5) else placedCard.category.lightBgColor
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(
                    if (isCompleted) Color(0xFF10B981) else placedCard.category.primaryColor
                ),
                width = 2.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    PictoSymbol(
                        card = placedCard,
                        size = 46.dp,
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = placedCard.label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Tiny remove cross
                if (!isCompleted) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(18.dp)
                            .background(Color.Black.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove card",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    } else {
        // Empty Placeholder Slot
        Box(
            modifier = modifier
                .height(115.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (isNextToFill) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
                .border(
                    width = if (isNextToFill) 2.dp else 1.5.dp,
                    color = if (isNextToFill) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isNextToFill) "👇" else "➕",
                    fontSize = 26.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = slotHint,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = if (isNextToFill) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun FeedbackAndActionArea(
    feedback: String,
    isCompleted: Boolean,
    isCelebration: Boolean,
    onReplayFullSentence: () -> Unit,
    onNextExercise: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = feedback,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isCelebration) Color(0xFF059669) else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        if (isCompleted) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReplayFullSentence,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(0.45f)
                        .height(48.dp)
                        .testTag("training_replay_sentence_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Hear 🔊", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNextExercise,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .weight(0.55f)
                        .height(48.dp)
                        .testTag("training_next_exercise_button")
                ) {
                    Text("Next ➡️", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = onReset,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("training_reset_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear Slots", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun ChoiceBankGrid(
    choices: List<WordCard>,
    onCardTapped: (WordCard) -> Unit
) {
    if (choices.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✨ All words placed! Great job!",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF059669),
                fontWeight = FontWeight.Bold
            )
        }
        return
    }

    val chunked = choices.chunked(2)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        chunked.forEach { rowCards ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowCards.forEach { card ->
                    TrainingChoiceCard(
                        card = card,
                        onCardTapped = { onCardTapped(card) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill space if odd number in last row
                if (rowCards.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TrainingChoiceCard(
    card: WordCard,
    onCardTapped: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(115.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onCardTapped() }
            .testTag("choice_card_${card.word}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = card.category.lightBgColor),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(card.category.primaryColor.copy(alpha = 0.5f)),
            width = 2.dp
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PictoSymbol(
                card = card,
                size = 50.dp,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = card.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
