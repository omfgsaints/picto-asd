package com.example.data.tts

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class TextToSpeechHelper(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _speechRate = MutableStateFlow(0.85f) // Child-friendly slightly relaxed pace
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _activeSpokenIndex = MutableStateFlow(-1)
    val activeSpokenIndex: StateFlow<Int> = _activeSpokenIndex.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main)
    private var sequenceJob: Job? = null

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.w("TextToSpeechHelper", "TTS service initialization failed: ${e.message}")
            _isReady.value = false
        }
    }

    override fun onInit(status: Int) {
        try {
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setPitch(1.05f) // Friendly child pitch
                    tts?.setSpeechRate(_speechRate.value)
                    _isReady.value = true
                } else {
                    // Try device default locale
                    tts?.setLanguage(Locale.getDefault())
                    _isReady.value = true
                }

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        _activeSpokenIndex.value = -1
                    }

                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        _activeSpokenIndex.value = -1
                    }
                })
            } else {
                _isReady.value = false
            }
        } catch (e: Exception) {
            Log.w("TextToSpeechHelper", "Error in onInit: ${e.message}")
            _isReady.value = false
        }
    }

    fun setSpeechRate(rate: Float) {
        _speechRate.value = rate.coerceIn(0.4f, 1.4f)
        try {
            tts?.setSpeechRate(_speechRate.value)
        } catch (_: Exception) {}
    }

    fun speak(text: String, customRate: Float? = null) {
        if (!_isReady.value || text.isBlank() || tts == null) return
        stop()
        try {
            tts?.setSpeechRate(customRate ?: _speechRate.value)
            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "speech_${System.currentTimeMillis()}")
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "speech_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.w("TextToSpeechHelper", "Error in speak: ${e.message}")
        }
    }

    /**
     * Reads a word spelled letter-by-letter with pauses and visual letter index tracking,
     * concluding with the pronunciation of the whole word.
     */
    fun spellOut(word: String, onLetterChange: (Int) -> Unit = {}, onFinished: () -> Unit = {}) {
        if (!_isReady.value || word.isBlank() || tts == null) {
            onFinished()
            return
        }
        stop()

        sequenceJob = scope.launch {
            try {
                _isSpeaking.value = true
                val cleanWord = word.trim().uppercase()
                for (i in cleanWord.indices) {
                    val letter = cleanWord[i]
                    if (letter.isLetter()) {
                        _activeSpokenIndex.value = i
                        onLetterChange(i)
                        tts?.setSpeechRate(0.8f)
                        tts?.speak(letter.toString(), TextToSpeech.QUEUE_FLUSH, null, "letter_$i")
                        delay(550)
                    }
                }

                delay(350)
                _activeSpokenIndex.value = -1
                onLetterChange(-1)

                // Speak whole word at standard rate
                tts?.setSpeechRate(_speechRate.value)
                tts?.speak(word, TextToSpeech.QUEUE_FLUSH, null, "final_word")
                delay(800)
            } catch (e: Exception) {
                Log.w("TextToSpeechHelper", "Error in spellOut: ${e.message}")
            } finally {
                _isSpeaking.value = false
                _activeSpokenIndex.value = -1
                onLetterChange(-1)
                onFinished()
            }
        }
    }

    /**
     * Speaks an AAC sentence strip sequentially, calling onWordChange for each word index.
     */
    fun speakSequence(words: List<String>, onWordChange: (Int) -> Unit = {}, onFinished: () -> Unit = {}) {
        if (!_isReady.value || words.isEmpty() || tts == null) {
            onFinished()
            return
        }
        stop()

        sequenceJob = scope.launch {
            try {
                _isSpeaking.value = true
                for (i in words.indices) {
                    _activeSpokenIndex.value = i
                    onWordChange(i)
                    tts?.setSpeechRate(_speechRate.value)
                    tts?.speak(words[i], TextToSpeech.QUEUE_FLUSH, null, "seq_$i")
                    delay(750)
                }
                delay(400)
            } catch (e: Exception) {
                Log.w("TextToSpeechHelper", "Error in speakSequence: ${e.message}")
            } finally {
                _activeSpokenIndex.value = -1
                onWordChange(-1)
                _isSpeaking.value = false
                onFinished()
            }
        }
    }

    fun stop() {
        try {
            sequenceJob?.cancel()
            sequenceJob = null
            tts?.stop()
        } catch (_: Exception) {}
        _isSpeaking.value = false
        _activeSpokenIndex.value = -1
    }

    fun shutdown() {
        stop()
        try {
            tts?.shutdown()
        } catch (_: Exception) {}
        tts = null
        _isReady.value = false
    }
}
