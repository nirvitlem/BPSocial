package com.example.bpsocial

import android.app.Activity
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.example.bpsocial.SlaveObjectlist.cb
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

private const val TAG = "BPSocial MyBluetoothService"

// Defines several constants used when transmitting messages between the
// service and the UI.
const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2
// ... (Add other message types here as needed.)

class MyBluetoothService(private val mmSocket: BluetoothSocket) : Thread() {


        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream
        private var A : Activity?= null;
        private val time : TimersData ?= TimersData();
        private var M : HashMap<String,Long> ?=HashMap<String,Long>();


    public fun setconextintent(a : Activity)
    {
        A=a;
    }

    override fun run() {

        var numBytes: Int // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            Log.e(TAG, "mmInStream.read(mmBuffer)");
            // Read from the InputStream.
            numBytes = try {
                mmInStream.read(mmBuffer);

            } catch (e: IOException) {
                Log.d(TAG, "Input stream was disconnected", e)
                break
            }
            addtesttolist(String( mmBuffer));
            if (String( mmBuffer).contains("150874")) write("התקבלה הההודעה - 740815".toByteArray());
            if (String( mmBuffer).contains("740815")) addtesttolist(mmSocket.remoteDevice.name + " בדיקה עברה בהצלחה ")
            if (String( mmBuffer).contains("ready"))
            {
                var intent = Intent(A, Slave::class.java)
                A?.startActivity(intent)
            }

            //server
            if (String( mmBuffer).contains("Schecked"))
            {
               // time?.StopTime();
                val sec: Double ?=((Calendar.getInstance().timeInMillis.toDouble() - M?.get(mmSocket.remoteDevice.address.toString())?.toDouble()!!)/1000);
                time?.sumtime(sec!!);
                StartObjectval?.next=true;
                A?.runOnUiThread(Runnable {
                    StartObjectlist.listoftTableRow[String(mmBuffer, StandardCharsets.UTF_8).replace(0.toChar().toString(), "").split('+')[1].toInt()]?.setBackgroundColor(Color.WHITE);
                    val t= StartObjectlist.listoftTableRow[String(mmBuffer, StandardCharsets.UTF_8).replace(0.toChar().toString(), "").split('+')[1].toInt()]?.tag.toString();
                    StartObjectlist.list?.add(   " נגיעה ב- "+ t + " אחרי " + sec.toString() );
                    StartObjectlist.adapter?.notifyDataSetChanged()
                })
            }
            if (String( mmBuffer).contains("Sblue"))
            {
                M?.put(mmSocket.remoteDevice.address.toString(), Calendar.getInstance().timeInMillis!!)
            }

            //client
            if (String( mmBuffer).contains("Cblue"))
            {
                //time?.startTime();
                write("Sblue".toByteArray());
                A?.runOnUiThread(Runnable {
                    SlaveVal.bool=true;
                    SlaveObjectlist.cb?.setBackgroundColor(Color.BLUE);
                })

            }
        }

    }

        // Call this from the main activity to send data to the remote device.
        fun write(bytes: ByteArray) {
            try {
                Log.e(TAG, "mmOutStream.write(bytes)");
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)
                return
            }
        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }

         fun addtesttolist(s: String) {
            A?.runOnUiThread(Runnable { // This code will always run on the UI thread, therefore is safe to modify UI elements.
                Slist.list.add(s);
                Slist.adapter?.notifyDataSetChanged();
            })
        }

    fun addtesttotext(s: String) {
        A?.runOnUiThread(Runnable { // This code will always run on the UI thread, therefore is safe to modify UI elements.
            Slist.RText?.text=s;
        })
    }
    }
//}