package com.example.receiverapp

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import java.io.IOException
import java.io.InputStream
import java.util.UUID

class BluetoothServer(val context: Context, val textView: TextView, val stateText: TextView)  {

    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var serverSocket: BluetoothServerSocket? = null
    private var sendEmergency: Boolean = true
    private var HeartRateValues = mutableListOf<Int>()


    @SuppressLint("MissingPermission")
    fun startListening(){
        try {
            while (true) {
                serverSocket =
                    bluetoothAdapter.listenUsingRfcommWithServiceRecord("HeartRateService", uuid)
                Log.d("Bluetooth", "Esperando conexiones bluetooth...")
                val socket: BluetoothSocket =
                    serverSocket?.accept() ?: throw IOException("Socket no aceptado")
                Log.d("Bluetooth", "Conexion aceptada desde: ${socket.remoteDevice.name}")
                readData(socket.inputStream)
            }
        } catch (e: IOException){
            Log.e("Bluetooth", "Error al aceptar conexion: ${e.message}")
        }
    }

    private fun readData(inputStream: InputStream){
        val buffer = ByteArray(1024)
        var bytes: Int

        try{
            while (true){
                bytes = inputStream.read(buffer)
                val receivedData = String(buffer,0,bytes)
                val HeartRateString = receivedData.split("\n")
                Log.d("Mensaje",HeartRateString[0])
                UpdateText(HeartRateString[0])
                val heartRateVal:Int = HeartRateString[0].toInt()
                if(heartRateVal > 100){
                    if(sendEmergency) {
                        ActivateEmergency(context)
                        sendEmergency = false
                    }
                }
            }
        } catch (e: IOException){
            Log.e("Message", "Error al leer datos: ${e.message}")
        }
    }

    fun stopListening(){
        try {
            serverSocket?.close()
            Log.d("ServerStatus", "Servidor Bluetooth cerrado")
        }catch (e: IOException){
            Log.e("Message","Error al cerrar servidor: ${e.message}")
        }
    }

    private fun UpdateStateText(message: String){
        (context as Activity).runOnUiThread{
            stateText.text = message
        }
    }



    private fun UpdateText(message: String){
        (context as Activity).runOnUiThread{
            textView.text = message
        }
    }

    companion object{
        public fun ActivateEmergency(context: Context){
            (context as Activity).runOnUiThread(Runnable(){
                Log.d("Emergency", "Mensaje enviado")
                context.sendBroadcast(Intent("com.airbus.pmr.action.EMERGENCY_START"))
                Toast.makeText(context,"Emergencia activada", Toast.LENGTH_LONG).show()
            })
        }
    }

}