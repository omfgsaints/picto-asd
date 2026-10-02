package com.example.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.dictionary.PresetDictionary
import com.example.data.model.WordCard
import com.example.data.model.WordCategory
import com.example.ui.components.PictoSymbol
import com.example.ui.components.SentenceStripView
import com.example.ui.viewmodel.PictoWordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AacBoardScreen(
    viewModel: PictoWordViewModel,
    modifier: Modifier = Modifier
) {
    val sentenceCards by viewModel.sentenceCards.collectAsStateWithLifecycle()
    val activeSentenceWordIndex by viewModel.activeSentenceWordIndex.collectAsStateWithLifecycle()

    var selectedCategory by rememberSaveable { mutableStateOf<WordCategory?>(WordCategory.COMMUNICATION) }
    var isBigGridMode by rememberSaveable { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // High-frequency Core Vocabulary cards always available for rapid communication
    val coreWords = remember {
        listOf(
            "yes", "no", "i want", "i feel", "help", "more", "stop", "all done", "please", "thank you"
        ).map { PresetDictionary.findOrGenerate(it) }
    }

    val categoryWords = remember(selectedCategory) {
        val cat = selectedCategory
        if (cat != null) {
            PresetDictionary.allWords.filter { it.category == cat }
        } else {
            PresetDictionary.allWords
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🗣️ AAC Communication Board",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 22.sp
                        )
                    }
                },
                actions = {
                    // Density Toggle (Big 2-col vs Standard 3-col)
                    IconButton(
                        onClick = { isBigGridMode = !isBigGridMode },
                        modifier = Modifier.testTag("aac_grid_density_toggle")
                    ) {
                        Icon(
                            imageVector = if (isBigGridMode) Icons.Default.Grid3x3 else Icons.Default.GridView,
                            contentDescription = "Toggle Grid Size",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
        ) {
            // ==================== 1. FIXED LIVE MESSAGE STRIP ====================
            SentenceStripView(
                cards = sentenceCards,
                activePlayingIndex = activeSentenceWordIndex,
                onCardClick = { _, card -> viewModel.speakCard(card) },
                onRemoveCard = { index -> viewModel.removeSentenceCard(index) },
                onClearAll = { viewModel.clearSentenceStrip() },
                onSpeakAll = { viewModel.speakSentenceStrip() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ==================== 2. QUICK CORE VOCABULARY ROW ====================
            Text(
                text = "⚡ Quick Words",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(coreWords) { card ->
                    AacCoreWordPill(
                        card = card,
                        onClick = { viewModel.addToSentence(card) }
                    )
                }
            }

            // ==================== 3. CATEGORY SELECTOR TABS ====================
            ScrollableTabRow(
                selectedTabIndex = WordCategory.values().indexOf(selectedCategory).coerceAtLeast(0),
                edgePadding = 4.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                WordCategory.values().forEach { category ->
                    val isSelected = selectedCategory == category
                    Tab(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(text = category.emoji, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = category.displayName,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        },
                        modifier = Modifier.testTag("aac_tab_${category.name.lowercase()}")
                    )
                }
            }

            // ==================== 4. OVERSIZED PICTORIAL CARDS GRID ====================
            val gridColumns = when {
                isBigGridMode -> GridCells.Fixed(if (isLandscape) 3 else 2)
                isLandscape -> GridCells.Adaptive(minSize = 130.dp)
                else -> GridCells.Adaptive(minSize = 110.dp)
            }

            LazyVerticalGrid(
                columns = gridColumns,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("aac_board_grid")
            ) {
                items(categoryWords) { card ->
                    AacBoardCardItem(
                        card = card,
                        isLarge = isBigGridMode,
                        onClick = {
                            viewModel.addToSentence(card)
                        }
                    )
                }
            }
        }
    }
}

/**
 * High-contrast Quick Core Word button
 */
@Composable
private fun AacCoreWordPill(
    card: WordCard,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = card.category.lightBgColor,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(card.category.primaryColor),
            width = 2.dp
        ),
        shadowElevation = 2.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("core_pill_${card.word}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PictoSymbol(
                card = card,
                size = 32.dp,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = card.label.uppercase(),
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Large, pictorial AAC communication card with big fonts
 */
@Composable
private fun AacBoardCardItem(
    card: WordCard,
    isLarge: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardHeight = if (isLarge) 140.dp else 115.dp
    val symbolSize = if (isLarge) 64.dp else 52.dp
    val labelFontSize = if (isLarge) 20.sp else 17.sp

    Card(
        modifier = modifier
            .height(cardHeight)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag("aac_tile_${card.word}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = card.category.lightBgColor
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(card.category.primaryColor),
            width = 2.5.dp
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
                size = symbolSize,
                fontSize = if (isLarge) 44.sp else 36.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = card.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                fontSize = labelFontSize,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
