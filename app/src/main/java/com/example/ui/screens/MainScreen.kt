package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.PictoWordViewModel

enum class AppNavDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    EXPLORER("Words", Icons.Filled.AutoStories, Icons.Outlined.AutoStories, "nav_explorer"),
    AAC_STRIP("Sentence", Icons.Filled.RecordVoiceOver, Icons.Filled.ChatBubble, "nav_sentence"),
    QUIZ("Quiz", Icons.Filled.School, Icons.Outlined.School, "nav_quiz"),
    MY_WORDS("My Cards", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder, "nav_my_words")
}

@Composable
fun MainScreen(
    viewModel: PictoWordViewModel = viewModel()
) {
    val isCalmMode by viewModel.isCalmMode.collectAsStateWithLifecycle()
    var currentDestination by rememberSaveable { mutableStateOf(AppNavDestination.EXPLORER) }

    // System BackHandler to safely return to Explorer tab if in secondary tab
    BackHandler(enabled = currentDestination != AppNavDestination.EXPLORER) {
        currentDestination = AppNavDestination.EXPLORER
    }

    MyApplicationTheme(isCalmMode = isCalmMode) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    AppNavDestination.entries.forEach { destination ->
                        val isSelected = currentDestination == destination
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentDestination = destination },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = destination.title
                                )
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag(destination.testTag)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentDestination) {
                    AppNavDestination.EXPLORER -> {
                        WordExplorerScreen(viewModel = viewModel)
                    }
                    AppNavDestination.AAC_STRIP -> {
                        SentenceStripScreen(viewModel = viewModel)
                    }
                    AppNavDestination.QUIZ -> {
                        QuizGameScreen(viewModel = viewModel)
                    }
                    AppNavDestination.MY_WORDS -> {
                        SavedWordsScreen(
                            viewModel = viewModel,
                            onWordCardSelected = { card ->
                                viewModel.selectWord(card)
                                currentDestination = AppNavDestination.EXPLORER
                            }
                        )
                    }
                }
            }
        }
    }
}
