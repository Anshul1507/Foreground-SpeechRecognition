package com.example.foregroundservice.STT

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import com.example.foregroundservice.R
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
        if (partialRestartActive) partialRestartActive = false else speechResult.value = ""

        listeningTime = System.currentTimeMillis()
        pauseAndSpeakTime = listeningTime
        finalSpeechResultFound = false

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {
                onReadyForSpeech = true
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                restartSpeechRecognition(true) //Restart Speech after end of speech
            }

            override fun onError(errorCode: Int) {
                val errDuration = System.currentTimeMillis() - listeningTime
                if (errDuration < 5000L && errorCode == SpeechRecognizer.ERROR_NO_MATCH && !onReadyForSpeech) return

                // Disabling/Enabling audio based on "audio beep disabled timeout",
                mute(onReadyForSpeech && errDuration < 30000L)

                if (arrayOf(
                        SpeechRecognizer.ERROR_NO_MATCH,
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT,
                        SpeechRecognizer.ERROR_AUDIO,
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY
                    ).any { it == errorCode }
                ) {
                    // Restarting speech recognition
                    restartSpeechRecognition(errorCode == SpeechRecognizer.ERROR_RECOGNIZER_BUSY)
                } else {
                    listener.onSttSpeechError(getErrorTextFromCode(errorCode))

                    // Resetting on ready for speech status
                    onReadyForSpeech = false

                    // Closing speech operations
                    closeSpeechOperations()
                }
            }

            override fun onResults(results: Bundle?) {
                val result = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0]
                Log.d("test", "result: $result")
            }

            override fun onPartialResults(partialResults: Bundle?) {
                if (finalSpeechResultFound) return
                val partialResult =
                    partialResults!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0]
                // Sending an update with the live speech result
                listener.onSttLiveSpeechResult(partialResult)
                // Updating the speech observer with the live result
                speechResult.value = partialResult

                if ((System.currentTimeMillis() - pauseAndSpeakTime) > 1000L) {
                    // Final Speech result found
                    finalSpeechResultFound = true

                    partialResultSpeechHandler.postDelayed({
                        closeSpeechOperations()

                        listener.onSttFinalSpeechResult(partialResult)

                        startSpeechRecognition() //starting speech recognition, for continuous speech recog.

                    }, 500L)
                } else {
                    pauseAndSpeakTime = System.currentTimeMillis()
                }
            }

            override fun onEvent(p0: Int, p1: Bundle?) {}

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

    fun getErrorTextFromCode(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Didn't understand, please try again."
        }
    }
}