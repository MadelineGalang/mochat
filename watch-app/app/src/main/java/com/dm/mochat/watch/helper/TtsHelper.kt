package com.dm.mochat.watch.helper

import android.os.Bundle
import android.speech.tts.TextToSpeech

object TtsHelper {
    fun TextToSpeech.speakThenDo(text: CharSequence, queueMode: Int, params: Bundle?, utteranceId: String, onDone: ()->Unit){
        val speechListener = object : DoneUtteranceProgressListener() {
            override fun onDone(p0: String?) {
                onDone()
            }
        }
        this.setOnUtteranceProgressListener(speechListener)
        this.speak(text, queueMode, params, utteranceId)
    }
}