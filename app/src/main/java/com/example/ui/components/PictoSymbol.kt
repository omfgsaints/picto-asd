package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.WordCard
import com.example.data.repository.RealPhotoCatalog
import java.io.File

/**
 * High-visibility real photograph renderer for children with Autism Spectrum Disorder (ASD).
 * Prioritizes:
 * 1. User custom camera / gallery photo
 * 2. High-resolution bundled real photographic image (from res/drawable)
 * 3. Online web photo URL via Coil
 * 4. Fallback symbol badge
 */
@Composable
fun PictoSymbol(
    card: WordCard,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    fontSize: TextUnit = 38.sp
) {
    // 1. Check custom parent / camera photograph
    val localFile = card.customImagePath?.let { path ->
        val f = File(path)
        if (f.exists() && f.length() > 0) f else null
    }

    if (localFile != null) {
        AsyncImage(
            model = localFile,
            contentDescription = card.label,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, card.category.primaryColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        )
        return
    }

    // 2. Check bundled real actual photo from RealPhotoCatalog
    val localPhotoRes = RealPhotoCatalog.getDrawableForWord(card.word)
    if (localPhotoRes != null) {
        Image(
            painter = painterResource(id = localPhotoRes),
            contentDescription = card.label,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, card.category.primaryColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        )
        return
    }

    // 3. Check web image URL
    if (!card.imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = card.imageUrl,
            contentDescription = card.label,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, card.category.primaryColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        )
        return
    }

    // 4. Clean fallback badge
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(card.category.primaryColor.copy(alpha = 0.15f))
            .border(2.dp, card.category.primaryColor.copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = card.emoji,
            fontSize = fontSize,
            textAlign = TextAlign.Center
        )
    }
}
