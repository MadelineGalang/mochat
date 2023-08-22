package com.dm.mochat.watch.helper

import android.speech.tts.UtteranceProgressListener

abstract class DoneUtteranceProgressListener : UtteranceProgressListener() {
    override fun onStart(p0: String?) {}

    override fun onError(p0: String?) {}
}