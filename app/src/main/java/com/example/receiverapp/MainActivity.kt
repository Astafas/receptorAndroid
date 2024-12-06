package com.example.receiverapp

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMap
import java.io.IOException
import java.io.InputStream
import java.util.UUID

class MainActivity: ComponentActivity(){

    private lateinit var bluetooth: BluetoothServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_activity)
        val ReceivedMessage: TextView = findViewById(R.id.heartRate)

        bluetooth = BluetoothServer(this,ReceivedMessage)

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
