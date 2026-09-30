package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WordCategory

@Composable
fun CategorySelector(
    selectedCategory: WordCategory?,
    onCategorySelected: (WordCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "All" option
        val isAllSelected = selectedCategory == null
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable { onCategorySelected(null) }
                .testTag("category_all"),
            shape = RoundedCornerShape(16.dp),
            color = if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = if (isAllSelected) 4.dp else 0.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🌟", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "All Words",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isAllSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Each specific category
        WordCategory.entries.filter { it != WordCategory.GENERAL }.forEach { category ->
            val isSelected = selectedCategory == category
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onCategorySelected(if (isSelected) null else category) }
                    .testTag("category_${category.name.lowercase()}"),
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) category.primaryColor else MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = if (isSelected) 4.dp else 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = category.emoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = category.displayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
