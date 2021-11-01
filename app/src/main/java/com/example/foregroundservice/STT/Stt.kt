package com.example.foregroundservice.STT

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import java.util.*

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
    override var showProgressView: Boolean = false
    override var continuousSpeechRecognition: Boolean = true

    override var speechResult: MutableLiveData<String> = MutableLiveData()
    override var speechFrequency: MutableLiveData<Float> = MutableLiveData()

    init {
        speechIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, app.packageName)
        speechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        speechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
    }

    override fun startSpeechRecognition() {
        onReadyForSpeech = false
        if(partialRestartActive) partialRestartActive = false else speechResult.value = ""

        listeningTime = System.currentTimeMillis()
        pauseAndSpeakTime = listeningTime
        finalSpeechResultFound = false

        speechRecognizer?.setRecognitionListener(object: RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onBeginningOfSpeech() {
                TODO("Not yet implemented")
            }

            override fun onRmsChanged(p0: Float) {
                TODO("Not yet implemented")
            }

            override fun onBufferReceived(p0: ByteArray?) {
                TODO("Not yet implemented")
            }

            override fun onEndOfSpeech() {
                TODO("Not yet implemented")
            }

            override fun onError(p0: Int) {
                TODO("Not yet implemented")
            }

            override fun onResults(p0: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onPartialResults(p0: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onEvent(p0: Int, p1: Bundle?) {
                TODO("Not yet implemented")
            }

        })
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