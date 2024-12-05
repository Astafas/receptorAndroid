package com.example.receiverapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMap

class MainActivity : ComponentActivity(), DataClient.OnDataChangedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_activity)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for(event in dataEvents){
            if(event.type == DataEvent.TYPE_CHANGED){
                val dataItem = event.dataItem
                if(dataItem.uri.path == "/heart_rate"){
                    val dataMap = dataItem.data?.let { DataMap.fromByteArray(it) }
                    val heartRate = dataMap?.getFloat("heart_rate")
                    val showHeartRate = findViewById<TextView>(R.id.heartRate)
                    showHeartRate.text = heartRate.toString()
                    Log.d("Receicved","$heartRate")
                }
            }

        }
    }

}