package com.example.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.SentenceIdentifyItem
import com.example.data.model.WordCard
import com.example.data.repository.SentenceIdentifyCatalog
import com.example.ui.viewmodel.PictoWordViewModel

@Composable
fun SentenceIdentifyView(
    viewModel: PictoWordViewModel,
    modifier: Modifier = Modifier
) {
    val currentIndex by viewModel.currentIdentifyIndex.collectAsStateWithLifecycle()
    val currentItem by viewModel.currentIdentifyItem.collectAsStateWithLifecycle()
    val options by viewModel.identifyOptions.collectAsStateWithLifecycle()
    val feedback by viewModel.identifyFeedback.collectAsStateWithLifecycle()
    val isCelebration by viewModel.isIdentifyCelebration.collectAsStateWithLifecycle()
    val score by viewModel.identifyScore.collectAsStateWithLifecycle()

    val targetCard = SentenceIdentifyCatalog.getTargetCard(currentItem)

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Progress and Stars Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sentence ${currentIndex + 1} of ${SentenceIdentifyCatalog.items.size}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

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
                        text = "$score",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = Color(0xFFB45309)
                    )
                }
            }
        }

        if (isLandscape) {
            // Tablet Landscape Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column: Spoken Sentence Card & Actions
                Column(
                    modifier = Modifier
                        .weight(0.48f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SentenceSpeakerCard(
                        item = currentItem,
                        onReplaySentence = { viewModel.speakCurrentIdentifySentence() }
                    )

                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isCelebration) Color(0xFF059669) else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Button(
                        onClick = { viewModel.nextIdentifyItem() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("identify_next_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Next Sentence ➡️", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                // Right Column: 4 Choice Picture Cards
                Column(
                    modifier = Modifier
                        .weight(0.52f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Touch the matching picture:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    IdentifyChoiceCardsGrid(
                        options = options,
                        targetCard = targetCard,
                        isCelebration = isCelebration,
                        onSelectCard = { viewModel.onIdentifyAnswer(it) }
                    )
                }
            }
        } else {
            // Portrait Vertical Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SentenceSpeakerCard(
                    item = currentItem,
                    onReplaySentence = { viewModel.speakCurrentIdentifySentence() }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = feedback,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCelebration) Color(0xFF059669) else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Touch the matching picture:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                IdentifyChoiceCardsGrid(
                    options = options,
                    targetCard = targetCard,
                    isCelebration = isCelebration,
                    onSelectCard = { viewModel.onIdentifyAnswer(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.nextIdentifyItem() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("identify_next_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Next Sentence ➡️", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SentenceSpeakerCard(
    item: SentenceIdentifyItem,
    onReplaySentence: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("identify_speaker_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
            width = 1.5.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Listen carefully:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "\"${item.spokenSentence}\"",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontSize = 26.sp,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            FilledTonalButton(
                onClick = onReplaySentence,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("identify_replay_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Listen to sentence again",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Hear Sentence 🔊",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Composable
private fun IdentifyChoiceCardsGrid(
    options: List<WordCard>,
    targetCard: WordCard,
    isCelebration: Boolean,
    onSelectCard: (WordCard) -> Unit
) {
    val chunked = options.chunked(2)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        chunked.forEach { rowCards ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowCards.forEach { card ->
                    val isTarget = card.word.equals(targetCard.word, ignoreCase = true)
                    val highlightCorrect = isCelebration && isTarget

                    val scale by animateFloatAsState(
                        targetValue = if (highlightCorrect) 1.05f else 1.0f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "card_scale"
                    )

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .scale(scale)
                            .height(148.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .clickable { onSelectCard(card) }
                            .testTag("identify_card_${card.word}"),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (highlightCorrect) Color(0xFFD1FAE5) else card.category.lightBgColor
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (highlightCorrect) Color(0xFF10B981) else card.category.primaryColor.copy(alpha = 0.5f)
                            ),
                            width = if (highlightCorrect) 3.5.dp else 2.dp
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (highlightCorrect) 6.dp else 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                PictoSymbol(
                                    card = card,
                                    size = 68.dp,
                                    fontSize = 42.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = card.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 19.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                            }

                            if (highlightCorrect) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(26.dp)
                                        .background(Color(0xFF10B981), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✓",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
