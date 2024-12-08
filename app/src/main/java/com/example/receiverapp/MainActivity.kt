package com.example.receiverapp

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.receiverapp.BluetoothServer.Companion.ActivateEmergency
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMap
import java.io.IOException
import java.io.InputStream
import java.util.UUID

class MainActivity: AppCompatActivity(){

    private lateinit var bluetooth: BluetoothServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        enableEdgeToEdge()
        val ReceivedMessage: TextView = findViewById(R.id.heartRate)
        val stateTextView: TextView = findViewById(R.id.stateText)

        bluetooth = BluetoothServer(this,ReceivedMessage, stateTextView)

        startBluetoothReceiver()
    }

    private fun startBluetoothReceiver(){
        Thread{
            bluetooth.startListening()
        }.start()
    }


    override fun onDestroy() {
        super.onDestroy()
        bluetooth.stopListening()
    }

}
