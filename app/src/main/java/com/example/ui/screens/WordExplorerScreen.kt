package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.WordCard
import com.example.ui.components.CategorySelector
import com.example.ui.components.PictureCardView
import com.example.ui.components.QuickWordKeyboard
import com.example.ui.components.SensorySettingsDialog
import com.example.ui.components.SentenceStripView
import com.example.ui.viewmodel.PictoWordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordExplorerScreen(
    viewModel: PictoWordViewModel,
    modifier: Modifier = Modifier
) {
    val typedQuery by viewModel.typedQuery.collectAsStateWithLifecycle()
    val currentCard by viewModel.currentCard.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isCurrentCardFavorite.collectAsStateWithLifecycle()
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val sentenceCards by viewModel.sentenceCards.collectAsStateWithLifecycle()
    val activeSentenceWordIndex by viewModel.activeSentenceWordIndex.collectAsStateWithLifecycle()
    val activeLetterHighlight by viewModel.activeLetterHighlight.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.ttsHelper.isSpeaking.collectAsStateWithLifecycle()
    val isBigKeyboardOpen by viewModel.isBigKeyboardOpen.collectAsStateWithLifecycle()
    val isCalmMode by viewModel.isCalmMode.collectAsStateWithLifecycle()
    val speechRate by viewModel.ttsHelper.speechRate.collectAsStateWithLifecycle()
    val isHapticEnabled by viewModel.isHapticEnabled.collectAsStateWithLifecycle()

    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val isFetchingOnline by viewModel.isFetchingOnline.collectAsStateWithLifecycle()
    val isAutoFetchOnline by viewModel.isAutoFetchOnlineEnabled.collectAsStateWithLifecycle()

    var showSettingsDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    if (showSettingsDialog) {
        SensorySettingsDialog(
            speechRate = speechRate,
            isCalmMode = isCalmMode,
            isHapticEnabled = isHapticEnabled,
            isAutoFetchOnline = isAutoFetchOnline,
            onSpeechRateChange = { viewModel.setSpeechRate(it) },
            onCalmModeChange = { viewModel.setCalmMode(it) },
            onHapticChange = { viewModel.setHapticEnabled(it) },
            onAutoFetchOnlineChange = { viewModel.setAutoFetchOnline(it) },
            onDismiss = { showSettingsDialog = false }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🎨 PictoWord",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    // Big Keyboard Toggle Button
                    IconButton(
                        onClick = { viewModel.toggleBigKeyboard() },
                        modifier = Modifier.testTag("toggle_big_keyboard_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = "Toggle Big Keyboard",
                            tint = if (isBigKeyboardOpen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Settings Button
                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Sensory Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Online / Offline Status Chip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isOnline) Color(0xFFEFF6FF) else Color(0xFFF1F5F9),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isOnline) Color(0xFF3B82F6).copy(alpha = 0.4f) else Color(0xFF94A3B8)
                        ),
                        width = 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isOnline) "🌐 Online: Web Pictures Active" else "📴 Offline: Saved Pictures Active",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isOnline) Color(0xFF1D4ED8) else Color(0xFF475569)
                        )
                    }
                }

                if (isOnline && isAutoFetchOnline) {
                    Text(
                        text = "Auto-caching on",
                        fontSize = 11.sp,
                        color = Color(0xFF059669),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Big Tactile Typing Bar
            OutlinedTextField(
                value = typedQuery,
                onValueChange = { viewModel.onQueryChange(it) },
                placeholder = {
                    Text(
                        text = "Type any word here…",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                },
                trailingIcon = {
                    if (typedQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.onClearTyped() },
                            modifier = Modifier.testTag("clear_typed_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear typed text",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    viewModel.speakCurrentCard()
                }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("word_type_input")
            )

            // Optional Big Sensory Alphabet Board
            AnimatedVisibility(
                visible = isBigKeyboardOpen,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    QuickWordKeyboard(
                        onLetterClick = { char -> viewModel.onLetterTyped(char) },
                        onSpaceClick = { viewModel.onSpaceTyped() },
                        onBackspaceClick = { viewModel.onBackspaceTyped() },
                        onClearClick = { viewModel.onClearTyped() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Pills
            CategorySelector(
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.selectCategory(it) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Word Suggestions Chips (Instant visual cards to explore)
            if (suggestions.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(suggestions) { card ->
                        val isCurrent = card.word.equals(currentCard.word, ignoreCase = true)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    focusManager.clearFocus()
                                    viewModel.selectWord(card)
                                }
                                .testTag("suggestion_${card.word}"),
                            shape = RoundedCornerShape(14.dp),
                            color = if (isCurrent) card.category.lightBgColor else MaterialTheme.colorScheme.surface,
                            border = CardDefaults.outlinedCardBorder().copy(
                                width = if (isCurrent) 2.dp else 1.dp,
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (isCurrent) card.category.primaryColor else MaterialTheme.colorScheme.outlineVariant
                                )
                            ),
                            shadowElevation = if (isCurrent) 2.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = card.emoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = card.label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isCurrent) card.category.primaryColor else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // HERO COMPONENT: The Live Visual Picture Card
            PictureCardView(
                card = currentCard,
                isSpeaking = isSpeaking,
                activeLetterHighlightIndex = activeLetterHighlight,
                isFavorite = isFavorite,
                isOnline = isOnline,
                isFetchingOnline = isFetchingOnline,
                onSpeakClick = { viewModel.speakCurrentCard() },
                onSlowSpeakClick = { viewModel.speakCurrentCardSlow() },
                onSpellClick = { viewModel.spellCurrentCard() },
                onToggleFavorite = { viewModel.toggleFavoriteCurrent() },
                onAddToSentence = { viewModel.addToSentence() },
                onFetchOnlineImage = { viewModel.fetchOnlineImageForCurrentCard() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sentence Strip Area
            SentenceStripView(
                cards = sentenceCards,
                activePlayingIndex = activeSentenceWordIndex,
                onCardClick = { _, card -> viewModel.speakCard(card) },
                onRemoveCard = { index -> viewModel.removeSentenceCard(index) },
                onClearAll = { viewModel.clearSentenceStrip() },
                onSpeakAll = { viewModel.speakSentenceStrip() }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
