package com.example.foregroundservice

import android.Manifest
import android.app.NotificationManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import android.content.ComponentName
import android.content.Context

import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private var mBindFlag = 0
    private val PERMISSION_REQUEST_RECORD_AUDIO = 1
    private var mServiceMessenger: Messenger? = null

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mServiceMessenger = Messenger(service)
            val msg = Message()
            msg.what = BGService.MSG_RECOGNIZER_START_LISTENING
            try {
                mServiceMessenger!!.send(msg)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mServiceMessenger = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        val serviceIntent = Intent(this@MainActivity, BGService::class.java)
        startService(serviceIntent)
        mBindFlag = Context.BIND_ABOVE_CLIENT
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
    override fun onStart() {
        super.onStart()
        bindService(Intent(this, BGService::class.java), mServiceConnection, mBindFlag)
    }

    override fun onStop() {
        super.onStop()
        if (mServiceMessenger != null) {
            unbindService(mServiceConnection)
            mServiceMessenger = null
        }
    }

}