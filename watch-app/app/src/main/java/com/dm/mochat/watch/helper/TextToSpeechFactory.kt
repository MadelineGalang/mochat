package com.dm.mochat.watch.helper

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

object TextToSpeechFactory {
    private lateinit var _tts: TextToSpeech
    val instance: TextToSpeech
        get() {
            return _tts
        }

    fun configure(context: Context){
        _tts = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                _tts.language = Locale.US
            }
        }
    }


}