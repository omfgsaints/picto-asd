package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.WordCard
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PictureCardView(
    card: WordCard,
    isSpeaking: Boolean,
    activeLetterHighlightIndex: Int,
    isFavorite: Boolean,
    isOnline: Boolean,
    isFetchingOnline: Boolean,
    onSpeakClick: () -> Unit,
    onSlowSpeakClick: () -> Unit,
    onSpellClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToSentence: () -> Unit,
    onFetchOnlineImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Allows toggling between Photo and Pictogram if a photo exists
    var showPhotoView by remember(card.word, card.customImagePath, card.imageUrl) {
        mutableStateOf(card.customImagePath != null || card.imageUrl != null)
    }

    val localFile = remember(card.customImagePath) {
        card.customImagePath?.let { path ->
            val f = File(path)
            if (f.exists() && f.length() > 0) f else null
        }
    }

    val hasPhotoAvailable = localFile != null || !card.imageUrl.isNullOrBlank()

    val scaleAnim by animateFloatAsState(
        targetValue = if (isSpeaking) 1.03f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSpeaking) card.category.primaryColor else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "border_color"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scaleAnim)
            .shadow(
                elevation = if (isSpeaking) 12.dp else 4.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = card.category.primaryColor.copy(alpha = 0.35f)
            )
            .border(
                width = if (isSpeaking) 4.dp else 2.dp,
                color = if (isSpeaking) borderColor else card.category.primaryColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(28.dp)
            )
            .testTag("picture_card_${card.word}"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Category badge, Offline status & Favorite button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = card.category.lightBgColor,
                    modifier = Modifier.testTag("category_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = card.category.emoji,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = card.category.displayName,
                            color = card.category.primaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Offline Cached Badge
                    if (card.isDownloadedForOffline || localFile != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFD1FAE5),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OfflinePin,
                                    contentDescription = "Saved for Offline",
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Offline Ready",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46)
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("favorite_button")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Visual Picture Area
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(card.category.lightBgColor)
                    .border(
                        width = 2.dp,
                        color = card.category.primaryColor.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .clickable { onSpeakClick() }
                    .testTag("picture_tap_area"),
                contentAlignment = Alignment.Center
            ) {
                // If local offline file or web URL exists and photo view is selected, render image
                if (showPhotoView && (localFile != null || card.imageUrl != null)) {
                    val imageSource = localFile ?: card.imageUrl
                    AsyncImage(
                        model = imageSource,
                        contentDescription = card.label,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Fallback to high-contrast crisp pictorial symbol
                    PictoSymbol(
                        card = card,
                        size = 160.dp,
                        fontSize = 80.sp
                    )
                }

                // Volume indicator icon in corner
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(card.category.primaryColor)
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Tap to speak",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Switch between Photo & Pictogram view button
                if (hasPhotoAvailable) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable { showPhotoView = !showPhotoView }
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Photo,
                            contentDescription = "Toggle photo or icon",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Online Fetch & Offline Status Action Bar
            Spacer(modifier = Modifier.height(10.dp))
            if (isFetchingOnline) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = card.category.primaryColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Downloading picture for offline use…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (!hasPhotoAvailable && isOnline) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onFetchOnlineImage() }
                        .testTag("fetch_online_photo_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Download Real Photo from Web",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Word Label (Large Bold Legible for Autistic / Early Readers)
            Text(
                text = card.label,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("word_label")
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Syllables / Letter Breakdown with dynamic spelling highlight!
            FlowRow(
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                val cleanWordUpper = card.word.uppercase()
                if (activeLetterHighlightIndex >= 0 && activeLetterHighlightIndex < cleanWordUpper.length) {
                    // Spell out mode: Individual letter chips with active bouncing highlight
                    cleanWordUpper.forEachIndexed { index, char ->
                        val isActive = index == activeLetterHighlightIndex
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isActive) card.category.primaryColor else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .padding(horizontal = 3.dp, vertical = 2.dp)
                                .scale(if (isActive) 1.25f else 1.0f)
                        ) {
                            Text(
                                text = char.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                } else {
                    // Normal mode: Display syllable breakdown
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = card.syllables,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Description / Simple sentence
            if (card.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = card.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            if (card.exampleSentence.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "“${card.exampleSentence}”",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = card.category.primaryColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Interactive Audio & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Main Speak Button
                Button(
                    onClick = onSpeakClick,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(56.dp)
                        .testTag("hear_word_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = card.category.primaryColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Read Aloud",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Slow Speech Button
                FilledTonalButton(
                    onClick = onSlowSpeakClick,
                    modifier = Modifier
                        .weight(0.9f)
                        .height(56.dp)
                        .testTag("slow_speech_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = card.category.lightBgColor,
                        contentColor = card.category.primaryColor
                    )
                ) {
                    Text(
                        text = "🐢 Slow",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary Buttons: Spell It Out + Add to Sentence Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onSpellClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("spell_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Spellcheck,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Spell Out",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                OutlinedButton(
                    onClick = onAddToSentence,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("add_to_strip_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+ Strip",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
