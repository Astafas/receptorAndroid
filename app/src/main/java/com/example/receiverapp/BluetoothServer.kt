package com.example.receiverapp

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.InputStream
import java.util.UUID

class BluetoothServer(val context: Context, val textView: TextView, val stateTExt: TextView)  {

    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var serverSocket: BluetoothServerSocket? = null
    private var sendEmergency: Boolean = true
    private var activatePTT: Boolean = true
    private var heart_rate_prom = 0
    private var cont: Int = 0


    @SuppressLint("MissingPermission")
    fun startListening(){
        UpdateState("Estado: Normal",R.color.healthy)
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
                val heartRateVal:Int = HeartRateString[0].toInt()
                if(heartRateVal >= 0)
                    UpdateText(HeartRateString[0])

                if(heartRateVal == -10 && activatePTT){
                    ActivatePTT()
                    activatePTT = false
                }
                else if(heartRateVal == -15){
                    deactivatePTT()
                    activatePTT = true
                }

                if(cont<20 && heartRateVal > 0) {
                    cont++
                    heart_rate_prom += heartRateVal
                }
                else if(cont >= 20){
                    cont = 0
                    heart_rate_prom /= 20
                    Log.d("Promedio","Promedio: $heart_rate_prom")
                    heart_rate_prom = 0
                }

                when{
                    heartRateVal > 120 && sendEmergency->{
                        UpdateState("Estado: Emergencia",R.color.emergency)
                        ActivateEmergency(context)
                        sendEmergency = false
                    }
                    heart_rate_prom in 61..100 && !sendEmergency ->{
                        UpdateState("Estado: Normal",R.color.healthy)
                        sendEmergency = true
                    }
                    heartRateVal in 10..60 && sendEmergency ->{
                        UpdateState("Estado: Emergencia",R.color.emergency)
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

    private fun ActivatePTT(){
        (context as Activity).runOnUiThread(Runnable() {
            context.sendBroadcast(Intent("com.airbus.pmr.action.PTT_START"))
            Log.d("PTT", "PTT Activado")
            Toast.makeText(context, "PTT Activado", Toast.LENGTH_SHORT).show()
        })
    }

    private fun deactivatePTT(){
        (context as Activity).runOnUiThread(Runnable() {
            context.sendBroadcast(Intent("com.airbus.pmr.action.PTT_STOP"))
            Log.d("PTT", "PTT Desactivado")
            Toast.makeText(context, "PTT Desactivado", Toast.LENGTH_SHORT).show()
        })
    }

    private fun UpdateState(message: String, colorId: Int){
        (context as Activity).runOnUiThread{
            stateTExt.setBackgroundColor(ContextCompat.getColor(context,colorId))
            stateTExt.text = message
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