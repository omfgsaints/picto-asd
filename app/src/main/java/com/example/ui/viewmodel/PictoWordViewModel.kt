package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.dictionary.PresetDictionary
import com.example.data.local.AppDatabase
import com.example.data.model.LetterBankTile
import com.example.data.model.SentenceExercise
import com.example.data.model.SentenceIdentifyItem
import com.example.data.model.SpellingWordItem
import com.example.data.model.WordCard
import com.example.data.model.WordCategory
import com.example.data.network.NetworkMonitor
import com.example.data.network.OnlineImageFetcher
import com.example.data.repository.SentenceIdentifyCatalog
import com.example.data.repository.SentenceTrainingCatalog
import com.example.data.repository.SpellingCatalog
import com.example.data.repository.WordRepository
import com.example.data.tts.TextToSpeechHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PictoWordViewModel(application: Application) : AndroidViewModel(application) {

    private val imageFetcher = OnlineImageFetcher(application)
    private val networkMonitor = NetworkMonitor(application)
    private val repository: WordRepository
    val ttsHelper = TextToSpeechHelper(application)

    private val vibrator: Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (_: Exception) {
        null
    }

    // Network Online/Offline state
    val isOnline: StateFlow<Boolean> = networkMonitor.isOnlineFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        networkMonitor.isCurrentlyConnected()
    )

    private val _isFetchingOnline = MutableStateFlow(false)
    val isFetchingOnline: StateFlow<Boolean> = _isFetchingOnline.asStateFlow()

    private val _isAutoFetchOnlineEnabled = MutableStateFlow(true)
    val isAutoFetchOnlineEnabled: StateFlow<Boolean> = _isAutoFetchOnlineEnabled.asStateFlow()

    // Input & Current Card
    private val _typedQuery = MutableStateFlow("apple")
    val typedQuery: StateFlow<String> = _typedQuery.asStateFlow()

    private val _currentCard = MutableStateFlow(
        PresetDictionary.findOrGenerate("apple").let { card ->
            val local = imageFetcher.getLocalOfflineImage(card.word)
            if (local != null) card.copy(customImagePath = local.absolutePath, isDownloadedForOffline = true) else card
        }
    )
    val currentCard: StateFlow<WordCard> = _currentCard.asStateFlow()

    private val _selectedCategory = MutableStateFlow<WordCategory?>(null)
    val selectedCategory: StateFlow<WordCategory?> = _selectedCategory.asStateFlow()

    private val _suggestions = MutableStateFlow(PresetDictionary.searchSuggestions("apple"))
    val suggestions: StateFlow<List<WordCard>> = _suggestions.asStateFlow()

    // Sentence Strip
    private val _sentenceCards = MutableStateFlow<List<WordCard>>(emptyList())
    val sentenceCards: StateFlow<List<WordCard>> = _sentenceCards.asStateFlow()

    private val _activeSentenceWordIndex = MutableStateFlow(-1)
    val activeSentenceWordIndex: StateFlow<Int> = _activeSentenceWordIndex.asStateFlow()

    // Spelling Highlight
    private val _activeLetterHighlight = MutableStateFlow(-1)
    val activeLetterHighlight: StateFlow<Int> = _activeLetterHighlight.asStateFlow()

    // Sensory & Accessibility Settings
    private val _isCalmMode = MutableStateFlow(false)
    val isCalmMode: StateFlow<Boolean> = _isCalmMode.asStateFlow()

    private val _isBigKeyboardOpen = MutableStateFlow(false)
    val isBigKeyboardOpen: StateFlow<Boolean> = _isBigKeyboardOpen.asStateFlow()

    private val _isHapticEnabled = MutableStateFlow(true)
    val isHapticEnabled: StateFlow<Boolean> = _isHapticEnabled.asStateFlow()

    // Favorites & History from Room
    val favoritesList: StateFlow<List<WordCard>>
    val recentWordsList: StateFlow<List<WordCard>>
    val customWordsList: StateFlow<List<WordCard>>

    private val _isCurrentCardFavorite = MutableStateFlow(false)
    val isCurrentCardFavorite: StateFlow<Boolean> = _isCurrentCardFavorite.asStateFlow()

    // Quiz Mode
    private val _quizTargetCard = MutableStateFlow<WordCard?>(null)
    val quizTargetCard: StateFlow<WordCard?> = _quizTargetCard.asStateFlow()

    private val _quizOptions = MutableStateFlow<List<WordCard>>(emptyList())
    val quizOptions: StateFlow<List<WordCard>> = _quizOptions.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _quizFeedback = MutableStateFlow("Listen carefully and tap the matching card!")
    val quizFeedback: StateFlow<String> = _quizFeedback.asStateFlow()

    private val _quizCelebration = MutableStateFlow(false)
    val quizCelebration: StateFlow<Boolean> = _quizCelebration.asStateFlow()

    // Sentence Training State
    private val _currentTrainingLevel = MutableStateFlow(1)
    val currentTrainingLevel: StateFlow<Int> = _currentTrainingLevel.asStateFlow()

    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex: StateFlow<Int> = _currentExerciseIndex.asStateFlow()

    private val _currentExercise = MutableStateFlow(SentenceTrainingCatalog.levels[0].exercises[0])
    val currentExercise: StateFlow<SentenceExercise> = _currentExercise.asStateFlow()

    private val _trainingPlacedCards = MutableStateFlow<List<WordCard?>>(listOf(null, null))
    val trainingPlacedCards: StateFlow<List<WordCard?>> = _trainingPlacedCards.asStateFlow()

    private val _trainingAvailableChoices = MutableStateFlow<List<WordCard>>(emptyList())
    val trainingAvailableChoices: StateFlow<List<WordCard>> = _trainingAvailableChoices.asStateFlow()

    private val _trainingFeedback = MutableStateFlow("Tap the cards in order to build the sentence!")
    val trainingFeedback: StateFlow<String> = _trainingFeedback.asStateFlow()

    private val _isTrainingCompleted = MutableStateFlow(false)
    val isTrainingCompleted: StateFlow<Boolean> = _isTrainingCompleted.asStateFlow()

    private val _isTrainingCelebration = MutableStateFlow(false)
    val isTrainingCelebration: StateFlow<Boolean> = _isTrainingCelebration.asStateFlow()

    private val _trainingStars = MutableStateFlow(0)
    val trainingStars: StateFlow<Int> = _trainingStars.asStateFlow()

    // Sentence Identification (Receptive Matching) State
    private val _currentIdentifyIndex = MutableStateFlow(0)
    val currentIdentifyIndex: StateFlow<Int> = _currentIdentifyIndex.asStateFlow()

    private val _currentIdentifyItem = MutableStateFlow(SentenceIdentifyCatalog.items[0])
    val currentIdentifyItem: StateFlow<SentenceIdentifyItem> = _currentIdentifyItem.asStateFlow()

    private val _identifyOptions = MutableStateFlow<List<WordCard>>(emptyList())
    val identifyOptions: StateFlow<List<WordCard>> = _identifyOptions.asStateFlow()

    private val _identifyFeedback = MutableStateFlow("Listen to the sentence, then tap the matching picture!")
    val identifyFeedback: StateFlow<String> = _identifyFeedback.asStateFlow()

    private val _isIdentifyCelebration = MutableStateFlow(false)
    val isIdentifyCelebration: StateFlow<Boolean> = _isIdentifyCelebration.asStateFlow()

    private val _identifyScore = MutableStateFlow(0)
    val identifyScore: StateFlow<Int> = _identifyScore.asStateFlow()

    // Spelling Game ("Spell the Picture") State
    private val _currentSpellingItem = MutableStateFlow(SpellingCatalog.items[0])
    val currentSpellingItem: StateFlow<SpellingWordItem> = _currentSpellingItem.asStateFlow()

    private val _spellingSlots = MutableStateFlow<List<Char?>>(listOf(null, null, null))
    val spellingSlots: StateFlow<List<Char?>> = _spellingSlots.asStateFlow()

    private val _spellingTiles = MutableStateFlow<List<LetterBankTile>>(emptyList())
    val spellingTiles: StateFlow<List<LetterBankTile>> = _spellingTiles.asStateFlow()

    private val _isSpellingCelebration = MutableStateFlow(false)
    val isSpellingCelebration: StateFlow<Boolean> = _isSpellingCelebration.asStateFlow()

    private val _spellingFeedback = MutableStateFlow("Tap the letters to spell the picture!")
    val spellingFeedback: StateFlow<String> = _spellingFeedback.asStateFlow()

    private val _spellingStars = MutableStateFlow(0)
    val spellingStars: StateFlow<Int> = _spellingStars.asStateFlow()

    private val _spellingLevelFilter = MutableStateFlow(1) // 1: 3-letters, 2: 4-letters, 3: 5+ letters
    val spellingLevelFilter: StateFlow<Int> = _spellingLevelFilter.asStateFlow()

    private var onlineFetchJob: Job? = null

    init {
        val db = AppDatabase.getDatabase(application)
        repository = WordRepository(db.wordCardDao(), imageFetcher)

        favoritesList = repository.favorites.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        recentWordsList = repository.history.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        customWordsList = repository.customWords.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Initial setup
        checkFavoriteStatus("apple")
        startNewQuizRound(speakPrompt = false)
        loadExercise(SentenceTrainingCatalog.levels[0].exercises[0], speakPrompt = false)
        loadIdentifyItem(0, speakSentence = false)
        loadSpellingItem(SpellingCatalog.items[0], speakWord = false)
    }

    fun onQueryChange(newQuery: String) {
        _typedQuery.value = newQuery
        val matchedCard = repository.enrichWithLocalOfflineImage(
            PresetDictionary.findOrGenerate(newQuery)
        )
        _currentCard.value = matchedCard
        _suggestions.value = PresetDictionary.searchSuggestions(newQuery)
        checkFavoriteStatus(matchedCard.word)

        if (newQuery.isNotBlank()) {
            viewModelScope.launch {
                repository.recordWordUsage(matchedCard)
            }
        }

        // If online auto-fetch is enabled and card doesn't have an offline image yet, fetch from web in background
        if (_isAutoFetchOnlineEnabled.value && isOnline.value && !matchedCard.isDownloadedForOffline && newQuery.trim().length >= 2) {
            triggerBackgroundOnlineFetch(matchedCard)
        }
    }

    /**
     * Manually triggers fetching a web picture for the current word
     * and downloads it for permanent offline use.
     */
    fun fetchOnlineImageForCurrentCard() {
        triggerHaptic()
        val card = _currentCard.value
        triggerBackgroundOnlineFetch(card, force = true)
    }

    private fun triggerBackgroundOnlineFetch(card: WordCard, force: Boolean = false) {
        onlineFetchJob?.cancel()
        onlineFetchJob = viewModelScope.launch {
            try {
                _isFetchingOnline.value = true
                val updated = repository.fetchAndSaveOnlineImage(card)
                if (updated != null && _currentCard.value.word.equals(card.word, ignoreCase = true)) {
                    _currentCard.value = updated
                }
            } catch (_: Exception) {
            } finally {
                _isFetchingOnline.value = false
            }
        }
    }

    fun setAutoFetchOnline(enabled: Boolean) {
        triggerHaptic()
        _isAutoFetchOnlineEnabled.value = enabled
        if (enabled && isOnline.value && !_currentCard.value.isDownloadedForOffline) {
            fetchOnlineImageForCurrentCard()
        }
    }

    fun onLetterTyped(char: Char) {
        triggerHaptic()
        val updated = _typedQuery.value + char
        onQueryChange(updated)
    }

    fun onBackspaceTyped() {
        triggerHaptic()
        if (_typedQuery.value.isNotEmpty()) {
            val updated = _typedQuery.value.dropLast(1)
            onQueryChange(updated)
        }
    }

    fun onSpaceTyped() {
        triggerHaptic()
        val updated = _typedQuery.value + " "
        onQueryChange(updated)
    }

    fun onClearTyped() {
        triggerHaptic()
        onQueryChange("")
    }

    fun selectWord(card: WordCard) {
        triggerHaptic()
        val enriched = repository.enrichWithLocalOfflineImage(card)
        _typedQuery.value = enriched.word
        _currentCard.value = enriched
        _suggestions.value = PresetDictionary.searchSuggestions(enriched.word)
        checkFavoriteStatus(enriched.word)
        viewModelScope.launch {
            repository.recordWordUsage(enriched)
        }
        speakCard(enriched)

        if (_isAutoFetchOnlineEnabled.value && isOnline.value && !enriched.isDownloadedForOffline) {
            triggerBackgroundOnlineFetch(enriched)
        }
    }

    fun pickRandomWord() {
        triggerHaptic()
        val randomCard = PresetDictionary.allWords.randomOrNull() ?: return
        selectWord(randomCard)
    }

    fun selectCategory(category: WordCategory?) {
        triggerHaptic()
        _selectedCategory.value = category
        if (category != null) {
            val filtered = PresetDictionary.allWords.filter { it.category == category }
            _suggestions.value = filtered
            filtered.firstOrNull()?.let { selectWord(it) }
        } else {
            _suggestions.value = PresetDictionary.searchSuggestions(_typedQuery.value)
        }
    }

    fun speakCurrentCard() {
        triggerHaptic()
        val card = _currentCard.value
        ttsHelper.speak(card.word)
    }

    fun speakCurrentCardSlow() {
        triggerHaptic()
        val card = _currentCard.value
        ttsHelper.speak(card.word, customRate = 0.55f)
    }

    fun spellCurrentCard() {
        triggerHaptic()
        val card = _currentCard.value
        ttsHelper.spellOut(
            word = card.word,
            onLetterChange = { index -> _activeLetterHighlight.value = index },
            onFinished = { _activeLetterHighlight.value = -1 }
        )
    }

    fun speakCard(card: WordCard) {
        ttsHelper.speak(card.word)
    }

    fun toggleFavoriteCurrent() {
        triggerHaptic()
        val card = _currentCard.value
        viewModelScope.launch {
            try {
                val newStatus = repository.toggleFavorite(card)
                _isCurrentCardFavorite.value = newStatus
            } catch (_: Exception) {}
        }
    }

    private fun checkFavoriteStatus(word: String) {
        viewModelScope.launch {
            try {
                _isCurrentCardFavorite.value = repository.isWordFavorited(word)
            } catch (_: Exception) {
                _isCurrentCardFavorite.value = false
            }
        }
    }

    // Sentence Strip
    fun addToSentence(card: WordCard = _currentCard.value) {
        triggerHaptic()
        _sentenceCards.value = _sentenceCards.value + card
        ttsHelper.speak(card.word)
    }

    fun removeSentenceCard(index: Int) {
        triggerHaptic()
        if (index in _sentenceCards.value.indices) {
            val list = _sentenceCards.value.toMutableList()
            list.removeAt(index)
            _sentenceCards.value = list
        }
    }

    fun clearSentenceStrip() {
        triggerHaptic()
        _sentenceCards.value = emptyList()
        _activeSentenceWordIndex.value = -1
    }

    fun speakSentenceStrip() {
        triggerHaptic()
        val words = _sentenceCards.value.map { it.label }
        if (words.isEmpty()) return

        ttsHelper.speakSequence(
            words = words,
            onWordChange = { index -> _activeSentenceWordIndex.value = index },
            onFinished = { _activeSentenceWordIndex.value = -1 }
        )
    }

    // Custom Words
    fun saveCustomWord(card: WordCard) {
        triggerHaptic()
        viewModelScope.launch {
            try {
                repository.saveCustomWord(card)
                selectWord(card)
            } catch (_: Exception) {}
        }
    }

    fun deleteSavedWord(word: String) {
        triggerHaptic()
        viewModelScope.launch {
            try {
                repository.deleteWord(word)
            } catch (_: Exception) {}
        }
    }

    // Sensory & UI Toggles
    fun setCalmMode(enabled: Boolean) {
        triggerHaptic()
        _isCalmMode.value = enabled
    }

    fun setSpeechRate(rate: Float) {
        ttsHelper.setSpeechRate(rate)
    }

    fun setHapticEnabled(enabled: Boolean) {
        _isHapticEnabled.value = enabled
        if (enabled) triggerHaptic()
    }

    fun toggleBigKeyboard() {
        triggerHaptic()
        _isBigKeyboardOpen.value = !_isBigKeyboardOpen.value
    }

    // Quiz Game
    fun startNewQuizRound(speakPrompt: Boolean = true) {
        _quizCelebration.value = false
        val available = PresetDictionary.allWords.shuffled()
        if (available.size >= 4) {
            val target = available.first()
            val distractors = available.subList(1, 4)
            val options = (listOf(target) + distractors).shuffled()
            _quizTargetCard.value = target
            _quizOptions.value = options
            _quizFeedback.value = "Tap the picture for: ${target.label}!"
            if (speakPrompt) {
                ttsHelper.speak("Can you find: ${target.label}?")
            }
        }
    }

    fun onQuizAnswer(selected: WordCard) {
        triggerHaptic()
        val target = _quizTargetCard.value ?: return
        if (selected.word.equals(target.word, ignoreCase = true)) {
            _quizScore.value += 1
            _quizCelebration.value = true
            _quizFeedback.value = "🌟 Great job! That is ${target.label}!"
            ttsHelper.speak("Great job! ${target.label}!")
        } else {
            _quizFeedback.value = "That is ${selected.label}. Try finding ${target.label}!"
            ttsHelper.speak("That is ${selected.label}. Let us find ${target.label}!")
        }
    }

    // ==================== SENTENCE TRAINING ====================
    fun selectTrainingLevel(levelNumber: Int) {
        triggerHaptic()
        val lvl = SentenceTrainingCatalog.levels.find { it.levelNumber == levelNumber } ?: return
        _currentTrainingLevel.value = levelNumber
        _currentExerciseIndex.value = 0
        loadExercise(lvl.exercises[0], speakPrompt = true)
    }

    fun selectExercise(index: Int) {
        triggerHaptic()
        val currentLvl = SentenceTrainingCatalog.levels.find { it.levelNumber == _currentTrainingLevel.value } ?: return
        if (index in currentLvl.exercises.indices) {
            _currentExerciseIndex.value = index
            loadExercise(currentLvl.exercises[index], speakPrompt = true)
        }
    }

    fun loadExercise(exercise: SentenceExercise, speakPrompt: Boolean = true) {
        _currentExercise.value = exercise
        _trainingPlacedCards.value = List(exercise.targetWords.size) { null }
        _trainingAvailableChoices.value = SentenceTrainingCatalog.getExerciseChoices(exercise)
        _isTrainingCompleted.value = false
        _isTrainingCelebration.value = false
        _trainingFeedback.value = "Build: ${exercise.promptAudioText}"
        if (speakPrompt) {
            ttsHelper.speak("Can you build: ${exercise.promptAudioText}?")
        }
    }

    fun onTrainingCardTapped(card: WordCard) {
        triggerHaptic()
        val exercise = _currentExercise.value
        val currentSlots = _trainingPlacedCards.value.toMutableList()
        val nextEmptySlot = currentSlots.indexOfFirst { it == null }

        if (nextEmptySlot == -1) return // all filled

        val expectedWord = exercise.targetWords[nextEmptySlot]
        if (card.word.equals(expectedWord, ignoreCase = true)) {
            // Correct card!
            currentSlots[nextEmptySlot] = card
            _trainingPlacedCards.value = currentSlots

            // Remove from available choices
            _trainingAvailableChoices.value = _trainingAvailableChoices.value.filter { it.word != card.word }

            // Pronounce the word
            ttsHelper.speak(card.label)

            // Check if all slots are now filled
            if (currentSlots.all { it != null }) {
                _isTrainingCompleted.value = true
                _isTrainingCelebration.value = true
                _trainingStars.value += 1
                _trainingFeedback.value = "🌟 Great job! You built: ${exercise.promptAudioText}!"

                viewModelScope.launch {
                    delay(700)
                    ttsHelper.speak(exercise.promptAudioText)
                }
            } else {
                _trainingFeedback.value = "Good! What word comes next?"
            }
        } else {
            // Incorrect attempt - gentle sensory guidance
            _trainingFeedback.value = "That is ${card.label}. Listen again: '${exercise.promptAudioText}'"
            ttsHelper.speak("That is ${card.label}. Try again for: ${exercise.promptAudioText}")
        }
    }

    fun removePlacedTrainingCard(slotIndex: Int) {
        triggerHaptic()
        val currentSlots = _trainingPlacedCards.value.toMutableList()
        val removedCard = currentSlots[slotIndex] ?: return
        currentSlots[slotIndex] = null
        _trainingPlacedCards.value = currentSlots

        // Add back to choices if not present
        if (_trainingAvailableChoices.value.none { it.word.equals(removedCard.word, ignoreCase = true) }) {
            _trainingAvailableChoices.value = (_trainingAvailableChoices.value + removedCard).distinctBy { it.word }
        }

        _isTrainingCompleted.value = false
        _isTrainingCelebration.value = false
        _trainingFeedback.value = "Pick the next card!"
    }

    fun replayTrainingPrompt() {
        triggerHaptic()
        val exercise = _currentExercise.value
        ttsHelper.speak("Can you build: ${exercise.promptAudioText}?")
    }

    fun speakTrainingFullSentence() {
        triggerHaptic()
        val exercise = _currentExercise.value
        ttsHelper.speak(exercise.promptAudioText)
    }

    fun nextTrainingExercise() {
        triggerHaptic()
        val currentLvl = SentenceTrainingCatalog.levels.find { it.levelNumber == _currentTrainingLevel.value } ?: return
        val nextIdx = _currentExerciseIndex.value + 1
        if (nextIdx < currentLvl.exercises.size) {
            selectExercise(nextIdx)
        } else {
            // Move to next level if available
            val nextLvlNum = _currentTrainingLevel.value + 1
            if (nextLvlNum <= SentenceTrainingCatalog.levels.size) {
                selectTrainingLevel(nextLvlNum)
            } else {
                selectTrainingLevel(1)
            }
        }
    }

    fun resetTrainingExercise() {
        triggerHaptic()
        loadExercise(_currentExercise.value, speakPrompt = true)
    }

    // ==================== SENTENCE IDENTIFICATION (LISTEN & MATCH) ====================
    fun loadIdentifyItem(index: Int, speakSentence: Boolean = true) {
        if (index !in SentenceIdentifyCatalog.items.indices) return
        _currentIdentifyIndex.value = index
        val item = SentenceIdentifyCatalog.items[index]
        _currentIdentifyItem.value = item
        _identifyOptions.value = SentenceIdentifyCatalog.resolveItemOptions(item)
        _isIdentifyCelebration.value = false
        _identifyFeedback.value = "Tap the picture that matches the sentence!"

        if (speakSentence) {
            ttsHelper.speak(item.spokenSentence)
        }
    }

    fun speakCurrentIdentifySentence() {
        triggerHaptic()
        ttsHelper.speak(_currentIdentifyItem.value.spokenSentence)
    }

    fun onIdentifyAnswer(selectedCard: WordCard) {
        triggerHaptic()
        val item = _currentIdentifyItem.value
        val targetCard = SentenceIdentifyCatalog.getTargetCard(item)

        if (selectedCard.word.equals(targetCard.word, ignoreCase = true)) {
            _isIdentifyCelebration.value = true
            _identifyScore.value += 1
            _identifyFeedback.value = "⭐ ${item.explanation}"
            ttsHelper.speak("Great job! ${item.explanation}")
        } else {
            _identifyFeedback.value = "That is ${selectedCard.label}. Listen again: \"${item.spokenSentence}\""
            ttsHelper.speak("That is ${selectedCard.label}. Try again! ${item.spokenSentence}")
        }
    }

    fun nextIdentifyItem() {
        triggerHaptic()
        val nextIdx = (_currentIdentifyIndex.value + 1) % SentenceIdentifyCatalog.items.size
        loadIdentifyItem(nextIdx, speakSentence = true)
    }

    // ==================== SPELLING GAME ("SPELL THE PICTURE") ====================
    fun setSpellingLevel(level: Int) {
        triggerHaptic()
        _spellingLevelFilter.value = level
        val filtered = SpellingCatalog.items.filter { it.level == level }
        if (filtered.isNotEmpty()) {
            loadSpellingItem(filtered[0], speakWord = true)
        }
    }

    fun loadSpellingItem(item: SpellingWordItem, speakWord: Boolean = true) {
        _currentSpellingItem.value = item
        _spellingSlots.value = List(item.word.length) { null }
        _spellingTiles.value = SpellingCatalog.generateLetterBank(item.word, item.level)
        _isSpellingCelebration.value = false
        _spellingFeedback.value = "Tap the letters to spell ${item.label.uppercase()}!"
        if (speakWord) {
            ttsHelper.speak(item.label)
        }
    }

    fun onSpellingTileClick(tile: LetterBankTile) {
        if (tile.isUsed || _isSpellingCelebration.value) return
        triggerHaptic()

        // Speak the letter phonetically
        ttsHelper.speak(tile.char.toString())

        // Find the first empty slot
        val currentSlots = _spellingSlots.value.toMutableList()
        val emptyIndex = currentSlots.indexOfFirst { it == null }
        if (emptyIndex == -1) return

        currentSlots[emptyIndex] = tile.char
        _spellingSlots.value = currentSlots

        // Mark tile as used
        _spellingTiles.value = _spellingTiles.value.map {
            if (it.id == tile.id) it.copy(isUsed = true) else it
        }

        // Check if all slots are now filled
        if (!currentSlots.contains(null)) {
            val formedWord = currentSlots.map { it!! }.joinToString("")
            val targetWord = _currentSpellingItem.value.word.uppercase()
            if (formedWord.equals(targetWord, ignoreCase = true)) {
                _isSpellingCelebration.value = true
                _spellingStars.value += 1
                _spellingFeedback.value = "⭐ Awesome! You spelled $targetWord!"
                val spelledOut = targetWord.toList().joinToString(", ")
                ttsHelper.speak("$spelledOut: ${_currentSpellingItem.value.label}! Fantastic spelling!")
            } else {
                _spellingFeedback.value = "Almost! Tap a letter to fix it."
                ttsHelper.speak("Almost! Try again. ${_currentSpellingItem.value.label}")
            }
        }
    }

    fun removeSpellingSlot(index: Int) {
        val currentSlots = _spellingSlots.value.toMutableList()
        val removedChar = currentSlots.getOrNull(index) ?: return
        triggerHaptic()

        currentSlots[index] = null
        _spellingSlots.value = currentSlots
        _isSpellingCelebration.value = false
        _spellingFeedback.value = "Tap the letters to spell ${_currentSpellingItem.value.label.uppercase()}!"

        // Unmark one matching used tile in bank
        var restored = false
        _spellingTiles.value = _spellingTiles.value.map {
            if (!restored && it.isUsed && it.char == removedChar) {
                restored = true
                it.copy(isUsed = false)
            } else it
        }
    }

    fun resetSpellingWord() {
        triggerHaptic()
        loadSpellingItem(_currentSpellingItem.value, speakWord = true)
    }

    fun nextSpellingWord() {
        triggerHaptic()
        val levelItems = SpellingCatalog.items.filter { it.level == _spellingLevelFilter.value }
        val currentIdx = levelItems.indexOfFirst { it.id == _currentSpellingItem.value.id }
        val nextIdx = if (currentIdx != -1) (currentIdx + 1) % levelItems.size else 0
        loadSpellingItem(levelItems[nextIdx], speakWord = true)
    }

    fun speakCurrentSpellingWord() {
        triggerHaptic()
        ttsHelper.speak(_currentSpellingItem.value.label)
    }

    fun provideSpellingHint() {
        if (_isSpellingCelebration.value) return
        triggerHaptic()
        val target = _currentSpellingItem.value.word.uppercase()
        val currentSlots = _spellingSlots.value
        val nextIndex = currentSlots.indexOfFirst { it == null }
        if (nextIndex == -1) return

        val neededChar = target[nextIndex]
        val matchingTile = _spellingTiles.value.firstOrNull { !it.isUsed && it.char == neededChar }
        if (matchingTile != null) {
            onSpellingTileClick(matchingTile)
        }
    }

    private fun triggerHaptic() {
        if (!_isHapticEnabled.value || vibrator == null) return
        try {
            if (!vibrator.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(30)
            }
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        onlineFetchJob?.cancel()
        ttsHelper.shutdown()
    }
}
