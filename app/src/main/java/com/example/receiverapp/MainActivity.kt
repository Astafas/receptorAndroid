package com.example.receiverapp

import android.os.Bundle
import android.os.Debug
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.receiverapp.ui.theme.ReceiverAppTheme
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
                    Log.d("Receicved","$heartRate")
                }
            }

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ReceiverAppTheme {
        Greeting("Android")
    }
}