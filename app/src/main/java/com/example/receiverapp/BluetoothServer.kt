package com.example.receiverapp

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import android.widget.TextView
import java.io.IOException
import java.io.InputStream
import java.util.UUID

class BluetoothServer(val context: Context, val textView: TextView)  {

    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var serverSocket: BluetoothServerSocket? = null

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
                Log.d("Mensaje",receivedData)
                UpdateText(receivedData)
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

    private fun UpdateText(message: String){
        (context as Activity).runOnUiThread{
            textView.text = message
        }
    }

}