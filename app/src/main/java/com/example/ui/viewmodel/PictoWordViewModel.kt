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
import com.example.data.model.WordCard
import com.example.data.model.WordCategory
import com.example.data.network.NetworkMonitor
import com.example.data.network.OnlineImageFetcher
import com.example.data.repository.WordRepository
import com.example.data.tts.TextToSpeechHelper
import kotlinx.coroutines.Job
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
