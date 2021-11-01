package com.example.foregroundservice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foregroundservice.STT.Stt
import com.example.foregroundservice.STT.SttListener


class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var stt: Stt
    }

    private val PERMISSION_REQUEST_RECORD_AUDIO = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setup stt engine
        initSttEngine()

        // check for permission
        val permissionCheck =
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQUEST_RECORD_AUDIO
            )
            return
        }


    }

    private fun initSttEngine() {
        stt = Stt(application, supportFragmentManager, object : SttListener {
            override fun onSttLiveSpeechResult(liveSpeechResult: String) {
                Log.d(application.packageName, "Speech result - $liveSpeechResult")
            }

            override fun onSttFinalSpeechResult(speechResult: String) {
                Log.d(application.packageName, "Final speech result - $speechResult")
            }

            override fun onSttSpeechError(errMsg: String) {
                Log.d(application.packageName, "Speech error - $errMsg")
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_RECORD_AUDIO) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val serviceIntent = Intent(this@MainActivity, BGService::class.java)
                startService(serviceIntent)
            } else {
                Toast.makeText(
                    this, "Permission Denied!", Toast
                        .LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

}