package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.components.CategorySelector
import com.example.ui.components.PictoSymbol
import com.example.ui.components.SentenceIdentifyView
import com.example.ui.components.SentenceStripView
import com.example.ui.components.SentenceTrainingView
import com.example.ui.viewmodel.PictoWordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentenceStripScreen(
    viewModel: PictoWordViewModel,
    modifier: Modifier = Modifier
) {
    // 0: Sentence Builder, 1: Listen & Match, 2: Free AAC Board
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val sentenceCards by viewModel.sentenceCards.collectAsStateWithLifecycle()
    val activeSentenceWordIndex by viewModel.activeSentenceWordIndex.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    val displayCards = remember(selectedCategory) {
        if (selectedCategory != null) {
            PresetDictionary.allWords.filter { it.category == selectedCategory }
        } else {
            val priorityCats = listOf(
                WordCategory.COMMUNICATION,
                WordCategory.ACTIONS,
                WordCategory.FOOD,
                WordCategory.FEELINGS,
                WordCategory.TOYS
            )
            PresetDictionary.allWords.filter { it.category in priorityCats }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = when (selectedTab) {
                                0 -> "🧩 Sentence Builder"
                                1 -> "👂 Listen & Match"
                                else -> "🗣️ Free AAC Board"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )

                // 3 Clear Tabs for Sentence Learning & Communication
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "🧩 Build",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier.testTag("tab_sentence_builder")
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "👂 Listen & Match",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier.testTag("tab_sentence_identify")
                    )

                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                text = "🗣️ Free Board",
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier.testTag("tab_sentence_free_board")
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> {
                // Expressive Guided Sentence Builder
                SentenceTrainingView(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            1 -> {
                // Receptive Sentence Identification (Sentence Reading + Picture Matching)
                SentenceIdentifyView(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            else -> {
                // Open Freeform AAC Communication Board
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SentenceStripView(
                        cards = sentenceCards,
                        activePlayingIndex = activeSentenceWordIndex,
                        onCardClick = { _, card -> viewModel.speakCard(card) },
                        onRemoveCard = { index -> viewModel.removeSentenceCard(index) },
                        onClearAll = { viewModel.clearSentenceStrip() },
                        onSpeakAll = { viewModel.speakSentenceStrip() }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    CategorySelector(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { viewModel.selectCategory(it) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tap any card below to add to your sentence:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 100.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("aac_word_grid")
                    ) {
                        items(displayCards) { card ->
                            Card(
                                modifier = Modifier
                                    .height(118.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .testTag("aac_card_${card.word}"),
                                onClick = { viewModel.addToSentence(card) },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = card.category.lightBgColor),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(card.category.primaryColor.copy(alpha = 0.5f)),
                                    width = 2.dp
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                        size = 52.dp,
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
                    }
                }
            }
        }
    }
}
