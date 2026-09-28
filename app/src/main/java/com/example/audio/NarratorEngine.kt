package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class NarratorEngine(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isVoiceEnabled = MutableStateFlow(true)
    val isVoiceEnabled: StateFlow<Boolean> = _isVoiceEnabled.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context, this)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {}
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {}
            })
        } catch (e: Exception) {
            Log.w("NarratorEngine", "TTS engine unavailable on this device", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS && tts != null) {
            try {
                val arLocale = Locale("ar")
                val result = tts?.setLanguage(arLocale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w("NarratorEngine", "Arabic TTS not fully supported, falling back to default locale")
                    tts?.setLanguage(Locale.getDefault())
                }
                tts?.setSpeechRate(_speechRate.value)
                tts?.setPitch(1.0f)
                isInitialized = true
            } catch (e: Exception) {
                Log.w("NarratorEngine", "Failed to configure TTS locale", e)
            }
        } else {
            Log.w("NarratorEngine", "TTS initialization failed or unavailable")
        }
    }

    fun speak(text: String, interrupt: Boolean = true) {
        if (!_isVoiceEnabled.value || !isInitialized || text.isBlank() || tts == null) return
        try {
            val queueMode = if (interrupt) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            tts?.speak(text, queueMode, null, "utterance_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.w("NarratorEngine", "Error during TTS speak", e)
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (_: Exception) {}
    }

    fun toggleVoice() {
        _isVoiceEnabled.value = !_isVoiceEnabled.value
        if (!_isVoiceEnabled.value) {
            stop()
        } else {
            speak("تم تفعيل المساعد الصوتي")
        }
    }

    fun setSpeechRate(rate: Float) {
        _speechRate.value = rate.coerceIn(0.75f, 2.0f)
        try {
            tts?.setSpeechRate(_speechRate.value)
        } catch (_: Exception) {}
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
        tts = null
    }
}
