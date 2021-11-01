package com.example.foregroundservice.STT

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData

class Stt(
    private val app: Application,
    private val manager: FragmentManager,
    private val listener: SttListener
) : SttEngine() {

    override var speechRecognizer: SpeechRecognizer? = SpeechRecognizer.createSpeechRecognizer(app)
    override var speechIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    override var audioManager: AudioManager =
        app.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override var restartSpeechHandler: Handler = Handler(Looper.getMainLooper())
    override var partialResultSpeechHandler: Handler = Handler(Looper.getMainLooper())

    override var listeningTime: Long = 0
    override var pauseAndSpeakTime: Long = 0
    override var finalSpeechResultFound: Boolean = false
    override var onReadyForSpeech: Boolean = false
    override var partialRestartActive: Boolean = false
    override var closedByUser: Boolean = false
    override var showProgressView: Boolean = false
    override var continuousSpeechRecognition: Boolean = true

    override var speechResult: MutableLiveData<String> = MutableLiveData()
    override var speechFrequency: MutableLiveData<Float> = MutableLiveData()

    override fun startSpeechRecognition() {
        TODO("Not yet implemented")
    }

    override fun restartSpeechRecognition(partialRestart: Boolean) {
        TODO("Not yet implemented")
    }

    override fun cancelSpeechOperations() {
        TODO("Not yet implemented")
    }

    override fun closeSpeechOperations() {
        TODO("Not yet implemented")
    }

    override fun mute(mute: Boolean) {
        TODO("Not yet implemented")
    }
}