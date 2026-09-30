package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.ui.components.SentenceStripView
import com.example.ui.viewmodel.PictoWordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentenceStripScreen(
    viewModel: PictoWordViewModel,
    modifier: Modifier = Modifier
) {
    val sentenceCards by viewModel.sentenceCards.collectAsStateWithLifecycle()
    val activeSentenceWordIndex by viewModel.activeSentenceWordIndex.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    val displayCards = remember(selectedCategory) {
        if (selectedCategory != null) {
            PresetDictionary.allWords.filter { it.category == selectedCategory }
        } else {
            // Curate most helpful AAC words first: Communication + Common Actions + Foods + Feelings
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
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🗣️ AAC Sentence Board",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Interactive Sentence Strip Header
            SentenceStripView(
                cards = sentenceCards,
                activePlayingIndex = activeSentenceWordIndex,
                onCardClick = { _, card -> viewModel.speakCard(card) },
                onRemoveCard = { index -> viewModel.removeSentenceCard(index) },
                onClearAll = { viewModel.clearSentenceStrip() },
                onSpeakAll = { viewModel.speakSentenceStrip() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Bar
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

            // Visual Grid of Cards
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("aac_grid")
            ) {
                items(displayCards) { card ->
                    AacTileCard(
                        card = card,
                        onClick = { viewModel.addToSentence(card) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AacTileCard(
    card: WordCard,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(115.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("aac_card_${card.word}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = card.category.lightBgColor
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(card.category.primaryColor.copy(alpha = 0.4f)),
            width = 1.5.dp
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
            val localFile = remember(card.customImagePath) {
                card.customImagePath?.let { path ->
                    val f = java.io.File(path)
                    if (f.exists() && f.length() > 0) f else null
                }
            }

            if (localFile != null || !card.imageUrl.isNullOrBlank()) {
                coil.compose.AsyncImage(
                    model = localFile ?: card.imageUrl,
                    contentDescription = card.label,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            } else {
                Text(
                    text = card.emoji,
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = card.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
